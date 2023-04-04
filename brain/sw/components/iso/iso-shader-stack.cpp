//
// Created by Tom Seago on 12/30/19.
//

#include "iso-shader-stack.h"

void
IsoShaderStack::beginShade(LEDShaderContext* pCtx) {
    for(uint8_t i=0; i<m_len; i++) {
        if (m_stack[i]->m_enabled) {
            m_stack[i]->beginShade(pCtx);
        }
    }
}

void
IsoShaderStack::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    uint8_t last[3];
    memcpy((void*)last, (void*)currentColor, 3);
    for(uint8_t i=0; i<m_len; i++) {
        if (m_stack[i]->m_enabled) {
            m_stack[i]->Apply(indexPixel, last, last);
        }
    }
    memcpy((void*)color, (void*)last, 3);
}

void
IsoShaderStack::endShade() {
    for(uint8_t i=0; i<m_len; i++) {
        if (m_stack[i]->m_enabled) {
            m_stack[i]->endShade();
        }
    }
}

void IsoShaderStack::push(LEDShader* shader) {
    if (m_len == MAX_SHADER_STACK_SIZE) return;

    m_stack[m_len++] = shader;
}

void
IsoShaderStack::replace(LEDShader* oldShader, LEDShader* newShader) {
    if (!oldShader || !newShader) return;

    for(uint8_t i=0; i<m_len; i++) {
        if (m_stack[i] == oldShader) {
            m_stack[i] = newShader;
            return;
        }
    }
}