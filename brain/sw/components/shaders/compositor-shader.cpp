#include "compositor-shader.h"

CompositorShader::CompositorShader(uint8_t** ppCursor, uint8_t* pEnd) {
    if (*ppCursor < pEnd - 1) {
        m_shaderA = Shader::createShaderFromDescrip(ppCursor, pEnd);
        *ppCursor += 1;

        if (*ppCursor < pEnd) {
            m_shaderB = Shader::createShaderFromDescrip(ppCursor, pEnd);
            *ppCursor += 1;
        }
    }
}

CompositorShader::~CompositorShader() {
    if (m_shaderA) {
        delete m_shaderA;
    }

    if (m_shaderB) {
        delete m_shaderB;
    }
}


void
CompositorShader::begin(Msg* pMsg) {
    if (m_shaderA) {
        m_shaderA->begin(pMsg);
    }
    if (m_shaderB) {
        m_shaderB->begin(pMsg);
    }
}

void
CompositorShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    // memcpy((void*)colorOut, (void*)&m_color, 3);
}

void
CompositorShader::end() {
    if (m_shaderB) {
        m_shaderB->end();
    }
    if (m_shaderA) {
        m_shaderA->end();
    }
}
