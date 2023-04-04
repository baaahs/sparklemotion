//
// Created by Tom Seago on 2019-08-05.
//

#pragma once

#include "brain_common.h"

#include <esp_system.h>
#include <esp_wifi.h>
#include <nvs_flash.h>

#include "net_interface.h"
#include "esp_event.h"

#include "lwip/err.h"
#include "lwip/sys.h"


class WifiStaInterface : public NetInterface {
public:
    void _evtHandler(esp_event_base_t evBase, int32_t evId, void *evData);

    const char *name() override { return "wifiSta"; }

    void setCredentials(const char* ssid, const char* password);

    void _taskLongPoll();

protected:
    bool init() override;

    void enableChanged() override;

    void addressingChanged() override;

    void startLongTermPoll();

private:
    wifi_config_t m_wifiConfig;

    esp_netif_t* m_netIf;

    bool m_started = false;
    bool m_connected = false;
    int m_numFailedConnects;

    bool m_haveLTPTask = false;
    TaskHandle_t m_hLTPTask;
};