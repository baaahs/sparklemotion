#include "solid-shader.h"

SolidShader::SolidShader(uint8_t* pCursor, uint8_t* pEnd) {

}

SolidShader::~SolidShader() {

}

void
SolidShader::begin(Msg* pMsg) {
    auto argb = pMsg->readInt();
    m_color.R = (argb >> 16) & 0xff;
    m_color.G = (argb >>  8) & 0xff;
    m_color.B = (argb      ) & 0xff;
}

void
SolidShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    memcpy((void*)colorOut, (void*)&m_color, 3);
}

void
SolidShader::end() {

}
