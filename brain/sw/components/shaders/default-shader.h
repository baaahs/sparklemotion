#pragma once

#include "shader.h"

class DefaultShader : public Shader {
public:
    DefaultShader(uint8_t* pCursor, uint8_t* pEnd);
    ~DefaultShader();

    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
};