#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

class IsoShaderRainbow : public LEDShader {
public:
    IsoShaderRainbow(IsoControlState& state);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    IsoControlState &m_state;
    LEDShaderContext *m_pCtx;

    float m_progress;
};