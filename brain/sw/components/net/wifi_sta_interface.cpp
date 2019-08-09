//
// Created by Tom Seago on 2019-08-05.
//

#include "wifi_sta_interface.h"
#include <string.h>

#define TAG TAG_NET

////////////////////////////////////////////////////////////
// Glue functions that let us use C++ functions with the IDF
// C apis.

static void glue_evtHandler(void* pArg, esp_event_base_t evBase, int32_t evId, void* evData) {
    ((WifiStaInterface *) pArg)->_evtHandler(evBase, evId, evData);
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
        result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_set_config(ESP_IF_WIFI_STA, &m_wifiConfig));
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
        if (ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_dhcpc_start(TCPIP_ADAPTER_IF_STA)) != ESP_OK) {
            m_dhcpEnabled = false;
        }
    } else {
        // Stop DHCP first or else you can't set the static ip
        ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_dhcpc_stop(TCPIP_ADAPTER_IF_STA));

        // Now set the info. Hopefully it's already been configured properly eh?
        ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_set_ip_info(TCPIP_ADAPTER_IF_STA, &m_staticIp));
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
                esp_wifi_connect();
                break;

            case WIFI_EVENT_STA_STOP:
                tellListenerStop();
                break;

            case WIFI_EVENT_STA_DISCONNECTED:
                tellListenerLinkDown();

                m_numFailedConnects++;
                ESP_LOGE(TAG, "Failed to connect to wifi ssid '%s', Failure #%d. Retrying.", m_wifiConfig.sta.ssid, m_numFailedConnects);
                if (m_numFailedConnects < 2) {
                    ESP_LOGE(TAG, "Will try again...");
                    esp_wifi_connect();
                } else {
                    ESP_LOGE(TAG, "That's it. No more trying. Need some better credentials");
                }
                break;

            case WIFI_EVENT_STA_CONNECTED:
                m_numFailedConnects = 0;

                tellListenerIntLinkUp();
                break;

            default:
                break;
        }
    } else if (evBase == IP_EVENT) {
        ip_event_got_ip_t *event = (ip_event_got_ip_t *) evData;
        const tcpip_adapter_ip_info_t *ip_info = &event->ip_info;

        tellListenerGotAddr(ip_info);
    }
}
