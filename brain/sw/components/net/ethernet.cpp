#include "net_priv.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_err.h"
#include "esp_event.h"
#include "esp_log.h"
#include "esp_eth.h"
#include "tcpip_adapter.h"
#include "sdkconfig.h"

static esp_eth_handle_t s_eth_handle = NULL;

/** Event handler for Ethernet events */
static void eth_event_handler(void* arg, esp_event_base_t event_base, 
                              int32_t event_id, void* event_data)
{
    switch (event_id) {
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

/** Event handler for IP_EVENT_ETH_GOT_IP */
static void got_ip_event_handler(void* arg, esp_event_base_t event_base, 
                                 int32_t event_id, void* event_data)
{
    ip_event_got_ip_t* event = (ip_event_got_ip_t*) event_data;
    const tcpip_adapter_ip_info_t* ip_info = &event->ip_info;

    ESP_LOGI(TAG, "Ethernet Got IP Address");
    ESP_LOGI(TAG, "~~~~~~~~~~~");
    ESP_LOGI(TAG, "ETHIP:" IPSTR, IP2STR(&ip_info->ip));
    ESP_LOGI(TAG, "ETHMASK:" IPSTR, IP2STR(&ip_info->netmask));
    ESP_LOGI(TAG, "ETHGW:" IPSTR, IP2STR(&ip_info->gw));
    ESP_LOGI(TAG, "~~~~~~~~~~~");
}

void ethernet_init()
{
    ESP_LOGD(TAG, "ethernet_init start");

    ESP_ERROR_CHECK(tcpip_adapter_set_default_eth_handlers());
    ESP_ERROR_CHECK(esp_event_handler_register(ETH_EVENT, ESP_EVENT_ANY_ID, &eth_event_handler, NULL));
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_ETH_GOT_IP, &got_ip_event_handler, NULL));

    eth_mac_config_t mac_config = ETH_MAC_DEFAULT_CONFIG();
    esp_eth_mac_t *mac = esp_eth_mac_new_esp32(&mac_config);
    eth_phy_config_t phy_config = ETH_PHY_DEFAULT_CONFIG();

    // Because the default example is assuming one of those breakout boards that
    // has the address set to 1 we have to be sure to set it to 0
    phy_config.phy_addr = 0;

#if CONFIG_PHY_IP101
    esp_eth_phy_t *phy = esp_eth_phy_new_ip101(&phy_config);
#elif CONFIG_PHY_RTL8201
    esp_eth_phy_t *phy = esp_eth_phy_new_rtl8201(&phy_config);
#elif CONFIG_PHY_LAN8720
    esp_eth_phy_t *phy = esp_eth_phy_new_lan8720(&phy_config);
#elif CONFIG_PHY_DP83848
    esp_eth_phy_t *phy = esp_eth_phy_new_dp83848(&phy_config);
#endif

    esp_eth_config_t config = ETH_DEFAULT_CONFIG(mac, phy);

    // The driver installation now calls init and the enable
    ESP_ERROR_CHECK(esp_eth_driver_install(&config, &s_eth_handle));

    ESP_LOGD(TAG, "ethernet_init done");
}
