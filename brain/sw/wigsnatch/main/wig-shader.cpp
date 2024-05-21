//
// Created by Tom Seago on 5/21/24.
//

#include "wig-shader.h"

WigShader::WigShader(IsoControlState& state) :
        m_state(state)
{

}

void
WigShader::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
    m_progress = m_state.m_masterAnimationLoop.progress(m_pCtx->now);
}

void
WigShader::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    // Just update the internal geometry values
    uint8_t sectionIx = indexPixel / (19 + 15);
    uint8_t offset = indexPixel % (19 + 15);

    if (offset < 19) {
        m_legIndex = sectionIx;
        m_sideIndex = -1;
    } else {
        m_legIndex = -1;
        m_sideIndex = sectionIx;
    }

    // TODO: x, y, z
}

void
WigShader::endShade() {
    m_pCtx = nullptr;
}