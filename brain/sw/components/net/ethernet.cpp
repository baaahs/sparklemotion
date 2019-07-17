#include "ethernet.h"
#include "net_priv.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_err.h"
#include "esp_log.h"
#include "tcpip_adapter.h"
#include "sdkconfig.h"

/** Event handler for Ethernet events */
void glue_ethHandler(void* pArg, esp_event_base_t evBase, int32_t evId, void* evData)
{
    ((Ethernet *) pArg)->_ethHandler(evBase, evId, evData);
}

/** Event handler for IP_EVENT_ETH_GOT_IP */
void glue_ipHandler(void* pArg, esp_event_base_t evBase, int32_t evId, void* evData)
{
    ((Ethernet *) pArg)->_ipHandler(evBase, evId, evData);
}


Ethernet::Ethernet() {

}

void
Ethernet::start() {
    ESP_LOGD(TAG, "ethernet_init start");

    ESP_ERROR_CHECK_WITHOUT_ABORT(tcpip_adapter_set_default_eth_handlers());
    ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(ETH_EVENT, ESP_EVENT_ANY_ID, glue_ethHandler, NULL));
    ESP_ERROR_CHECK_WITHOUT_ABORT(esp_event_handler_register(IP_EVENT, IP_EVENT_ETH_GOT_IP, glue_ipHandler, NULL));

    eth_mac_config_t macConfig = ETH_MAC_DEFAULT_CONFIG();
    esp_eth_mac_t *mac = esp_eth_mac_new_esp32(&macConfig);
    eth_phy_config_t phyConfig = ETH_PHY_DEFAULT_CONFIG();

    // Because the default example is assuming one of those 8720 breakout boards that
    // has the address set to 1 we have to be sure to set it to 0
    phyConfig.phy_addr = 0;

    // We could remove these #if's for PHY's we aren't using but if someone uses
    // our code on some other board they would just have to put them back
#if CONFIG_PHY_IP101
    esp_eth_phy_t *phy = esp_eth_phy_new_ip101(&phyConfig);
#elif CONFIG_PHY_RTL8201
    esp_eth_phy_t *phy = esp_eth_phy_new_rtl8201(&phyConfig);
#elif CONFIG_PHY_LAN8720
    esp_eth_phy_t *phy = esp_eth_phy_new_lan8720(&phyConfig);
#elif CONFIG_PHY_DP83848
    esp_eth_phy_t *phy = esp_eth_phy_new_dp83848(&phyConfig);
#endif

    esp_eth_config_t config = ETH_DEFAULT_CONFIG(mac, phy);

    // The driver installation now calls init and the enable
    ESP_ERROR_CHECK_WITHOUT_ABORT(esp_eth_driver_install(&config, &m_handle));

    ESP_LOGD(TAG, "ethernet_init done");
}

void
Ethernet::_ethHandler(esp_event_base_t evBase, int32_t evId, void *evData) {
    switch (evId) {
        case ETHERNET_EVENT_CONNECTED:
            ESP_LOGI(TAG, "Ethernet Link Up");
            break;
        case ETHERNET_EVENT_DISCONNECTED:
            ESP_LOGI(TAG, "Ethernet Link Down");
            break;
        case ETHERNET_EVENT_START:
            ESP_LOGI(TAG, "Ethernet Started");
            break;
        case ETHERNET_EVENT_STOP:
            ESP_LOGI(TAG, "Ethernet Stopped");
            break;
        default:
            break;
    }
}

void
Ethernet::_ipHandler(esp_event_base_t evBase, int32_t evId, void *evData) {
    ip_event_got_ip_t* event = (ip_event_got_ip_t*) evData;
    const tcpip_adapter_ip_info_t* ip_info = &event->ip_info;

    ESP_LOGI(TAG, "Ethernet Got IP Address");
    ESP_LOGI(TAG, "~~~~~~~~~~~");
    ESP_LOGI(TAG, "ETHIP:" IPSTR, IP2STR(&ip_info->ip));
    ESP_LOGI(TAG, "ETHMASK:" IPSTR, IP2STR(&ip_info->netmask));
    ESP_LOGI(TAG, "ETHGW:" IPSTR, IP2STR(&ip_info->gw));
    ESP_LOGI(TAG, "~~~~~~~~~~~");
}