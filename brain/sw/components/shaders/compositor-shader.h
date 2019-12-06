#pragma once

#include "shader.h"

class CompositorShader : public Shader {
private:
    Shader* m_shaderA;
    Shader* m_shaderB;

    int8_t m_mode;
    float m_fade;

public:
    CompositorShader(Surface *surface, Msg *config);
    ~CompositorShader();

    void begin(Msg *pMsg, LEDShaderContext* pCtx) override;
    Color apply(uint16_t pixelIndex) override;
    void end() override;
};