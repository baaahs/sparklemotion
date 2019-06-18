
#define ARDUINO_ARCH_ESP32
#include "NeoPixelBrightnessBus.h"


#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"

// #include <stdint.h>

#include "time-base.h"
#include "esp_log.h"

class LEDShader {
public:
    virtual void Begin() = 0;
    virtual void Apply(uint16_t indexPixel, uint8_t* color, uint8_t* currentColor) = 0;
};

class LEDShaderFiller : public LEDShader {
public:
    LEDShaderFiller(TimeBase& timeBase, uint16_t numPixels) :
        m_timeBase(timeBase),
        m_numPixels(numPixels),
        m_colorPrimary(16, 16, 0),
        m_colorSecondary(0, 16, 16)
        { }

    virtual void Begin() {
        m_pos = m_timeBase.posInInterval(m_timeBase.currentTime(), m_timeBase.duration(4), m_numPixels + 1);
        // ESP_LOGI("#filler", "Render begin, m_pos=%d", m_pos);
    }

    virtual void Apply(uint16_t indexPixel, uint8_t* color, uint8_t* currentColor) {
        if (indexPixel < m_pos) {
            memcpy((void*)color, (void*)&m_colorPrimary, 3);
        } else {
            memcpy((void*)color, (void*)&m_colorSecondary, 3);
        }
        // ESP_LOGI("#filler", "%d = %d %d %d", indexPixel, color[0], color[1], color[2]);
    }
private:
    TimeBase& m_timeBase;
    uint16_t m_numPixels;
    uint16_t m_pos;

    RgbColor m_colorPrimary;
    RgbColor m_colorSecondary;
};

class LEDRenderer {
public:
    LEDRenderer(TimeBase& timeBase);

    void start();

    /**
     * This task does not depend on network events to render. It should NOT
     * be used if the network is going to be driving the rendering.
     */
    void startLocalRenderTask();

    void setShader(LEDShader* shader) { m_shader = shader; }
    void render();

    void setBrightness(uint8_t brightness) { m_pixels.SetBrightness(brightness); }
    uint16_t getNumPixels() { return m_pixels.PixelCount(); }

    void _task();
    void _localRenderTask();
private:
    NeoPixelBrightnessBus<NeoGrbFeature, NeoEsp32I2s0Ws2812xMethod> m_pixels;
    NeoBuffer<NeoBufferMethod<NeoGrbFeature>> m_buffer;

    SemaphoreHandle_t m_hPixelsAccess;
    LEDShader* m_shader;

    TimeBase& m_timeBase;
};
