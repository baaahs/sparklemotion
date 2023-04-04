//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "brain_common.h"

class CSText {
public:
    /**
     * Creates a new CSText copying the characters to an internal
     * buffer.
     *
     * @param cStr
     */
    CSText(const char* cStr) {
        m_buf = strdup(cStr);
        if (!m_buf) return;
        m_len = strlen(m_buf);
    }

    CSText(const char* buf, size_t len) {
        m_buf = (char*)malloc(len);
        if (!m_buf) return;
        memcpy((void*)m_buf, (const void*)buf, len);
        m_len = len;
    }

    ~CSText() {
        free(m_buf);
    }

    size_t len() {
        return m_len;
    }

    char charAt(size_t pos) {
        size_t ix = m_offset + pos;
        if (ix >= m_len) {
            return 0;
        }
        return m_buf[ix];
    }

    void setVisibleLen(uint8_t visLen) {
        m_visLen = visLen;
    }

    void setScrollIntervalMicros(braintime_t micros) {
        m_scrollInterval = micros;
    }

    void scrollLeft() {
        m_offset += 1;
    }

    void reset() {
        m_offset = 0;
    }

    void updateForTime(uint32_t msNow) {
        // No need to scroll for short things
        if (m_len <= m_visLen) return;

        // Set the first scroll time
        if (!m_nextScroll) {
            // the next is one interval from now
            m_nextScroll = msNow + m_scrollInterval;
            return;
        }

        // Are we past the next?
        if (msNow > m_nextScroll) {
            // Gotta scroll or maybe reset eh?
            if (!charAt(0)) {
                reset();
            } else {
                scrollLeft();
            }

            while (msNow > m_nextScroll) {
                m_nextScroll += m_scrollInterval;
            }
        }
    }

private:
    char *m_buf;
    size_t m_len;

    size_t m_offset = 0;

    uint32_t m_nextScroll = 0;
    uint8_t m_visLen = 9;
    uint32_t m_scrollInterval = 1000;
};