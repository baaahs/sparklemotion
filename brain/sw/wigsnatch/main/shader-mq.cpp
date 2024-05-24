//
// Created by Tom Seago on 5/20/24.
//
#include "shader-mq.h"
#include "math.h"

ShaderMq::ShaderMq(IsoControlState& state) :
        WigShader(state)
{

}

void
ShaderMq::beginShade(LEDShaderContext* pCtx) {
    WigShader::beginShade(pCtx);

    m_onOffset = (uint16_t)floor(m_progress * 4.0f);
}

void
ShaderMq::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    WigShader::Apply(indexPixel, color, currentColor);

    uint16_t offset = indexPixel % 4;

    RgbColor primary = RgbColor(0,0,0);
    uint8_t white = 0;
    if (offset != m_onOffset) {
        primary = m_state.chosenColor(0);
        white = 64;
    }

    memcpy((void*)color, (void*)&primary, 3);
    color[3] = white;
}

void
ShaderMq::endShade() {
    WigShader::endShade();
}