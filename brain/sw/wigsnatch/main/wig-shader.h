//
// Created by Tom Seago on 5/21/24.
//
#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

class WigShader : public LEDShader {
public:
    WigShader(IsoControlState& state);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

protected:
    IsoControlState &m_state;
    LEDShaderContext *m_pCtx;

    int8_t m_legIndex;
    int8_t m_sideIndex;
    int8_t m_x;
    int8_t m_y;
    int8_t m_z;

    float m_progress;
};