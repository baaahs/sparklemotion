//
// Created by Tom Seago on 2019-08-05.
//

#include "wifi_sta_interface.h"
#include <string.h>
#include <freertos/timers.h>

#define TAG TAG_NET

#define MAX_IMMEDIATE_RECONNECTS        2
#define LONG_TERM_POLL_INTERVAL_SECS    30

////////////////////////////////////////////////////////////
// Glue functions that let us use C++ functions with the IDF
// C apis.

static void glue_evtHandler(void* pArg, esp_event_base_t evBase, int32_t evId, void* evData) {
    ((WifiStaInterface *) pArg)->_evtHandler(evBase, evId, evData);
}

static void glue_longPoll(void* pArg) {
    ((WifiStaInterface*)pArg)->_taskLongPoll();
}

////////////////////////////////////////////////////////////

void
WifiStaInterface::setCredentials(const char* ssid, const char* password) {
    bool wasEnabled = m_isEnabled;

    if (m_isEnabled) {
        setEnabled(false);
    }

    strncpy((char*)m_wifiConfig.sta.ssid, ssid, sizeof(m_wifiConfig.sta.ssid) - 1);
    strncpy((char*)m_wifiConfig.sta.password, password, sizeof(m_wifiConfig.sta.ssid) - 1);

    if (wasEnabled) {
        setEnabled(true);
    }
}


bool WifiStaInterface::init() {
    if (!NetInterface::init()) return false;

    ESP_LOGD(TAG, "WifiStaInterface init start");

    //Initialize NVS
    // TODo: move nvs init to net transport

    // Because of a bug in the IDF as of July 3, we ALWAYS erase the flash right now. This seems to be
    // related to refactorings where they moved the wap supplicant code around and probably didn't
    // update nvs_flash_init properly.
    // TODO: Try to remove this sometime after about July 7 or so. Test with changing wifi credentials
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(nvs_flash_erase()) != ESP_OK) return false;

    ESP_LOGI(TAG, "calling nvs_flash_init()");
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        ESP_ERROR_CHECK_WITHOUT_ABORT(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(ret) != ESP_OK) return false;

    //////////////////////

    // Change at 4.1 - we must create the default STA interface dynamically.
    // Previously it was static.
    m_netIf = esp_netif_create_default_wifi_sta();


    // TODO: protect init as per AP
    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    // This will call the tcpip_adapter init as well as the supplicant init
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_init(&cfg)) != ESP_OK) return false;

    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID, glue_evtHandler, this)) != ESP_OK) return false;
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, glue_evtHandler, this)) != ESP_OK) return false;
    // There is also a LOST_IP event

    ESP_LOGD(TAG, "WifiStaInterface init done");

    return true;
}

void WifiStaInterface::enableChanged() {
    if (m_isEnabled) {
        // Get the existing mode and add STA to it if necessary
        wifi_mode_t mode;
        auto result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_get_mode(&mode));
        if (result != ESP_OK) {
            m_isEnabled = false;
            return;
        }

        if (mode != WIFI_MODE_STA && mode != WIFI_MODE_APSTA) {
            if (mode == WIFI_MODE_AP) {
                mode = WIFI_MODE_APSTA;
            } else {
                mode = WIFI_MODE_STA;
            }

            result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_set_mode(mode));
            if (result != ESP_OK) {
                m_isEnabled = false;
                return;
            }
        }

        // Presume that the credentials have been set
        result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_set_config(static_cast<wifi_interface_t>(ESP_IF_WIFI_STA), &m_wifiConfig));
        if (result != ESP_OK) {
            m_isEnabled = false;
            return;
        }

        ESP_LOGI(TAG, "Connecting to SSID:%s PASS:%s", m_wifiConfig.sta.ssid, m_wifiConfig.sta.password);

        result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_start());
        if (result != ESP_OK) {
            m_isEnabled = false;
            return;
        }
    } else {
        // TODO: Implement this better
        ESP_LOGE(TAG, "Disabling wifi probably isn't really implemented properly yet");
        esp_wifi_stop();
    }
}

