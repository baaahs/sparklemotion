
#define ARDUINO_ARCH_ESP32
#include "NeoPixelBus.h"


#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"

// #include <stdint.h>

#include "time-base.h"
#include "esp_log.h"

#include "led-shader.h"

//class LEDShaderFiller : public LEDShader {
//public:
//    LEDShaderFiller(TimeBase& timeBase, uint16_t numPixels) :
//        m_timeBase(timeBase),
//        m_numPixels(numPixels),
//        m_colorPrimary(16, 16, 0),
//        m_colorSecondary(0, 16, 16)
//        { }
//
//    void beginShade(float progress) override {
//        m_pos = m_timeBase.posInInterval(m_timeBase.currentTime(), m_timeBase.duration(4), m_numPixels + 1);
//        // ESP_LOGI("#filler", "Render begin, m_pos=%d", m_pos);
//    }
//
//    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) override {
//        if (indexPixel < m_pos) {
//            memcpy((void*)color, (void*)&m_colorPrimary, 3);
//        } else {
//            memcpy((void*)color, (void*)&m_colorSecondary, 3);
//        }
//        // ESP_LOGI("#filler", "%d = %d %d %d", indexPixel, color[0], color[1], color[2]);
//    }
//
//    void endShade() override {
//
//    }
//private:
//    TimeBase& m_timeBase;
//    uint16_t m_numPixels;
//    uint16_t m_pos;
//
//    RgbColor m_colorPrimary;
//    RgbColor m_colorSecondary;
//};

class LEDRenderer {
public:
    LEDRenderer(TimeBase& timeBase, uint16_t pixelCount);

    void start();

//    /**
//     * This task does not depend on network events to render. It should NOT
//     * be used if the network is going to be driving the rendering.
//     */
//    void startLocalRenderTask();

    void setShader(LEDShader* shader) { m_shader = shader; }
    void render();

    void setBrightness(uint8_t brightness) { m_nBrightness = brightness; }
    uint16_t getNumPixels() { return m_pixels.PixelCount(); }

    /**
     * Private function to be called only be a the local glue function
     */
    void _showTask();

    /**
     * Private function to be called only be a the local glue function
     */
    void _renderTask();

    void logPixels();

    // Sets the brightness
    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
        uint8_t* end = currentColor + 3;

        while (currentColor != end)
        {
            uint16_t value = *currentColor;
            *(color++) = (value * m_nBrightness) >> 8;
            currentColor++;
        }
    }
private:
    uint8_t m_nBrightness;

    NeoPixelBus<NeoGrbFeature, NeoEsp32I2s0Ws2812xMethod> m_pixels;
    NeoBuffer<NeoBufferMethod<NeoGrbFeature>> m_buffer;

    SemaphoreHandle_t m_hPixelsAccess;
    LEDShader* m_shader;

    LEDShaderContext m_context;
    TimeBase& m_timeBase;
};
