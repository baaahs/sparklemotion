//
// Created by Tom Seago on 2019-08-05.
//
#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "brain_common.h"
#include "eth_interface.h"
#include "wifi_sta_interface.h"
#include "wifi_ap_interface.h"

class NetTransport : public NetInterfaceListener {
public:
    NetTransport();

    /**
     * Initializes the transport layer. Should be called at initial boot
     * before anything that will want to use the tcpip adapter is started
     * so that it can start the adapter itself.
     *
     * It should be called after the default event loop has been created.
     *
     * It does not bring up any interfaces. That can happen manually by
     * getting each interface through the various accessor methods, or
     * an entire configuration of interfaces can be set using the
     * reconfigure() function.
     */
    void start(TaskDef taskDef);

    /**
     * Get the ethernet interface.
     *
     * @return
     */
    NetInterface* eth() {
        if (!m_pEth) {
            m_pEth = new EthInterface();
            m_pEth->setListener(this);
        }
        return m_pEth;
    }

    /**
     * Get the wifi STA interface.
     *
     * @return
     */
    NetInterface* wifiSta() {
        if (!m_pWifiSta) {
            m_pWifiSta = new WifiStaInterface();
            m_pWifiSta->setListener(this);
        }
        return m_pWifiSta;
    }

    /**
     * Get the wifi AP interface.
     *
     * @return
     */
    NetInterface* wifiAp() {
        if (!m_pWifiAp) {
            m_pWifiAp = new WifiApInterface();
            m_pWifiAp->setListener(this);
        }
        return m_pWifiAp;
    }

    /**
     * Disables all interfaces.
     */
    void stopAll();

    /**
     * Reconfigures all of the interfaces using the supplied JSON data.
     *
     * TODO: Figure out how to supply JSON data from one of the IDF JSON parsers
     */
    void reconfigure();

    /**
     * Internal task method. Provides a task context for the handling of
     * various interface change related events.
     */
    void _interfaceTask();

    void netIntLinkUp(NetInterface *interface) override;

    void netIntLinkDown(NetInterface *interface) override;

    void netIntStart(NetInterface *interface) override;

    void netIntStop(NetInterface *interface) override;

    void netIntGotAddr(NetInterface *interface, esp_netif_ip_info_t *info) override;

private:
    TaskHandle_t m_hInterfaceTask = nullptr;

    EthInterface* m_pEth = nullptr;
    WifiStaInterface* m_pWifiSta = nullptr;
    WifiApInterface* m_pWifiAp = nullptr;

    bool m_ethUp = false;

};