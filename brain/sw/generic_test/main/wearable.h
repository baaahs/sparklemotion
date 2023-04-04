//
// Created by Tom Seago on 2019-06-02.
//

#pragma once

#include <sys/time.h>

#include "net_transport.h"
//#include "msg_handler.h"
//#include "msg_slinger.h"

#include "led-renderer.h"
//#include "shade-tree.h"
#include "sysmon.h"
#include "brain-ui.h"
#include "http_server.h"
//#include "ota_fetcher.h"

#include "iso-control-state.h"
#include "iso-shader-stack.h"
#include "iso-shader-list.h"
#include "iso-shader-manual.h"
#include "menu.h"

class Wearable : public MenuListener, WordsHandlerListener, ColorHandlerDelegate {
public:
    Wearable();

    void start();

    void _handleEvent(esp_event_base_t base, int32_t id, void* event_data);

    void doCommand(uint8_t cmd) override;
    void gotWords(const char* buf, size_t len) override;

    uint16_t getColorCount() override;
    void getColor(int16_t index, uint8_t* value) override;
    void setColor(int16_t index, uint8_t* value) override;

private:
//    char m_brainId[8];

    NetTransport m_netTransport;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = BRAIN_DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer;

//    Surface m_surface = Surface(m_pixelCount);
//    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    IsoControlState m_state;

    IsoShaderManual* m_shaderManual;

//    Menu m_menu;

    BrainUI m_brainUI;
    HttpServer m_httpServer;

//    void setMode(LEDShader* mode);
};

