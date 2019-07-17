//
// Created by Tom Seago on 2019-07-11.
//

#pragma once

#include <tcpip_adapter.h>

class NetInterface {
public:
    virtual void enableDHCP() = 0;
    virtual void staticIP(ip4_addr_t ip, ip4_addr_t mask, ip4_addr_t gw) = 0;
};