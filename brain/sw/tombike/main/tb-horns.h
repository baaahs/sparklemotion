//
// Created by Tom Seago on 8/28/24.
//

#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

class TBHorns : public LEDShader {
public:
    explicit TBHorns();

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    LEDShaderContext *m_pCtx{};
};