//
// Created by Tom Seago on 5/20/24.
//
#include "shader-top-sparkles.h"
#include "math.h"
#include <esp_random.h>

ShaderTopSparkles::ShaderTopSparkles(IsoControlState& state) :
        WigShader(state)
{

}

void
ShaderTopSparkles::beginShade(LEDShaderContext* pCtx) {
    WigShader::beginShade(pCtx);

    m_onOffset = (uint16_t)floor(m_progress * 4.0f);
}

void
ShaderTopSparkles::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    WigShader::Apply(indexPixel, color, currentColor);

    if (indexPixel < 136) return;

//    if (((float) esp_random() / UINT32_MAX) < 0.7) {
        color[3] = 255;
//    }
}

void
ShaderTopSparkles::endShade() {
    WigShader::endShade();
}