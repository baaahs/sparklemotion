#include "compositor-shader.h"

#include "esp_log.h"
#define TAG "#shader"

CompositorShader::CompositorShader(Surface *surface, Msg *config) : Shader(surface, config) {
    m_shaderA = Shader::createShaderFromDescrip(surface, config);
    m_shaderB = Shader::createShaderFromDescrip(surface, config);

    if (!m_shaderA || !m_shaderB) {
        ESP_LOGE(TAG, "A shader couldn't be created, the shader tree is probably broken!");
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
CompositorShader::begin(Msg *pMsg, LEDShaderContext* pCtx) {
    if (m_shaderA) {
        m_shaderA->begin(pMsg, 0);

        if (m_shaderB) {
            m_shaderB->begin(pMsg, 0);

            m_mode = pMsg->readByte();
            m_fade = pMsg->readFloat();

            ESP_LOGI(TAG, "Compositor mode=%d fade=%f", m_mode, m_fade);
        }
    }
}

void
CompositorShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    RgbColor clrA;
    RgbColor clrB;

    if (m_shaderA) {
        m_shaderA->apply(pixelIndex, (uint8_t*)&clrA, colorIn);
    }
    if (m_shaderB) {
        m_shaderB->apply(pixelIndex, (uint8_t*)&clrB, colorIn);
    }

    if (m_mode) {
        // Add A and B into B before fading
        clrB.R += clrA.R;
        clrB.G += clrA.G;
        clrB.B += clrA.B;
    }

    // Use the fade value to take some from A and some from B
    float aFactor = 1.0f - m_fade;
    colorOut[0] = (aFactor * clrA.R) + (m_fade * clrB.R);
    colorOut[1] = (aFactor * clrA.G) + (m_fade * clrB.G);
    colorOut[2] = (aFactor * clrA.B) + (m_fade * clrB.B);
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
