//
// Created by Tom Seago on 5/20/24.
//
#pragma once

#include "brain_common.h"
#include "wig-shader.h"

#include "iso-control-state.h"

class ShaderMq : public WigShader {
public:
    ShaderMq(IsoControlState& state);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    uint16_t m_onOffset;
};