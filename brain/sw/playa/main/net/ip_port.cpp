//
// Created by Tom Seago on 2019-06-02.
//

#include "ip_port.h"

#include "net_priv.h"

// #include "lwip/sys.h"
#include <lwip/netdb.h>

const IpPort IpPort::BroadcastPinky(8002);

std::ostream &operator<<(std::ostream &os, const IpPort &port) {
    os << "m_addr: " << port.m_addr.sin_addr.s_addr;
    return os;
}


const char*
IpPort::toString()
{
    static char addr[128];

    inet_ntoa_r(m_addr.sin_addr.s_addr, addr, sizeof(addr) - 1);
    int len = strlen(addr);
    sprintf(&addr[len], ":%d", ntohs(m_addr.sin_port));

    return addr;
}
