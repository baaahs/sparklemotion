
#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"
#include <esp_event_base.h>

#include <msg.h>
#include <led-shader.h>

#include "shader-desc.h"
#include "shader.h"

class ShadeTree : public LEDShader {
public:
    ShadeTree(Surface *surface);

    void start();
    void stop() { m_timeToDie = true; }

    bool beginFrame(LEDShaderContext* pCtx) override;

    Color draw(uint16_t pixelIndex) override;

    void endFrame() override;

    void handleMessage(Msg* pMsg);

    /**
     * Advance to the next local shader
     */
    void nextLocalShader();

    void _handleEvent(esp_event_base_t base, int32_t id, void* event_data);

private:
    bool m_timeToDie = false;

    Surface *m_surface;

    SemaphoreHandle_t m_hMsgAccess;
    Msg* m_pMsg;

    ShaderDesc* m_pCurrentShaderDesc;
    Shader* m_pCurrentShader;
    Shader* m_pLocalShader;

    uint8_t m_localShaderIndex = -1;

    void checkForShaderChanges();
    void buildNewTree();
};