void WifiStaInterface::addressingChanged() {
    if (m_dhcpEnabled) {
        if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_dhcpc_start(m_netIf)) != ESP_OK) {
            m_dhcpEnabled = false;
        }
    } else {
        // Stop DHCP first or else you can't set the static ip
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_dhcpc_stop(m_netIf));

        // Now set the info. Hopefully it's already been configured properly eh?
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_set_ip_info(m_netIf, &m_staticIpInfo));
    }
}

void
WifiStaInterface::_evtHandler(esp_event_base_t evBase, int32_t evId, void *evData) {
    if (evBase == WIFI_EVENT) {
        switch (evId) {
            case WIFI_EVENT_STA_START:
                tellListenerStart();

                // Attempt to connect
                m_numFailedConnects = 0;
                m_connected = false;
                m_started = true;
                esp_wifi_connect();
                break;

            case WIFI_EVENT_STA_STOP:
                m_connected = false;
                m_started = false;
                tellListenerStop();
                break;

            case WIFI_EVENT_STA_DISCONNECTED:
                m_connected = false;
                tellListenerLinkDown();

                m_numFailedConnects++;
                ESP_LOGE(TAG, "Failed to connect to wifi ssid '%s', Failure #%d. Retrying.", m_wifiConfig.sta.ssid, m_numFailedConnects);
                if (m_numFailedConnects < MAX_IMMEDIATE_RECONNECTS) {
                    ESP_LOGE(TAG, "Will try again...");
                    esp_wifi_connect();
                } else {
                    if (!m_haveLTPTask) {
                        ESP_LOGE(TAG, "That's it. No more immediate retries. Need some better credentials");
                        ESP_LOGW(TAG, "Starting the long term poll task.");
                        startLongTermPoll();
                    } else {
                        ESP_LOGW(TAG, "Wifi long term poll task is running. It will manage the connect attempt");
                    }
                }
                break;

            case WIFI_EVENT_STA_CONNECTED:
                m_numFailedConnects = 0;
                m_connected = true;

                tellListenerIntLinkUp();
                break;

            default:
                break;
        }
    } else if (evBase == IP_EVENT) {
        ip_event_got_ip_t *event = (ip_event_got_ip_t *) evData;
        esp_netif_ip_info_t *ip_info = &event->ip_info;

        tellListenerGotAddr(ip_info);
    }
}

void WifiStaInterface::startLongTermPoll() {

    if (m_haveLTPTask) {
        ESP_LOGE(TAG, "Attempting to start LTP task when it already exists (supposedly). Not starting");
        return;
    }

    auto def = DefaultBrainTasks.longTermSTAPoll;
    auto tcResult =def.createTask(glue_longPoll, this, &m_hLTPTask);
    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create long term poll task %d", tcResult);
        return;
    }
    m_haveLTPTask = true;
}

void WifiStaInterface::_taskLongPoll() {

    m_haveLTPTask = false;

    // m_connected and m_started should really be semaphores but whatever

    while(true) {
        vTaskDelay(pdMS_TO_TICKS(LONG_TERM_POLL_INTERVAL_SECS * 1000));

        if (m_connected) {
            ESP_LOGW(TAG, "Wifi STA long poll task expired and we are connected. Exiting task");
            break;
        }

        if (!m_started) {
            ESP_LOGW(TAG, "Wifi STA long poll task, not started. Exiting");
            break;
        }

        // We are started but not connected

        // We can go ahead and start a connect attempt
        ESP_LOGW(TAG, "Wifi STA long poll task starting a connect to '%s', Failure #%d.", m_wifiConfig.sta.ssid, m_numFailedConnects);
        esp_wifi_connect();

        // Just loop, which will be some sort of long delay.
    }

    // Clean up when we exit
    m_haveLTPTask = false;
    vTaskDelete(nullptr);
}
