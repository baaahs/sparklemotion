
#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"

#include <msg.h>
#include <led-shader.h>

#include "shader-desc.h"
#include "shader.h"

class ShadeTree : public LEDShader {
public:
    ShadeTree();

    void start();

    void beginShade() override;

    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) override;

    void endShade() override;

    void handleMessage(Msg* pMsg);

private:
    SemaphoreHandle_t m_hMsgAccess;
    Msg* m_pMsg;

    ShaderDesc* m_pCurrentShaderDesc;
    Shader* m_pCurrentShader;

    void checkForShaderChanges();
    void buildNewTree();
};
