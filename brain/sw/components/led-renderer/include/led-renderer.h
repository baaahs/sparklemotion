
#pragma once

// We need this not really ported to plain IDF library
// so we pretend that we are in the arduino world
//#define ARDUINO_ARCH_ESP32
//#include "NeoPixelBus.h"

#include "brain_common.h"
#include "freertos/semphr.h"
#include <string.h>

#include "time-base.h"
#include "esp_log.h"

#include "led-shader.h"

#include <led_strip.h>
#include "RgbColor.h"


class LEDShaderFiller : public LEDShader {
public:
    LEDShaderFiller() :
        m_pos(0)
//        m_colorPrimary(255, 255, 0),
//        m_colorSecondary(0, 255, 255)
        {
            m_colorPrimary[0] = 255;
            m_colorPrimary[1] = 255;
            m_colorPrimary[2] = 0;

            m_colorSecondary[0] = 0;
            m_colorSecondary[1] = 255;
            m_colorSecondary[2] = 255;
        }

    void beginShade(LEDShaderContext* pCtx) override {
        m_pCtx = pCtx;

        // m_pos = m_timeBase.posInInterval(m_timeBase.currentTime(), m_timeBase.duration(4), m_numPixels + 1);
        // ESP_LOGI("#filler", "Render begin, m_pos=%d", m_pos);
//        m_pos++;
//        if (m_pos >= pCtx->numPixels) {
//            m_pos = 0;
//        }
        m_pos = pCtx->progress * pCtx->numPixels;
    }

    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) override {
        if (indexPixel < m_pos) {
            memcpy((void*)color, (void*)&m_colorPrimary, 3);
        } else {
            memcpy((void*)color, (void*)&m_colorSecondary, 3);
        }
        // ESP_LOGI("#filler", "%d = %d %d %d", indexPixel, color[0], color[1], color[2]);
    }

    void endShade() override {

    }

private:
    LEDShaderContext* m_pCtx;
    uint16_t m_pos;

    uint8_t m_colorPrimary[3];
    uint8_t m_colorSecondary[3];
};



class LEDRenderer {
public:
#ifdef BRAIN_USE_RGBW
    const uint8_t gPixelWidth = 4;
#else
    const uint8_t gPixelWidth = 3;
#endif

    LEDRenderer(TimeBase& timeBase, uint16_t pixelCount, int gpioNum);

    void start(TaskDef show, TaskDef render);
    void stop() { m_timeToDie = true; }
//    /**
//     * This task does not depend on network events to render. It should NOT
//     * be used if the network is going to be driving the rendering.
//     */
//    void startLocalRenderTask();

    // TODO: Protect setting the shader against the render semaphore
    void setShader(LEDShader* shader) { m_shader = shader; }
    void render();

    void enableLocalRenderLoop(bool enable) { m_localRenderEnabled = enable; }

    void setBrightness(uint8_t brightness) { m_nBrightness = brightness; }
    uint16_t getNumPixels() { return m_pixelCount; }

    /**
     * Private function to be called only be a the local glue function
     */
    void _showTask();

    /**
     * Private function to be called only be a the local glue function
     */
    void _renderTask();

    void logPixels();

private:
    bool m_timeToDie = false;

    int m_gpioNum;
    uint8_t m_nBrightness;
    bool m_localRenderEnabled = true;

//    NeoPixelBus<BRAIN_NEO_COLORFEATURE, NeoEsp32I2s0Ws2812xMethod> m_pixels;
//    NeoBuffer<NeoBufferMethod<BRAIN_NEO_COLORFEATURE>> m_buffer;
    led_strip_handle_t m_ledStrip;
    uint16_t m_pixelCount;

    uint8_t* m_pixels;

    SemaphoreHandle_t m_hPixelsAccess;
    LEDShader* m_shader;

    LEDShaderContext m_context;
    TimeBase& m_timeBase;

    uint32_t m_frameNumber;
};
