#include "sine-wave-shader.h"

#include "math.h"

#include "esp_log.h"
#define TAG "#shader"

SineWaveShader::SineWaveShader(uint8_t** ppCursor, uint8_t* pEnd) {
    // No additional bytes of configuration
}

SineWaveShader::~SineWaveShader() {

}

void
SineWaveShader::begin(Msg* pMsg) {
    m_color = pMsg->readColor();
    m_theta = pMsg->readFloat();
    m_density = pMsg->readFloat();
}

void
SineWaveShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    float v = sinf(m_theta + 2 * PI * ( ( float)pixelIndex / 24.0 * m_density) ) / 2.0 + 0.5;

    colorOut[0] = m_color.R * v;
    colorOut[1] = m_color.G * v;
    colorOut[2] = m_color.B * v;
}

void
SineWaveShader::end() {

}
