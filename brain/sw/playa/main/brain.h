//
// Created by Tom Seago on 2019-06-02.
//

#pragma once

#include <sys/time.h>

#include "net_transport.h"
#include "msg_handler.h"
#include "msg_slinger.h"

#include "led-renderer.h"
#include "shade-tree.h"
#include "sysmon.h"
#include "brain-ui.h"
#include "http_server.h"
#include "ota_fetcher.h"

class Brain : public MsgHandler {
public:
    Brain();

    virtual void handleMsg(Msg* pMsg);

    void start();

    void maybeSendHello();

    /**
     * Private function to be called only by the local glue function
     */
    void _helloTask();


private:
    char m_brainId[8];

    NetTransport m_netTransport;
    MsgSlinger m_msgSlinger;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = BRAIN_DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer;

    Surface m_surface = Surface(m_pixelCount);
    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    BrainUI m_brainUI;
    HttpServer m_httpServer;

    OtaFetcher m_otaFetcher;

    timeval m_otaStartedAt{};

    void startSecondStageBoot();

    void msgBrainPanelShade(Msg* pMsg);
    void msgBrainIdRequest(Msg* pMsg);
    void msgBrainMapping(Msg* pMsg);
    void msgPing(Msg* pMsg);
    void msgUseFirmware(Msg* pMsg);

    void sendHello(const IpPort &port);
    void sendPong(const IpPort &port, const uint8_t *data, size_t dataLen);

    bool otaStarted();
    void stopEverythingForOTA();
};

