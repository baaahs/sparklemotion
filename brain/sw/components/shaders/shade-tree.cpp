#include "shader.h"
#include "shade-tree.h"

#include <esp_event.h>
#include <esp_log.h>

#include "brain-ui-events.h"
#include "default-shader.h"
#include "solid-shader.h"
#include "rainbow-shader.h"

#define TAG TAG_SHDTREE

void glue_handleEvent(void* arg, esp_event_base_t base, int32_t id, void* data) {
    ((ShadeTree*)arg)->_handleEvent(base, id, data);
}


ShadeTree::ShadeTree(Surface *surface) {
    m_surface = surface;
}

void
ShadeTree::start() {
    m_hMsgAccess = xSemaphoreCreateBinary();
    if (!m_hMsgAccess) {
        ESP_LOGE(TAG, "ERROR: Unable to create m_hMsgAccess semaphore");
        return;
    }

    // Always start with a give
    xSemaphoreGive(m_hMsgAccess);


    // Start with a local shader
    // Important to do AFTER the semaphore has been initialized!
    nextLocalShader();

    esp_event_handler_register(BRAIN_UI_BASE, BrainUiEvent::KeyPress, glue_handleEvent, this);
}

bool
ShadeTree::beginFrame(LEDShaderContext* pCtx) {
    if (m_timeToDie) return false;

    xSemaphoreTake(m_hMsgAccess, portMAX_DELAY);
    // Assume success
    ESP_LOGD(TAG, "beginFrame got semaphore");

    if (m_pCurrentShader && m_pMsg) {
        m_pCurrentShader->begin(m_pMsg, pCtx);
        return true;
    } else if (m_pLocalShader) {
        ESP_LOGI(TAG, "beginFrame with localShader");
        m_pLocalShader->begin(nullptr, pCtx);
        return true;
    } else {
        //ESP_LOGW(TAG, "beginFrame but don't have a message and a current shader");
        xSemaphoreGive(m_hMsgAccess);
        return false;
    }
}

Color
ShadeTree::draw(uint16_t pixelIndex) {
    if (m_pCurrentShader) {
        return m_pCurrentShader->apply(pixelIndex);
    } else if (m_pLocalShader) {
        return m_pLocalShader->apply(pixelIndex);
    }
}

void
ShadeTree::endFrame() {

    if (m_pCurrentShader) {
        m_pCurrentShader->end();
    } else if (m_pLocalShader) {
        m_pLocalShader->end();
    }

    if (m_pMsg) {
        // We done did this one so let it go
        m_pMsg->release();
        m_pMsg = nullptr;
    }

    // Be nice and give it back
    // ESP_LOGW(TAG, "endFrame giving semaphore");
    xSemaphoreGive(m_hMsgAccess);
}

void
ShadeTree::handleMessage(Msg* pMsg){
    // ESP_LOGW(TAG, "handleMessage pMsg=%p", pMsg);
    if (!pMsg) return;
    if (m_timeToDie) return;

    // Wait, but not forever so that the network task keeps running just in
    // case rendering explodes somehow.
    // ESP_LOGW(TAG, "handleMessage pMsg=%p waiting", pMsg);
    if (xSemaphoreTake(m_hMsgAccess, pdMS_TO_TICKS(3000)) == pdFALSE) {
        ESP_LOGE(TAG, "Dropping a shader message because we couldn't get the semaphore");
        return;
    };
    // ESP_LOGW(TAG, "handleMessage pMsg=%p got semaphore", pMsg);

    if (m_pMsg) {
        m_pMsg->release();
    }

    // All we need to do is set this message in
    m_pMsg = pMsg;
    m_pMsg->addRef();
    m_pMsg->log("BrainShader");

    // Now we do any setup or massive shader switch a roo sort of stuff here
    // in the network task rather than leaving it for the render task to
    // stumble over. It _probably_ doesn't make much difference if we do
    // it here or during say, beginFrame(), but this feels a little bit
    // more appropriate.
    checkForShaderChanges();

    // And that's it!
    // ESP_LOGW(TAG, "handleMessage pMsg=%p give semaphore back", pMsg);
    xSemaphoreGive(m_hMsgAccess);
}

