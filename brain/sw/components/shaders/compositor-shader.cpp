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

//            ESP_LOGI(TAG, "Compositor mode=%d fade=%f", m_mode, m_fade);
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

    // Because we might want to add values which could overflow we have to upgrade
    // the storage for the composite result.
    uint16_t compR = clrB.R;
    uint16_t compG = clrB.G;
    uint16_t compB = clrB.B;

    if (m_mode) {
        // Add A and B into B before fading
        compR = MIN(255, compR + clrA.R);
        compG = MIN(255, compG + clrA.G);
        compB = MIN(255, compB + clrA.B);
    }

    // Use the fade value to take some from A and some from B
    float aFactor = 1.0f - m_fade;
    colorOut[0] = (aFactor * clrA.R) + (m_fade * compR);
    colorOut[1] = (aFactor * clrA.G) + (m_fade * compG);
    colorOut[2] = (aFactor * clrA.B) + (m_fade * compB);

//    if (pixelIndex == 1) {
//        ESP_LOGI(TAG, "a=%2x%2x%2x  b=%2x%2x%2x  out=%2x%2x%2x",
//                clrA.R, clrA.G, clrA.B,
//                clrB.R, clrB.G, clrB.B,
//                colorOut[0], colorOut[1], colorOut[2]
//        );
//    }
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
