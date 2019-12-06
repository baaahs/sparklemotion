#pragma once

#include "shader.h"

class DefaultShader : public Shader {
public:
    DefaultShader(Surface *surface, Msg *config);
    ~DefaultShader();

    Color apply(uint16_t pixelIndex) override;
};