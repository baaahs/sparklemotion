//
// Created by Tom Seago on 1/26/20.
//

#include "iso-shader-manual.h"

IsoShaderManual::IsoShaderManual(IsoControlState& state) :
        m_state(state)
{

}

void
IsoShaderManual::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
    m_progress = m_state.m_masterAnimationLoop.progress(m_pCtx->now);
}

void
IsoShaderManual::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    if (indexPixel < m_count) {
        memcpy((void*)color, (void*)(m_colors + (indexPixel * 3)), 3);
    } else {
        RgbColor primary = m_state.chosenColor(0);
        memcpy((void *) color, (void *) &primary, 3);
    }
}

void
IsoShaderManual::endShade() {
    m_pCtx = nullptr;
}

void
IsoShaderManual::setColorCount(uint16_t count) {
    m_count = count;
    if (m_colors) {
        free(m_colors);
        m_colors = nullptr;
    }
    m_colors = (uint8_t*)malloc(m_count * 3);
}

void
IsoShaderManual::getColor(int16_t index, uint8_t* value) {
    if (index < m_count) {
        memcpy((void*)value, (void*)(m_colors + (index * 3)), 3);
    }
}

void
IsoShaderManual::setColor(int16_t index, uint8_t* value) {
    if (index < m_count) {
        memcpy((void*)(m_colors + (index * 3)), (void*)value, 3);
    }
}