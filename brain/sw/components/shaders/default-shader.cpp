#include "default-shader.h"

DefaultShader::DefaultShader(uint8_t* pCursor, uint8_t* pEnd) {

}

DefaultShader::~DefaultShader() {

}

void
DefaultShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    colorOut[0] = 0x10;
    colorOut[1] = 0x20;
    colorOut[2] = 0x30;
}
