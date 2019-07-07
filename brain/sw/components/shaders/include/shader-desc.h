//
// Created by Tom Seago on 2019-06-18.
//

#pragma once

#include "msg.h"
#include <esp_log.h>

class ShaderDesc {
public:
    uint8_t* m_pBuf;
    size_t m_len;

    ShaderDesc(Msg* pMsg) {
        m_len = pMsg->copyBytes(&m_pBuf);
    }

    ~ShaderDesc() {
        if (m_pBuf) {
            free(m_pBuf);
        }
    }

    /**
     * Compares this saved description to a description in a freshly
     * received message. The message read cursor must be at the start
     * position for the description (the beginning of the message).
     *
     * The cursor for the message is not updated regardless of whether
     * the check succeeds or fails.
     *
     * @param msg
     * @return
     */
    bool isSameAs(Msg* pMsg) {
        size_t rewindPos = pMsg->pos();
        
        // Do this as a peek
        size_t dLen = pMsg->readInt();

        // ESP_LOGI(MSG_TAG, "Start isSameAs dLen=%d m_len=%d m_pBuf=%p", dLen, m_len, m_pBuf);
        // Have to be the same length for sure
        if (dLen != m_len) {
            pMsg->rewind(rewindPos);
            return false;
        }

        // Then we just check byte by byte
        uint8_t* pMe = m_pBuf;

        for(int i = 0; i < dLen; i++) {
            int8_t byte = pMsg->readByte();
            if (*pMe != byte) {
                pMsg->rewind(rewindPos);
                return false;
            }
            pMe++;
        }

        // It's okay, they are the same
        // pMsg->log("Same Message??");
        pMsg->rewind(rewindPos);
        return true;
    }

    void log(const char* tag) {
        ESP_LOGI(tag, "Shader Desc: m_len=%d", m_len);
        ESP_LOG_BUFFER_HEX(tag, m_pBuf, m_len);
    }
};


