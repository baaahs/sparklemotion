#pragma once

#include "shader.h"

class DefaultShader : public Shader {
public:
    DefaultShader(Surface *surface, Msg *config);
    ~DefaultShader();

    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
};