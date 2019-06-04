//
// Created by Tom Seago on 2019-06-02.
//

#include "msg.h"

#include "net_priv.h"

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
Msg::parse()
{
    Msg* out = NULL;

    if (!m_used || !m_buf) {
        return out;
    }

    switch(m_buf[m_cursor++]) {
        case static_cast<int>(Msg::Type::BRAIN_PANEL_SHADE):
            out = new BrainShaderMsg(this);
            break;

        default:
            ESP_LOGW(TAG, "Unknown message type %d", m_buf[--m_cursor]);
    }

    return out;
}

void Msg::injectFragmentingHeader() {
    static uint8_t messageId = 0;

    if (prepCapacity(m_used + 12)) {
        memcpy(m_buf + 12, m_buf, m_used);

        rewind();
        writeShort(messageId++);
        writeShort(m_used);
        writeInt(m_used);
        writeInt(0);

        m_used += 12;
    }
}

void Msg::rewindToPostFragmentingHeader() {
    m_cursor = 12;
}

bool Msg::isSingleFragmentMessage() {
    m_cursor = 2;
    auto frameSize = readShort();
    auto totalSize = readInt();
    auto offset = readInt();

    return ( offset == 0 && frameSize == totalSize );
}

