//
// Created by Tom Seago on 2019-08-05.
//

#include "net_transport.h"
#include "net_priv.h"

#include <esp_err.h>
#include <tcpip_adapter.h>

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
    tcpip_adapter_init();

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

    // Just in case we ever exit, we're supposed to do this
    vTaskDelete(NULL);
}

void
NetTransport::netIntLinkUp(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Link up", pcTaskGetTaskName(nullptr), interface->name());
}

void
NetTransport::netIntLinkDown(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Link down", pcTaskGetTaskName(nullptr), interface->name());
}

void
NetTransport::netIntStart(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Start", pcTaskGetTaskName(nullptr), interface->name());
}

void
NetTransport::netIntStop(NetInterface *interface) {
    ESP_LOGI(TAG, "(task=%s) (%s) Stop", pcTaskGetTaskName(nullptr), interface->name());
}

void
NetTransport::netIntGotAddr(NetInterface *interface, const tcpip_adapter_ip_info_t *info) {
    ESP_LOGI(TAG, "(task=%s) (%s) Got address, ip " IPSTR ", mask " IPSTR ", gw " IPSTR,
            pcTaskGetTaskName(nullptr), interface->name(),
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
}

void
NetTransport::reconfigure() {
    stopAll();

    eth();
    m_pEth->setEnabled(true);

    wifiSta();
    m_pWifiSta->setCredentials(GlobalConfig.staSsid(), GlobalConfig.staPass());
    m_pWifiSta->setEnabled(true);
}
