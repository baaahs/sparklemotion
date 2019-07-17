#include "default-shader.h"

DefaultShader::DefaultShader(Surface *surface, Msg *config) : Shader(surface, config) {

}

DefaultShader::~DefaultShader() {

}

void
DefaultShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    colorOut[0] = 0x10;
    colorOut[1] = 0x20;
    colorOut[2] = 0x30;
}
