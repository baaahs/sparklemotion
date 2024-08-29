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
#include "cs-shader.h"
#include "cs-evil-clock.h"
#include "cs-tminus.h"
#include "menu.h"
#include "tb-horns.h"

class Wearable : public MenuListener, WordsHandlerListener {
public:
    Wearable();

    void start();

    void _handleEvent(esp_event_base_t base, int32_t id, void* event_data);

    void doCommand(uint8_t cmd) override;
    void gotWords(const char* buf, size_t len) override;

private:
//    char m_brainId[8];

    NetTransport m_netTransport;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = BRAIN_DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer1;
    LEDRenderer m_ledRenderer2;

//    Surface m_surface = Surface(m_pixelCount);
//    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    IsoControlState m_state;

    IsoShaderStack m_frontStack;
    IsoShaderStack m_backStack;
    IsoShaderList m_frontShaders;
    IsoShaderList m_backShaders;
//    CSShader m_csShader;

    // Mode shaders
    CSEvilClock m_evilClock;
    CSTMinus m_tMinus;
    CSShader m_words;

    // The last mode we set
    LEDShader* m_curMode;

    Menu m_menu;

    BrainUI m_brainUI;
    HttpServer m_httpServer;

    TBHorns m_horns;

    void setMode(LEDShader* mode);
};

