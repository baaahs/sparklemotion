//
// Created by Tom Seago on 2019-06-02.
//

#pragma once

#include <sys/time.h>

#include "net_transport.h"
#include "msg_handler.h"
#include "msg_slinger.h"

#include "led-renderer.h"
//#include "shade-tree.h"
#include "sysmon.h"
#include "brain-ui.h"
//#include "http_server.h"
//#include "ota_fetcher.h"

#include "iso-control-state.h"
#include "iso-shader-stack.h"
#include "iso-shader-list.h"
//#include "cs-shader.h"
//#include "cs-nye-clock.h"
//#include "cs-tminus.h"
#include "iso-shader-rainbow.h"

#include "menu.h"

class WigSnatch : public MenuListener, IsoShaderListListener /*, WordsHandlerListener */ {
public:
    WigSnatch();

    void start();

    void _handleEvent(esp_event_base_t base, int32_t id, void* event_data);

    void doCommand(uint8_t cmd) override;
//    void gotWords(const char* buf, size_t len) override;

    // IsoShaderListListener
    void handleShaderListNext(LEDShader* nextShader) override;

private:
//    char m_brainId[8];

    NetTransport m_netTransport;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = BRAIN_DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer;

//    Surface m_surface = Surface(m_pixelCount);
//    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    IsoControlState m_state;

    IsoShaderStack m_masterStack;
    IsoShaderList m_colorList;
//    CSShader m_csShader;

    // Mode shaders
//    CSNYEClock m_nyeClock;
//    CSTMinus m_tMinus;
//    CSShader m_words;
//    IsoShaderRainbow m_shaderRainbow;

    // The last mode we set
    LEDShader* m_curMode;

    Menu m_menu;

    BrainUI m_brainUI;
//    HttpServer m_httpServer;

    void setMode(LEDShader* mode);
};

