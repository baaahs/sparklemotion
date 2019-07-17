#include "shade-tree.h"

#include "esp_log.h"

#include "default-shader.h"
#include "solid-shader.h"
#include "rainbow-shader.h"

#define TAG "#shdtre"

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
}

void
ShadeTree::beginShade(float progress) {
    xSemaphoreTake(m_hMsgAccess, portMAX_DELAY);
    // Assume success
    ESP_LOGW(TAG, "beginShade got semaphore");

    if (m_pCurrentShader && m_pMsg) {
        m_pCurrentShader->begin(m_pMsg, progress);
    } else if (m_pLocalShader) {
        ESP_LOGI(TAG, "beginShade with localShader");
        m_pLocalShader->begin(nullptr, progress);
    } else {
        //ESP_LOGW(TAG, "beginShade but don't have a message and a current shader");
    }
}

void
ShadeTree::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    // If we don't have a pNextMsg set, we don't want to change anything
    // about the pixels
    if (!(m_pCurrentShader || m_pLocalShader)) {
        // However, because the color OUT parameter will absolutely be set as
        // the value of the pixel, we DO have to copy the current color
        // into the output.
        // However However, the pixels are going through a color feature so we
        // have to do this swap on the first two bytes.
        color[0] = currentColor[1];
        color[1] = currentColor[0];
        color[2] = currentColor[2];
        return;
    }

    if (m_pCurrentShader) {
        m_pCurrentShader->apply(indexPixel, color, currentColor);
    } else if (m_pLocalShader) {
        m_pLocalShader->apply(indexPixel, color, currentColor);
    }
}

void
ShadeTree::endShade() {

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
    // ESP_LOGW(TAG, "endShade giving semaphore");
    xSemaphoreGive(m_hMsgAccess);
}

void
ShadeTree::handleMessage(Msg* pMsg){
    // ESP_LOGW(TAG, "handleMessage pMsg=%p", pMsg);
    if (!pMsg) return;

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
    // it here or during say, beginShade(), but this feels a little bit
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

    switch (m_localShaderIndex % 2) {
        case 0: // Boring Red
            m_pLocalShader = new SolidShader(nullptr, nullptr);
            break;

        case 1: // Boring Red
            m_pLocalShader = new RainbowShader(nullptr, nullptr);
            break;
    }

    xSemaphoreGive(m_hMsgAccess);
}