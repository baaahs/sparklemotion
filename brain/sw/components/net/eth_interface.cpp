//
// Created by Tom Seago on 2019-08-05.
//

#include <esp_event.h>
#include "eth_interface.h"
#include "net_priv.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_err.h"
#include "esp_log.h"
#include "tcpip_adapter.h"
#include "sdkconfig.h"

////////////////////////////////////////////////////////////
// Glue functions that let us use C++ functions with the IDF
// C apis.

static void glue_evtHandler(void* pArg, esp_event_base_t evBase, int32_t evId, void* evData) {
    ((EthInterface *) pArg)->_evtHandler(evBase, evId, evData);
}

////////////////////////////////////////////////////////////

bool EthInterface::init() {
    if (!NetInterface::init()) return false;

    ESP_LOGD(TAG, "EthInterface init begin");

    if (ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_set_default_eth_handlers()) != ESP_OK) return false;
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(ETH_EVENT, ESP_EVENT_ANY_ID, glue_evtHandler, this)) != ESP_OK) return false;
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(IP_EVENT, IP_EVENT_ETH_GOT_IP, glue_evtHandler, this)) != ESP_OK) return false;

    eth_mac_config_t macConfig = ETH_MAC_DEFAULT_CONFIG();
    macConfig.sw_reset_timeout_ms = 300;
    esp_eth_mac_t *mac = esp_eth_mac_new_esp32(&macConfig);

    eth_phy_config_t phyConfig = ETH_PHY_DEFAULT_CONFIG();

    // Because the default example is assuming one of those 8720 breakout boards that
    // has the address set to 1 we have to be sure to set it to 0
    phyConfig.phy_addr = 0;

    esp_eth_phy_t *phy = esp_eth_phy_new_lan8720(&phyConfig);

    m_config = ETH_DEFAULT_CONFIG(mac, phy);

    ESP_LOGD(TAG, "EthInterface init done");

    return true;
}

void EthInterface::enableChanged() {
    if (m_isEnabled) {
        // The esp_eth_driver_install call will send the START event, which the
        // tcpip adapter will be listening to as long as the default eth handlers
        // have been set. When the tcpip adapter sees the start event, it looks at
        // the ip configuration it has for the eth driver and uses that to configure
        // it's eth interface.
        if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_eth_driver_install(&m_config, &m_hDriver)) != ESP_OK) {
            ESP_LOGE(TAG, "EthInterface attempt to enable failed");
            m_isEnabled = false;
        }
    } else {
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_eth_driver_uninstall(&m_hDriver));
    }
}

void EthInterface::addressingChanged() {
    if (m_dhcpEnabled) {
        if (ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_dhcpc_start(TCPIP_ADAPTER_IF_ETH)) != ESP_OK) {
            m_dhcpEnabled = false;
        }
    } else {
        // Stop DHCP first or else you can't set the static ip
        ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_dhcpc_stop(TCPIP_ADAPTER_IF_ETH));

        // Now set the info. Hopefully it's already been configured properly eh?
        ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_set_ip_info(TCPIP_ADAPTER_IF_ETH, &m_staticIp));
    }
}

void
EthInterface::_evtHandler(esp_event_base_t evBase, int32_t evId, void *evData) {
    if (evBase == ETH_EVENT) {
        switch (evId) {
            case ETHERNET_EVENT_CONNECTED:
                tellListenerIntLinkUp();
                break;
            case ETHERNET_EVENT_DISCONNECTED:
                tellListenerLinkDown();
                break;
            case ETHERNET_EVENT_START:
                tellListenerStart();
                break;
            case ETHERNET_EVENT_STOP:
                tellListenerStop();
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
