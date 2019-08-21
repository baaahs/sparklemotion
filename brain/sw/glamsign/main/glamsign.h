//
// Created by Tom Seago on 2019-06-02.
//

#pragma once

#include <sys/time.h>

#include "net_transport.h"

#include "led-renderer.h"
#include "shade-tree.h"
#include "sysmon.h"
#include "brain-ui.h"
#include "http_server.h"
#include "artnet-service.h"

class Glamsign {
public:
    Glamsign();

    void start();

private:
    NetTransport m_netTransport;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = BRAIN_DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer;

    Surface m_surface = Surface(m_pixelCount);
    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    BrainUI m_brainUI;
    HttpServer m_httpServer;

    ArtnetService m_artnetService;

    void startSecondStageBoot();
};

