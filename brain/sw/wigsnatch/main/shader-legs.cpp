//
// Created by Tom Seago on 5/20/24.
//
#include "shader-legs.h"

ShaderLegs::ShaderLegs(IsoControlState& state) :
        WigShader(state)
{

}

void
ShaderLegs::beginShade(LEDShaderContext* pCtx) {
    WigShader::beginShade(pCtx);
}

void
ShaderLegs::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    WigShader::Apply(indexPixel, color, currentColor);

    RgbColor primary = m_state.chosenColor(1);
    if (m_legIndex >=0 ) {
//        primary = RgbColor(255, 0, 0);
        primary = m_state.chosenColor(0);
    }

    memcpy((void*)color, (void*)&primary, 3);
}

void
ShaderLegs::endShade() {
    WigShader::endShade();
}