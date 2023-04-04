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
#include "esp_netif.h"
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

    // Now handled by driver installation
//    if (ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_set_default_eth_handlers()) != ESP_OK) return false;
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(ETH_EVENT, ESP_EVENT_ANY_ID, glue_evtHandler, this)) != ESP_OK) return false;
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(IP_EVENT, IP_EVENT_ETH_GOT_IP, glue_evtHandler, this)) != ESP_OK) return false;

    // Don't leave the physical config up to the menuconfig stuff. Misconfiguration from there
    // is super frustrating AND a lot of this is dictated by hardware constraints. Sure,
    // maybe this doesn't work on some other hardware that uses an SPI based interface
    // to the PHY. Don't care.
    //
    // Strongly recommend the use of the "basic" ethernet example to debug these settings
    // if that ever comes up again!

    eth_mac_config_t macConfig = ETH_MAC_DEFAULT_CONFIG();
    // Docs say this should be 100 typically but I think we wanted to be more liberal for some reason???
//    macConfig.sw_reset_timeout_ms = 1000;

    eth_esp32_emac_config_t emacConfig = ETH_ESP32_EMAC_DEFAULT_CONFIG();
    emacConfig.smi_mdio_gpio_num = GPIO_NUM_18;
    emacConfig.smi_mdc_gpio_num = GPIO_NUM_23;
    emacConfig.clock_config.rmii.clock_mode = EMAC_CLK_OUT;
    emacConfig.clock_config.rmii.clock_gpio = EMAC_CLK_OUT_180_GPIO;
    esp_eth_mac_t *mac = esp_eth_mac_new_esp32(&emacConfig, &macConfig);

    eth_phy_config_t phyConfig = ETH_PHY_DEFAULT_CONFIG();
//    phyConfig.reset_timeout_ms = 1000;

    // Because the default example is assuming one of those 8720 breakout boards that
    // has the address set to 1 we have to be sure to set it to 0
    phyConfig.phy_addr = 0;

    // It's also also a somewhat newer thing that we can set the nRst pin
    // on the built-in driver - which is neat!
    phyConfig.reset_gpio_num = GPIO_NUM_15;

    esp_eth_phy_t *phy = esp_eth_phy_new_lan87xx(&phyConfig);

    // Now we need to install the actual Ethernet driver using the above
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
            return;
        }

        // This seems like a thing we should do per the Flow Control section in
        // https://docs.espressif.com/projects/esp-idf/en/v5.0.1/esp32/api-reference/network/esp_eth.html#
        bool flow_ctrl_enable = true;
        esp_eth_ioctl(m_hDriver, ETH_CMD_S_FLOW_CTRL, &flow_ctrl_enable);

        // We assume that esp_netif_init() has already been called. However we do need to go ahead
        // and glue the netif to the driver we just installed
        esp_netif_config_t cfg = ESP_NETIF_DEFAULT_ETH();
        m_netIf = esp_netif_new(&cfg);
        if (m_netIf == NULL) {
            ESP_LOGE(TAG, "Failed to create ethernet netif");
            esp_eth_driver_uninstall(m_hDriver);
            m_isEnabled = false;
            return;
        }

        // I'm not entirely sure how we would detach so enabling and disabling over time
        // is likely to leak memory...
        if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_netif_attach(m_netIf, esp_eth_new_netif_glue(m_hDriver))) != ESP_OK) {
            ESP_LOGE(TAG, "Ethernet netif could not be attached");
            esp_eth_driver_uninstall(m_hDriver);
            m_isEnabled = false;
            return;
        }

        // We already registered the IP_EVENT handler in the constructor
        // I'm not 100% sure we need to call start but it seems reasonable to do here
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_eth_start(m_hDriver));
        ESP_LOGI(TAG, "EthInterface attached");
    } else {
        esp_eth_stop(m_hDriver);
        ESP_ERROR_CHECK_WITHOUT_ABORT(esp_eth_driver_uninstall(&m_hDriver));
    }
}

void EthInterface::addressingChanged() {
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
        esp_netif_ip_info_t *ip_info = &event->ip_info;

        tellListenerGotAddr(ip_info);
    }
}
