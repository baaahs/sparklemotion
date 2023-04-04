//
// Created by Tom Seago on 2019-07-11.
//

#pragma once

#include <esp_netif.h>
#include <esp_log.h>

class NetInterface;

#define TAG_NET_INTERFACE "#netint"

class NetInterfaceListener {
public:
    virtual void netIntLinkUp(NetInterface* interface) = 0;
    virtual void netIntLinkDown(NetInterface* interface) = 0;
    virtual void netIntStart(NetInterface* interface) = 0;
    virtual void netIntStop(NetInterface* interface) = 0;

    virtual void netIntGotAddr(NetInterface* interface, esp_netif_ip_info_t* info) = 0;
};

class NetInterface {
public:
    virtual const char* name() = 0;

    NetInterfaceListener* listener() { return m_listener; }
    void setListener(NetInterfaceListener* listener) { m_listener = listener; }

    virtual void setEnabled(bool enabled) {
        if (!ensureInited()) return;
        if (enabled == m_isEnabled) return;

        m_isEnabled = enabled;
        enableChanged();
    }
    virtual bool isEnabled() { return m_isEnabled; }

    virtual void enableDHCP() {
        if (!ensureInited()) return;
        if (m_dhcpEnabled) return;

        m_dhcpEnabled = true;
        addressingChanged();
    };
    virtual bool isDHCPEnabled() { return m_dhcpEnabled; }

    virtual void setIpInfo(esp_netif_ip_info_t *info) {
        if (!ensureInited()) return;
        if (!m_dhcpEnabled &&
            info->ip.addr == m_staticIpInfo.ip.addr &&
            info->netmask.addr == m_staticIpInfo.netmask.addr &&
            info->gw.addr == m_staticIpInfo.gw.addr) {
            return;
        }

        m_dhcpEnabled = false;
        m_staticIpInfo = *info;

        addressingChanged();
    }

protected:
    bool m_isInited = false;
    bool m_isEnabled = false;

    bool m_dhcpEnabled = true;

    esp_netif_ip_info_t m_staticIpInfo;

    NetInterfaceListener* m_listener = nullptr;

    /**
     * Subclasses should override this allocate their initial resources. They
     * should do very little memory allocation or resource configuration until
     * this method is called. This lets interfaces be lazily initialized.
     *
     * If an error occurs during initialization they should return false. Otherwise
     * true.
     *
     * This method will only be called once. Right now we're not bothering with
     * the notion of de-allocating interfaces, but will probably add that in the
     * future.
     *
     * @return success or failure of resource allocation.
     */
    virtual bool init() { return true; }

    bool ensureInited() {
        if (m_isInited) return true;

        if (init()) {
            m_isInited = true;
        }

        return m_isInited;
    }

    virtual void enableChanged() = 0;
    virtual void addressingChanged() = 0;

    void tellListenerIntLinkUp() {
        if (!m_listener) return;
        m_listener->netIntLinkUp(this);
    }

    void tellListenerLinkDown() {
        if (!m_listener) return;
        m_listener->netIntLinkDown(this);
    }

    void tellListenerStart() {
        if (!m_listener) return;
        m_listener->netIntStart(this);
    }

    void tellListenerStop() {
        if (!m_listener) return;
        m_listener->netIntStop(this);
    }

    void tellListenerGotAddr(esp_netif_ip_info_t* info) {
        if (!m_listener) return;
        m_listener->netIntGotAddr(this, info);
    };

};