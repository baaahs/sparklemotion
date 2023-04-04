//
// Created by Tom Seago on 2019-08-07.
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


class WifiApInterface : public NetInterface {
public:
    void _evtHandler(esp_event_base_t evBase, int32_t evId, void *evData);

    const char *name() override { return "wifiAP"; }

protected:
    bool init() override;

    void enableChanged() override;

    void addressingChanged() override;

private:
    wifi_config_t m_wifiConfig;

    esp_netif_t* m_netIf;
};