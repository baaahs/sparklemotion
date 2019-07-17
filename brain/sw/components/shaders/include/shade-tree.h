
#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"

#include <msg.h>
#include <led-shader.h>

#include "shader-desc.h"
#include "shader.h"

class ShadeTree : public LEDShader {
public:
    ShadeTree(Surface *surface);

    void start();

    void beginShade(float progress) override;

    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) override;

    void endShade() override;

    void handleMessage(Msg* pMsg);

    /**
     * Advance to the next local shader
     */
    void nextLocalShader();

private:
    Surface *m_surface;

    SemaphoreHandle_t m_hMsgAccess;
    Msg* m_pMsg;

    ShaderDesc* m_pCurrentShaderDesc;
    Shader* m_pCurrentShader;
    Shader* m_pLocalShader;

    uint8_t m_localShaderIndex;

    void checkForShaderChanges();
    void buildNewTree();
};
