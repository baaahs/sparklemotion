#pragma once

// Old
//extern void ethernet_init();

// New

#include "net_interface.h"
#include "esp_event.h"
#include "esp_eth.h"

class Ethernet : public NetInterface {
public:
    Ethernet();

    void start();
    void _ethHandler(esp_event_base_t evBase, int32_t evId, void *evData);
    void _ipHandler(esp_event_base_t evBase, int32_t evId, void *evData);

private:
    esp_eth_handle_t m_hDriver;

};
