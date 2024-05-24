//
// Created by Tom Seago on 2019-06-02.
//

#include <esp_ota_ops.h>
#include <esp_event.h>
#include <esp_system.h>
#include "wigsnatch.h"
#include "brain_common.h"

#include "esp_log.h"

#include "freertos/FreeRTOS.h"
#include "freertos/timers.h"

#include "iso-shader-solid.h"
#include "iso-shader-rainbow.h"
#include "iso-shader-stack.h"

#include "brain-ui-events.h"

#include "shader-growing.h"
#include "shader-legs.h"
#include "shader-mq.h"
#include "shader-top-sparkles.h"
#include "shader-rotator.h"

static const char* TAG = TAG_BRAIN;

void glue_handleEvent(void* arg, esp_event_base_t base, int32_t id, void* data) {
    ((WigSnatch*)arg)->_handleEvent(base, id, data);
}

WigSnatch::WigSnatch() :
    m_ledRenderer(m_timeBase, m_pixelCount),
    m_menu(*this)
{
}

void
WigSnatch::start() {
    gSysMon.start(DefaultBrainTasks.sysmon);
    m_brainUI.start(DefaultBrainTasks.ui);

    GlobalConfig.load();

    m_netTransport.start(DefaultBrainTasks.net);

//    m_state.setChosenColor(0, RgbColor(0, 190, 255));
//    m_state.setChosenColor(0, RgbColor(0, 190, 255));
    m_state.setChosenColor(0, RgbColor(0,198,255));
    m_state.setChosenColor(1, RgbColor(200,32,0));

    m_colorList.setListener(this);
    // For testing we just do a growing thing
//    m_state.setChosenColor(0, RgbColor(255,255,255));
    m_colorList.push(new ShaderRotator(m_state));
    m_colorList.push(new ShaderMq(m_state));

//    m_colorList.push(new ShaderGrowing(m_state));

    m_colorList.push(new ShaderLegs(m_state));
    m_colorList.push(new IsoShaderRainbow(m_state));

    m_state.m_masterAnimationLoop.setDuration(2000);
    m_colorList.setMexShaderTime(14000);
    //    m_colorList.push(new IsoShaderSolid(m_state));
//    m_colorList.push(new IsoShaderRainbow(m_state));

//    m_words.setText(new CSText("Hello friend"));

    // Stack, bottom layer is a color
    m_masterStack.push(&m_colorList);

    m_masterStack.push(new ShaderTopSparkles(m_state));

    // Then a mode shader
//    m_masterStack.push(&m_nyeClock);
//    m_curMode = &m_nyeClock;
    //m_masterStack.push()

    // Top is menu which sometimes is enabled and sometimes not
//    m_masterStack.push(&m_menu);


    ///// That's it, lets use it!
    m_ledRenderer.setShader(&m_masterStack);

    // Start talking to the pixels
    m_ledRenderer.start(DefaultBrainTasks.show, DefaultBrainTasks.render);

    // Some initial debugging stuff
    ESP_LOGE(TAG, "------- Wearable Start ---------");
    ESP_LOGE(TAG, "xPortGetTickRateHz = %d", xPortGetTickRateHz());
    ESP_LOGE(TAG, "pdMS_TO_TICKS(1000) = %d", pdMS_TO_TICKS(1000));
    ESP_LOGE(TAG, "getFPS() = %d", m_timeBase.getFPS());
    ESP_LOGE(TAG, "getFrameDuration() = %d", m_timeBase.getFrameDuration());

    // Do this last!
//    m_httpServer.start();
//    m_httpServer.setWordsHandlerListener(this);

    // And then we want some UI events yay!
    esp_event_handler_register(BRAIN_UI_BASE, BrainUiEvent::KeyPress, glue_handleEvent, this);
}

void
WigSnatch::_handleEvent(esp_event_base_t base, int32_t id, void* data) {

    if (!data) return;

    auto evt = (BrainUiEvent*)data;

    if (evt->id == BrainUiEvent::KeyPress) {
        ESP_LOGW(TAG, "Key code=%d mods=%d isLong=%d", evt->code, evt->modifiers, evt->isLong());
        switch(evt->code) {
            case BrainUiEvent::Left :
                if (m_menu.m_enabled) {
                    m_menu.evtSelect();
                } else {

                }
                break;

            case BrainUiEvent::Right :
                if (evt->isLong()) {
                    m_menu.m_enabled = !m_menu.m_enabled;
                } else {
                    if (m_menu.m_enabled) {
                        m_menu.evtNext();
                    } else {
                    }
                }
                break;

            default:
                break;
        }
    }
}

void
WigSnatch::setMode(LEDShader* mode) {
    m_masterStack.replace(m_curMode, mode);
    m_curMode = mode;
}

RgbColor colorList[] = {
        RgbColor(255, 255, 0),
        RgbColor(0, 255, 0),
        RgbColor(0, 255, 255),
        RgbColor(0, 0, 255),
        RgbColor(255, 0, 255),
        RgbColor(255, 0, 0),
        RgbColor(255, 64, 0),
        RgbColor(255, 128, 0),
        RgbColor(255, 191, 0),
        RgbColor(255, 255, 0),
        RgbColor(128, 212, 25),
        RgbColor(0, 168, 51),
        RgbColor(21, 132, 102),
        RgbColor(42, 95, 153),
        RgbColor(85, 48, 140),
        RgbColor(128, 0, 128),
        RgbColor(191, 0, 64),
};

void
WigSnatch::doCommand(uint8_t cmd) {

    if (cmd >= CMD_COLOR_BASE) {
        m_state.setChosenColor(0, colorList[cmd - CMD_COLOR_BASE]);
        return;
    }

    switch(cmd) {
        case CMD_BRIGHTNESS_LOW:
            m_ledRenderer.setBrightness(10);
            break;

        case CMD_BRIGHTNESS_MED:
            m_ledRenderer.setBrightness(30);
            break;

        case CMD_BRIGHTNESS_HI:
            m_ledRenderer.setBrightness(60);
            break;

//        case CMD_MODE_CLOCK:
//            setMode(&m_nyeClock);
//            break;
//
//        case CMD_MODE_TMINUS:
//            setMode(&m_tMinus);
//            break;
//
//        case CMD_MODE_WORDS:
//            setMode(&m_words);
//            break;

        case CMD_RAINBOW_OFF:
            m_colorList.reset();
            m_colorList.setMexShaderTime(0);
            break;

        case CMD_RAINBOW_ON:
            m_colorList.setMexShaderTime(5000);
            break;

        default:
            break;
    }
}

void
WigSnatch::handleShaderListNext(LEDShader* nextShader) {
    // Select some new colors randomly from our color list
//    uint32_t ix = esp_random() % 16;
//    m_state.setChosenColor(0, colorList[ix]);
//    ix += 2;
//    ix += esp_random() % 3;
//    if (ix>15) ix -= 15;
//    m_state.setChosenColor(1, colorList[ix]);
    float hue = (float) esp_random() / UINT32_MAX;
    float hue2 = hue + 0.3f;
    if (hue2 >= 1.0) hue2 -= 1.0;

    m_state.setChosenColor(0, gPaletteRYBRainbow.colorInPalette(hue));
    m_state.setChosenColor(1, gPaletteRYBRainbow.colorInPalette(hue2));

    m_state.orientation = esp_random() % 3;
}

//void
//Wearable::gotWords(const char* buf, size_t len) {
////    m_words.setText(new CSText(buf, len));
//}
