#include "sine-wave-shader.h"

#include "math.h"

#include "esp_log.h"
#define TAG "#shader"

#define PI 3.1415926535897932384626433832795


SineWaveShader::SineWaveShader(Surface *surface, Msg *msg) : Shader(surface, msg) {
    // No additional bytes of configuration
}

SineWaveShader::~SineWaveShader() {

}

void
SineWaveShader::begin(Msg *pMsg, LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
    m_color = pMsg->readColor();
    m_theta = pMsg->readFloat();
    m_density = pMsg->readFloat();

//    m_density = 1;
}

float last_theta = 0;

void
SineWaveShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    float v = sinf(m_theta + 2 * PI * ( ( float)pixelIndex / m_surface->pixelCount() * m_density) ) / 2.0 + 0.5;

    colorOut[0] = (float)m_color.R * v;
    colorOut[1] = (float)m_color.G * v;
    colorOut[2] = (float)m_color.B * v;

//    if (pixelIndex == 1 && m_theta != last_theta) {
//        last_theta = m_theta;
//        ESP_LOGD(TAG, "theta=%f density=%f v=%f", m_theta, m_density, v);
//        int32_t val = *((int32_t*)&m_theta);
//        ESP_LOGD(TAG, "  bits=%x   Color($%2x%2x%2x)", val,
//                colorOut[0], colorOut[1], colorOut[2]);
//    }

}

void
SineWaveShader::end() {

}
