//
// Created by Tom Seago on 5/20/24.
//
#include "shader-growing.h"
#include <math.h>

ShaderGrowing::ShaderGrowing(IsoControlState& state) :
        WigShader(state)
{

}

void
ShaderGrowing::beginShade(LEDShaderContext* pCtx) {
    WigShader::beginShade(pCtx);

    m_maxPixel = (uint16_t )round(m_progress * (float)(pCtx->numPixels));
}

void
ShaderGrowing::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    WigShader::Apply(indexPixel, color, currentColor);
    RgbColor primary = m_state.chosenColor(0);

    if (indexPixel >= m_maxPixel) {
        primary = RgbColor(0,0,0);
    } else {
        color[3] = 250;
    }

    memcpy((void*)color, (void*)&primary, 3);
}

void
ShaderGrowing::endShade() {
    WigShader::endShade();
}