void
ShadeTree::checkForShaderChanges() {
    if (!m_pMsg) return;

    // Assume that shader messages always have a description, so if we
    // don't have a description, we need to build a new tree.
    if (!m_pCurrentShaderDesc) {
        buildNewTree();
        return;
    }

    // Okay, we have a description, check the length and then do a
    // byte by byte comparison. Any differences and we just bail out to
    // creating a new tree.
    if (!m_pCurrentShaderDesc->isSameAs(m_pMsg)) {
        buildNewTree();
        return;
    } else {
        // Skip to the good parts of the message
        int32_t shaderDescLen = m_pMsg->readInt();
        m_pMsg->skip(shaderDescLen);
    }

    // The current tree is valid. Let rendering proceed using the current message
}

void
ShadeTree::buildNewTree() {
    if (m_pCurrentShader) {
        delete m_pCurrentShader;
    }
    if (m_pCurrentShaderDesc) {
        delete m_pCurrentShaderDesc;
    }

    ESP_LOGI(TAG, "buildNewTree");
    m_pCurrentShaderDesc = new ShaderDesc(m_pMsg);
    if (!m_pCurrentShaderDesc) {
        ESP_LOGE(TAG, "Error: Did not get a shader desc.");
    } else {
        uint8_t * pCursor = m_pCurrentShaderDesc->m_pBuf;
        uint8_t * pEnd = pCursor + m_pCurrentShaderDesc->m_len;

        m_pCurrentShaderDesc->log(TAG);

        // Shaders are expressed in a tree structure so we actually only need to
        // read a single root. However, that root might be a composite that
        // goes on to read further branches.

        // TODO: refactor to extract buffer streaming from Msg.
        Msg shaderConfig;
        shaderConfig.reuse(pCursor, pEnd - pCursor, pEnd - pCursor);

        m_pCurrentShader = Shader::createShaderFromDescrip(m_surface, &shaderConfig);
    }

    if (!m_pCurrentShader) {
        // Aack! Always want a current shader
        ESP_LOGW(TAG, "No current shader. Creating default shader");
        m_pCurrentShader = new DefaultShader(nullptr, nullptr);
    }
}

void
ShadeTree::nextLocalShader() {
    xSemaphoreTake(m_hMsgAccess, portMAX_DELAY);

    if (m_pLocalShader) {
        delete m_pLocalShader;
        m_pLocalShader = nullptr;
    }

    m_localShaderIndex++;

    switch (m_localShaderIndex % 5) {
        case 0: // Everything is just white
            m_pLocalShader = new SolidShader(RgbColor(255));
            break;

        case 1: // Rainbow!
            m_pLocalShader = new RainbowShader(nullptr, nullptr);
            break;

        case 2: // Red!
            m_pLocalShader = new SolidShader(RgbColor(255, 0, 0));
            break;

        case 3: // Green!
            m_pLocalShader = new SolidShader(RgbColor(0, 255, 0));
            break;

        case 4: // Blue!
            m_pLocalShader = new SolidShader(RgbColor(0, 0, 255));
            break;
    }

    xSemaphoreGive(m_hMsgAccess);
}

void
ShadeTree::_handleEvent(esp_event_base_t base, int32_t id, void* data) {

    if (!data) return;
    if (m_timeToDie) return;

    auto evt = (BrainUiEvent*)data;

    if (evt->id == BrainUiEvent::KeyPress) {
        switch(evt->code) {
            case BrainUiEvent::Left :
                ESP_LOGI(TAG, "Key press: nextLocalShader()");
                nextLocalShader();
                break;

            default:
                break;
        }
    }
}
