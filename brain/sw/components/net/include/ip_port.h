//
// Created by Tom Seago on 2019-06-02.
//

#ifndef BRAIN_IP_PORT_H
#define BRAIN_IP_PORT_H

#include <stdint.h>
#include <string.h>
#include <ostream>

#include "lwip/sockets.h"

/**
 * This class normalizes handling of an IP and Port tuple, what the
 * socket layer would call an address and what the LWIP library has
 * a handful of macros around. It's just a bit easier to deal with in
 * full on C++ using this sort of a wrapper.
 */
class IpPort {
public:

    IpPort() {
    }

    IpPort(uint16_t port) {
        m_addr.sin_port = htons(port);
        m_addr.sin_family = AF_INET;
        m_addr.sin_addr.s_addr = htonl(INADDR_BROADCAST);
    }

    IpPort(struct sockaddr_in *pOther) {
        memcpy(&m_addr, pOther, sizeof(m_addr));
    }

    struct sockaddr * addr() { return (struct sockaddr *)&m_addr; }
    struct sockaddr_in * addr_in() { return &m_addr; }

    size_t size() { return sizeof(m_addr); }

    const char * toString();

    static const IpPort BroadcastPinky;

    friend std::ostream &operator<<(std::ostream &os, const IpPort &port);


private:
    struct sockaddr_in m_addr;
};


#endif //PLAYA_IP_PORT_H
