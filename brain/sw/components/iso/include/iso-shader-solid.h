#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

class IsoShaderSolid : public LEDShader {
public:
    IsoShaderSolid(IsoControlState& state);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    IsoControlState &m_state;
    LEDShaderContext *m_pCtx;
};