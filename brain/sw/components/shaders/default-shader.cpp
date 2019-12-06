#include "default-shader.h"

DefaultShader::DefaultShader(Surface *surface, Msg *config) : Shader(surface, config) {

}

DefaultShader::~DefaultShader() {

}

Color
DefaultShader::apply(uint16_t pixelIndex) {
    return { 0xFF102030 }
}
