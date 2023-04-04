//
// Created by Tom Seago on 2019-08-05.
//

#include "net_transport.h"
#include "net_priv.h"

#include <esp_err.h>
#include <esp_netif.h>
#include <driver/gpio.h>
//#include <driver/periph_ctrl.h>
//#include <hal/emac.h>


////////////////////////////////////////////////////////////
// Glue functions that let us use C++ functions with the IDF
// C apis.

void
glue_interfaceTask(void *pArg) {
    ((NetTransport*)pArg)->_interfaceTask();
}

////////////////////////////////////////////////////////////

NetTransport::NetTransport() {

}

void
NetTransport::start(TaskDef taskDef) {
    // Initialize the tcpip adapter before anyone else does.
    esp_netif_init();

    // Create our task which will register various event listeners
    auto result = taskDef.createTask(glue_interfaceTask, this, &m_hInterfaceTask);
    if (result != pdPASS) {
        ESP_LOGE(TAG, "Failed to create network interface task: %d", result);
    } else {
        ESP_LOGI(TAG, "Created network interface task");
    }
}


void
NetTransport::_interfaceTask() {
    // TODO: Register interest in IP related events
    // TODO: Register interest in interface control events

//    vTaskDelay(pdMS_TO_TICKS(100));
//
//    ESP_LOGE(TAG, "Doing forced 1s after boot reconfiguration");
//    // reconfigure();
//
//    vTaskDelay(pdMS_TO_TICKS(500));
//
//    gpio_config_t config;
//    config.pin_bit_mask = (uint64_t)1 << 15;
//    config.mode = GPIO_MODE_OUTPUT;
//    config.pull_up_en = GPIO_PULLUP_ENABLE;
//    config.pull_down_en = GPIO_PULLDOWN_DISABLE;
//    config.intr_type = GPIO_INTR_DISABLE;
//
//    ESP_ERROR_CHECK_WITHOUT_ABORT(gpio_config(&config));
//
//    ESP_LOGE(TAG, "Resetting phy power");
//
//    ESP_ERROR_CHECK_WITHOUT_ABORT(gpio_set_level(GPIO_NUM_15, 0));
//
//    vTaskDelay(pdMS_TO_TICKS(200));
//
//    ESP_ERROR_CHECK_WITHOUT_ABORT(gpio_set_level(GPIO_NUM_15, 1));
//
//    vTaskDelay(pdMS_TO_TICKS(200));
//
//    ESP_LOGE(TAG, "Reset of phy power done, re-init of driver ");

    // Turn on the clock
//    periph_module_enable(PERIPH_EMAC_MODULE);
//    emac_ext_dev_t extRegs;
//    emac_hal_context_t context;
//    context.ext_regs = &extRegs;
//    emac_hal_lowlevel_init(&context);
//
//    ESP_LOGE(TAG, "Setting pin %d to low", CONFIG_ETH_PHY_RST_GPIO);
//    gpio_pad_select_gpio(CONFIG_ETH_PHY_RST_GPIO);
//    gpio_set_direction((gpio_num_t)CONFIG_ETH_PHY_RST_GPIO, GPIO_MODE_OUTPUT);
//    gpio_set_level((gpio_num_t)CONFIG_ETH_PHY_RST_GPIO, 0);
//
//    vTaskDelay(pdMS_TO_TICKS(500));
//
    ESP_LOGE(TAG, "Starting network drivers");
    reconfigure();

#if BRAIN_ETHERNET_ENABLED
    vTaskDelay(pdMS_TO_TICKS(20000));

    ESP_LOGE(TAG, "Checking for ethernet link up");
    if (!m_ethUp) {
        ESP_LOGE(TAG, "Ethernet link not up, restarting");

//        ESP_LOGE(TAG, "=========  Ethernet restart disabled for testing wifi  ===========");
        brain_restart(30);
    } else {
        ESP_LOGE(TAG, "Ethernet up - cool");
    }
#endif // BRAIN_ETHERNET_ENABLED

//    vTaskDelay(pdMS_TO_TICKS(1000));
//
//    ESP_LOGE(TAG, "Doing network drivers again");
//    reconfigure();

    //    if (m_pEth) {
//        ESP_LOGE(TAG, "Disabling eth driver");
//        m_pEth->setEnabled(false);
//
//        vTaskDelay(pdMS_TO_TICKS(200));
//        ESP_LOGE(TAG, "Re-enabling eth driver");
//
//        m_pEth->setEnabled(true);
//    } else {
//        ESP_LOGE(TAG, "No eth driver");
//    }

    // Just in case we ever exit, we're supposed to do this
    vTaskDelete(NULL);
}


void
NetTransport::netIntLinkUp(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Link up", pcTaskGetName(nullptr), interface->name());

    if (interface == m_pEth) {
        m_ethUp = true;
    }
}

void
NetTransport::netIntLinkDown(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Link down", pcTaskGetName(nullptr), interface->name());

    if (interface == m_pEth) {
        m_ethUp = false;
    }
}

void
NetTransport::netIntStart(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Start", pcTaskGetName(nullptr), interface->name());
}

void
NetTransport::netIntStop(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Stop", pcTaskGetName(nullptr), interface->name());
}

void
NetTransport::netIntGotAddr(NetInterface *interface, esp_netif_ip_info_t *info) {
    ESP_LOGI(TAG, "(task=%s) (%s) Got address, ip " IPSTR ", mask " IPSTR ", gw " IPSTR,
             pcTaskGetName(nullptr), interface->name(),
            IP2STR(&info->ip), IP2STR(&info->ip), IP2STR(&info->ip));
}

void
NetTransport::stopAll() {
    if (m_pEth) {
        m_pEth->setEnabled(false);
    }
    if (m_pWifiSta) {
        m_pWifiSta->setEnabled(false);
    }
    if (m_pWifiAp) {
        m_pWifiAp->setEnabled(false);
    }
}

void
NetTransport::reconfigure() {
    ESP_LOGI(TAG, "Reconfiguring network");
    stopAll();

#if BRAIN_ETHERNET_ENABLED
    eth();
    m_pEth->setEnabled(true);


#endif

#if BRAIN_WIFI_ENABLED
    // Ensure that the STA interface exists and then
    // configure it
    wifiSta();
    m_pWifiSta->setCredentials(GlobalConfig.staSsid(), GlobalConfig.staPass());
    m_pWifiSta->setEnabled(true);

    // Ensure the AP interface exists and then enable it
    wifiAp();
    m_pWifiAp->setEnabled(true);
#endif // BRAIN_WIFI_ENABLED
}
