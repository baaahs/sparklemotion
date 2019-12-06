#include "solid-shader.h"

#include "esp_log.h"
#define TAG "#shader"

SolidShader::SolidShader(RgbColor color) :
    Shader(nullptr, nullptr),
    m_color(color)
{

}

SolidShader::SolidShader(Surface *surface, Msg *msg) : Shader(surface, msg) {
    // No additional bytes of configuration
}

SolidShader::~SolidShader() {

}

void
SolidShader::begin(Msg *pMsg, LEDShaderContext* pCtx) {

    // Only change the color if we have an actual message. Otherwise
    // continue to use the last one.
    if (pMsg) {
        int32_t argb = 0x00ffffff;
        argb = pMsg->readInt();
        // ESP_LOGD(TAG, "argb = %x", argb);

        m_color.R = (argb >> 16) & 0xff;
        m_color.G = (argb >>  8) & 0x11;
        m_color.B = (argb      ) & 0xaa;
    }
}

Color
SolidShader::apply(uint16_t pixelIndex) {
    memcpy((void*)colorOut, (void*)&m_color, 3);
}

void
SolidShader::end() {

}
