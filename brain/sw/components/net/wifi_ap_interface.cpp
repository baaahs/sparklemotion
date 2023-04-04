//
// Created by Tom Seago on 2019-08-07.
//

#include "wifi_ap_interface.h"
#include <string.h>

// For MACSTR
#include "rom/ets_sys.h"

#define TAG TAG_NET

////////////////////////////////////////////////////////////
// Glue functions that let us use C++ functions with the IDF
// C apis.

static void glue_evtHandler(void* pArg, esp_event_base_t evBase, int32_t evId, void* evData) {
    ((WifiApInterface *) pArg)->_evtHandler(evBase, evId, evData);
}

////////////////////////////////////////////////////////////

bool WifiApInterface::init() {
    if (!NetInterface::init()) return false;

    ESP_LOGD(TAG, "WifiApInterface init start");

    // This is probably not cool to call multiple times, but there shouldn't be
    // multiple instances of this class now should there!
    m_netIf = esp_netif_create_default_wifi_ap();

    // Since it's not cool to call esp_wifi_init() more than once we use get_mode
    // as a check to see if we need to init or not.
    wifi_mode_t mode;
    auto modeGetResult = esp_wifi_get_mode(&mode);
    if (modeGetResult == ESP_ERR_WIFI_NOT_INIT) {
        // We can fix this!
        wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
        cfg.nvs_enable = false;
        // This will call the tcpip_adapter init as well as the supplicant init
        if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_init(&cfg)) != ESP_OK) return false;
    }

    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID, glue_evtHandler, this)) != ESP_OK) return false;
    // There is also a LOST_IP event

    // Set our static IP address
    m_staticIpInfo.ip.addr = 0x0A0A0A0A; // 10.10.10.10
    m_staticIpInfo.netmask.addr = 0x0AFFFFFF; // 255.255.255.10 - because various bits of the esp code assume this. Also, LSB not FTW :(
    m_staticIpInfo.gw.addr = 0x0A0A0A0A;
    m_dhcpEnabled = false;


    ESP_LOGD(TAG, "WifiApInterface init done");

    return true;
}

void WifiApInterface::enableChanged() {
    if (m_isEnabled) {
        // Get the existing mode and add STA to it if necessary
        wifi_mode_t mode;
        auto result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_get_mode(&mode));
        if (result != ESP_OK) {
            m_isEnabled = false;
            return;
        }

        if (mode != WIFI_MODE_AP && mode != WIFI_MODE_APSTA) {
            if (mode == WIFI_MODE_STA) {
                mode = WIFI_MODE_APSTA;
            } else {
                mode = WIFI_MODE_AP;
            }

            result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_set_mode(mode));
            if (result != ESP_OK) {
                m_isEnabled = false;
                return;
            }
        }

        // Always setup our config here because it's pretty darn static. We might want
        // to make something like the IP range a configurable value in the future. For now
        // don't worry about it.
        // Since set_config takes a type of config to set I don't think we are in any danger
        // over overwriting the STA config. So in other words we can just set our values
        // without first fetching existing ones.
        m_wifiConfig = {};
        uint8_t* mac = GlobalConfig.mac();
        snprintf((char*)m_wifiConfig.ap.ssid, sizeof(m_wifiConfig.ap.ssid)-1, "brain_%2x%2x%2x", mac[3], mac[4], mac[5]);
        m_wifiConfig.ap.ssid_len = strlen((char*)m_wifiConfig.ap.ssid);
        m_wifiConfig.ap.authmode = WIFI_AUTH_OPEN;

        // See how this is really small? That's because each connection uses a good amount of resources.
        // In other words, can't use an ESP32 as a general gateway ;)
        m_wifiConfig.ap.max_connection = 4;

        result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_set_config(static_cast<wifi_interface_t>(ESP_IF_WIFI_AP), &m_wifiConfig));
        if (result != ESP_OK) {
            m_isEnabled = false;
            return;
        }

