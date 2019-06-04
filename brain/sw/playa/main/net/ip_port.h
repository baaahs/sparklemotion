//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_IP_PORT_H
#define PLAYA_IP_PORT_H

#include <stdint.h>

#include "lwip/err.h"
#include "lwip/sockets.h"

class IpPort {
public:
    IpPort() {
    }

    IpPort(uint16_t port) {
        m_addr.sin_port = port;
        m_addr.sin_family = AF_INET;
        m_addr.sin_addr.s_addr = htonl(INADDR_BROADCAST);
    }

    IpPort(struct sockaddr_in *pOther) {
        memcpy(&m_addr, pOther, sizeof(m_addr));
    }

    struct sockaddr * addr() { return (struct sockaddr *)&m_addr; }
    struct sockaddr_in * addr_in() { return &m_addr; }

    size_t size() { return sizeof(m_addr); }
private:
    struct sockaddr_in m_addr;
};


#endif //PLAYA_IP_PORT_H
