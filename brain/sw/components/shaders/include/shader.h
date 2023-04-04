//
// Created by Tom Seago on 2019-06-18.
//

#pragma once

#include <stdint.h>
#include "led-shader.h"
#include "shader-desc.h"

//#define ARDUINO_ARCH_ESP32
//#include "NeoPixelBus.h"
#include "RgbColor.h"

typedef union Color {
    uint32_t argb;

    // esp32 is little-endian, so reversed:
    struct channel {
        uint8_t b;
        uint8_t g;
        uint8_t r;
        uint8_t a;
    } channel;
} Color;

class Surface {
public:
    Surface(uint16_t pixelCount) {
        m_pixelCount = pixelCount;
    };

    ~Surface() {
    };

    uint16_t pixelCount() {
        return m_pixelCount;
    }

private:
    uint16_t m_pixelCount;
};

class Shader {
public:
    enum class Type : uint8_t {
        SOLID = 0,
        PIXEL,
        SINE_WAVE,
        COMPOSITOR,
        SPARKLE,
        SIMPLE_SPATIAL,
        HEART,
        RANDOM,
    };

    /**
     * Creates a new shader using the description found at *ppCursor. *ppCursor
     * will be advanced by at least one byte and possibly more depending on what
     * type of shader(s) get created.
     *
     * @param ppCursor
     * @param pEnd
     * @return
     */
    static Shader* createShaderFromDescrip(Surface *surface, Msg *config);

    Shader(Surface* surface, Msg* config) {
        m_surface = surface;
    };
    virtual ~Shader() { };

    virtual void begin(Msg *pMsg, LEDShaderContext* pCtx) {};
    virtual void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {};
    virtual void end() {};

    Surface *m_surface;
};
