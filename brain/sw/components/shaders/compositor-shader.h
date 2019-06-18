#pragma once

#include "shader.h"

class CompositorShader : public Shader {
public:
    CompositorShader(uint8_t* pCursor, uint8_t* pEnd);
    ~CompositorShader();
};