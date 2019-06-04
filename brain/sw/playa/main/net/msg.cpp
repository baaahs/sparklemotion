//
// Created by Tom Seago on 2019-06-02.
//

#include "msg.h"

#include "esp_log.h"

static const char* TAG = "msg";

bool
Msg::prepCapacity(size_t atLeast) {
    if (!m_capacity || !m_buf) {
        m_buf = (uint8_t*)malloc(atLeast);
        if (!m_buf) {
            ESP_LOGE(TAG, "OOM in prepCapacity");
        } else {
            m_capacity = atLeast;

        }
    } else if (m_capacity < atLeast) {
        uint8_t* newBuf = (uint8_t *)realloc(m_buf, atLeast);
        if (newBuf) {
            m_buf = newBuf;
            m_capacity = atLeast;
        } else {
            ESP_LOGE(TAG, "OOM in prepCapacity 2");
        }
    } // else it is fine

    return m_capacity >= atLeast;
}

Msg*
Msg::parseAndRelease()
{
    Msg* out = NULL;

    if (!m_used || !m_buf) {
        release();
        return out;
    }

    switch(m_buf[0]) {
        case static_cast<int>(Msg::Type::BRAIN_PANEL_SHADE):
            out = new BrainShaderMsg(this);
            break;

        default:
            ESP_LOGW(TAG, "Unknown message type %d", m_buf[0]);
    }

    release();
    return out;
}