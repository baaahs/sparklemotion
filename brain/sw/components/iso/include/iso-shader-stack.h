//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "led-renderer.h"
#include "iso-shader-list.h"

#define MAX_SHADER_STACK_SIZE 10

class IsoShaderStack : public LEDShader {
public:
    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

    /**
     * Adds a shader to the end.
     * @param shader
     */
    void push(LEDShader *shader);

    /**
     * Replaces the old shader with the new one.
     * @param oldShader
     * @param newShader
     */
    void replace(LEDShader* oldShader, LEDShader* newShader);

private:
    uint8_t m_len;
    LEDShader* m_stack[MAX_SHADER_STACK_SIZE];

};