//
// Created by Tom Seago on 2019-08-05.
//

#pragma once

#include <esp_eth.h>
#include "esp_event.h"

#include "net_interface.h"

class EthInterface : public NetInterface {
public:
    void _evtHandler(esp_event_base_t evBase, int32_t evId, void *evData);

    const char *name() override { return "eth"; }

protected:
    bool init() override;

    void enableChanged() override;

    void addressingChanged() override;

private:
    esp_eth_config_t m_config;
    esp_eth_handle_t m_hDriver;
    esp_netif_t *m_netIf;
};