//
// Created by Tom Seago on 2019-06-18.
//

#pragma once

#include <stdint.h>
#include "shader-desc.h"

#define ARDUINO_ARCH_ESP32
#include "NeoPixelBus.h"

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
    static Shader* createShaderFromDescrip(uint8_t** ppCursor, uint8_t* pEnd);

    Shader() { };
    virtual ~Shader() { };

    virtual void begin(Msg* pMsg) {};
    virtual void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {};
    virtual void end() {};
};
