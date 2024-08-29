#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

class IsoShaderSolid : public LEDShader {
public:
    explicit IsoShaderSolid(IsoControlState& state, uint8_t selection = 0);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    IsoControlState &m_state;
    uint8_t m_selection;
    LEDShaderContext *m_pCtx{};
};