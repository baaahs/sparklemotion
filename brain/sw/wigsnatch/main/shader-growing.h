//
// Created by Tom Seago on 5/20/24.
//
#pragma once

#include "brain_common.h"
#include "wig-shader.h"

#include "iso-control-state.h"

class ShaderGrowing : public WigShader {
public:
    ShaderGrowing(IsoControlState& state);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    float m_progress;
    uint16_t m_maxPixel;
};