//        ESP_LOGI(TAG, "Connecting to SSID:%s PASS:%s", m_wifiConfig.sta.ssid, m_wifiConfig.sta.password);
//
        result = ESP_ERROR_CHECK_WITHOUT_ABORT(esp_wifi_start());
        if (result != ESP_OK) {
            m_isEnabled = false;
            return;
        }

        // I think we also need to set the static info after the start?
        ESP_LOGI(TAG, "Calling addressingChanged to set the static IP info after the wifi start call");
        addressingChanged();

    } else {
        // TODO: Implement this better
        ESP_LOGE(TAG, "Disabling wifi probably isn't really implemented properly yet");
        esp_wifi_stop();
    }
}

void WifiApInterface::addressingChanged() {
    if (m_dhcpEnabled) {
//        if (ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_dhcpc_start(TCPIP_ADAPTER_IF_STA)) != ESP_OK) {
//            m_dhcpEnabled = false;
//        }
        ESP_LOGE(TAG, "Can not enable DHCP on the AP interface. Ignoring");
        m_dhcpEnabled = false;
    } else {
        esp_netif_dhcp_status_t status;
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_dhcps_get_status(m_netIf, &status));
        ESP_LOGI(TAG, "AP dhcps status = %d", status);

        // Stop DHCP server first or else you can't set the static ip
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_dhcps_stop(m_netIf));

        // Now set the info. Hopefully it's already been configured properly eh?
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_set_ip_info(m_netIf, &m_staticIpInfo));

        // Now off course you need to restart that server
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_dhcps_start(m_netIf));
    }
}

void
WifiApInterface::_evtHandler(esp_event_base_t evBase, int32_t evId, void *evData) {
    if (evBase == WIFI_EVENT) {
        switch (evId) {
            case WIFI_EVENT_AP_START:
                tellListenerStart();

                // Do we need to start the dhcp server or does that happen automatically?

                break;

            case WIFI_EVENT_AP_STOP:
                tellListenerStop();
                break;

            case WIFI_EVENT_AP_PROBEREQRECVED:
                ESP_LOGI(TAG, "AP probe req received");
                break;

            case WIFI_EVENT_AP_STACONNECTED: {
                    wifi_event_ap_staconnected_t *event = (wifi_event_ap_staconnected_t *) evData;
                    ESP_LOGI(TAG, "Station connected to AP " MACSTR " join, AID=%d",
                             MAC2STR(event->mac), event->aid);
                }
                break;

            case WIFI_EVENT_AP_STADISCONNECTED: {
                    wifi_event_ap_stadisconnected_t *event = (wifi_event_ap_stadisconnected_t *) evData;
                    ESP_LOGI(TAG, "Station disconnected from AP " MACSTR " leave, AID=%d",
                             MAC2STR(event->mac), event->aid);
                }
                break;

//            case WIFI_EVENT_AP_DISCONNECTED:
//                tellListenerLinkDown();
//
//                m_numFailedConnects++;
//                // TODO: Add a maximum number of retries.
//                // The connect method itself should have take some time so it's "okay" to
//                // simply reconnect immediately. However, after some max number of retries
//                // we should probably backoff and add a non-trivial (30 seconds?) delay
//                // before we try to connect again so that we aren't always hammering the
//                // connect process. I think it will slow down a board more than we would like.
//
//                ESP_LOGE(TAG, "Failed to connect to wifi ssid '%s', Failure #%d. Retrying.", m_wifiConfig.sta.ssid, m_numFailedConnects);
//                esp_wifi_connect();
//                break;
//
//            case WIFI_EVENT_STA_CONNECTED:
//                m_numFailedConnects = 0;
//
//                tellListenerIntLinkUp();
//                break;

            default:
                break;
        }
//    } else if (evBase == IP_EVENT) {
//        ip_event_got_ip_t *event = (ip_event_got_ip_t *) evData;
//        const tcpip_adapter_ip_info_t *ip_info = &event->ip_info;
//
//        tellListenerGotAddr(ip_info);
    }
}
