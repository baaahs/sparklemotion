//
// Created by Tom Seago on 2019-06-02.
//

#ifndef BRAIN_MSG_H
#define BRAIN_MSG_H

#include "esp_log.h"
#include "freertos/FreeRTOS.h"

#include <string.h>
#include <cstdlib>
#include <stdint.h>
#include <esp_types.h>
#include <sys/param.h>

#include "ip_port.h"

#define MSG_TAG "#   msg"

class BrainHelloMsg;
class BrainShaderMsg;

class Msg {
public:

    // A convenience association for the slinger
    IpPort dest;

    enum class Type : uint8_t {
        BRAIN_HELLO,
        BRAIN_PANEL_SHADE,
        MAPPER_HELLO,
        BRAIN_ID_REQUEST,
        BRAIN_ID_RESPONSE,
        BRAIN_MAPPING,
        PINKY_PONG,
    };

    /**
     * The maximum size of a single message.
     */
    static const uint16_t FRAGMENT_MAX = 1500;

    /**
     * Get yourself your very own ready to use Msg instance that is backed by a
     * buffer of MAX_MSG_LEN.
     *
     * This method will block until a free buffer becomes available. The returned
     * Msg will have a refCount of 1.
     *
     * @return A ready to use instance
     */
    static Msg* obtain() {
        auto m = new Msg();
        return m;
    }

    /**
     * Adds one more to the ref count. Must be paired with an eventual release()
     */
    void addRef() { m_refCount++; }

    /**
     * Cast your lightly used message back towards the pool of availability. It
     * doesn't actually re-enter the pool until the refCount hits 0.
     */
    void release() {
        m_refCount--;
        if (!m_refCount) {
            if (m_pSrc) {
                m_pSrc->release();
            }

            if (m_buf) {
                free(m_buf);
            }

            delete this;
        }
    }

    bool prepCapacity(size_t atLeast);

    char* buffer() { return (char*)m_buf; }
    size_t capacity() { return m_capacity; }
    size_t used() { return m_used; }

    void setUsed(int used) { m_used = MIN(used, m_capacity); }

    void rewind() { m_cursor = 0; }
    void skip(size_t amt) { m_cursor += amt; }
    size_t pos() { return m_cursor; }

    //////////////////

    void writeBoolean(bool b) {
        if (prepCapacity(m_used + 1)) {
            m_buf[m_cursor++] = b ? 1 : 0;
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeByte(uint8_t v) {
        if (prepCapacity(m_used + 1)) {
            m_buf[m_cursor++] = v;
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeShort(uint16_t i) {
        if (prepCapacity(m_used + 2)) {
            m_buf[m_cursor++] = (uint8_t)((i >>  8) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((i      ) & 0x000000ff);
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeInt(uint32_t i) {
        if (prepCapacity(m_used + 4)) {
            m_buf[m_cursor++] = (uint8_t)((i >> 24) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((i >> 16) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((i >>  8) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((i      ) & 0x000000ff);
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeString(const char* sz) {
        if (!sz) return;

        size_t len = strlen(sz);
        size_t xtra = capFor(sz);
        if (prepCapacity(m_used + xtra)) {
            writeInt(len);
            for ( int i = 0; i < len; i++ ) {
                m_buf[m_cursor++] = 0; // 16 byte chars? Really???
                m_buf[m_cursor++] = (uint8_t)sz[i];
            }
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    size_t capFor(const char* sz) {
        if (!sz) return 0;

        return 4 + (strlen(sz) * 2);
    }

    bool readBoolean() {
        if (m_cursor + 1 > m_used) {
            return false;
        }
        return m_buf[m_cursor++];
    }

    uint8_t readByte() {
        if (m_cursor + 1 > m_used) {
            return false;
        }
        return m_buf[m_cursor++];
    }

    uint16_t readShort() {
        if (m_cursor + 2 > m_used) {
            return 0;
        }
        uint16_t out  = m_buf[m_cursor++] << 8;
        out |= m_buf[m_cursor++];

        return out;
    }

    uint32_t readInt() {
        if (m_cursor + 4 > m_used) {
            return 0;
        }
        uint32_t out  = m_buf[m_cursor++] << 24;
        out |= m_buf[m_cursor++] << 16;
        out |= m_buf[m_cursor++] <<  8;
        out |= m_buf[m_cursor++];
        return out;
    }

    void readBytes(uint8_t* dest, uint32_t len) {
        if (m_cursor + len > m_used) {
            return;
        }

        memcpy(dest, m_buf + m_cursor, len);
        m_cursor += len;
    }

    uint32_t readString(char* sz, uint32_t max) {
        if (!sz) return 0;

        uint32_t len = readInt();
        if (m_cursor + len > m_used) {
            return 0;
        }
        for (int i=0; i<len; i++) {
            m_cursor++; // Stupid double wide chars...
            sz[i] = (char)m_buf[m_cursor++];
        }
        return len;
    }

    //////////////////
    void injectFragmentingHeader();
    void rewindToPostFragmentingHeader();
    bool isSingleFragmentMessage();
    //////////////////

    Msg* parse();

    virtual void log(const char* name = "Unknown") {
        ESP_LOGI(MSG_TAG, "%s Msg cap=%d used=%d type=%d dest=%s", name, m_capacity, m_used,
                m_capacity ? m_buf[0] : -1, dest.toString());
        ESP_LOG_BUFFER_HEXDUMP(MSG_TAG, m_buf, m_used, ESP_LOG_INFO);
    }

protected:
    virtual ~Msg() { }

    uint8_t m_refCount = 1;
    uint8_t* m_buf = (uint8_t*)NULL;

    size_t m_capacity = 0;
    size_t m_used = 0;
    size_t m_cursor = 0;

    // This points at our original source data (or not)
    Msg* m_pSrc;
};


class BrainHelloMsg : public Msg {
public:
    BrainHelloMsg(const char* brainId, const char* panelName) {
        if (prepCapacity(capFor(brainId) + capFor(panelName) + 1)) {\
            writeByte(static_cast<int>(Msg::Type::BRAIN_HELLO));

            writeString(brainId);
            writeString(panelName);
        }
        rewind();
    }

    virtual void log(const char* name = "") {
        Msg::log("BrainHelloMsg");
    }
};

class BrainShaderMsg : public Msg {
    size_t m_shaderDescOff;
    size_t m_shaderDescLen;

public:
    BrainShaderMsg(Msg* pMsg) {
        if (!pMsg) return;

        m_pSrc = pMsg;
        m_pSrc->addRef();

        m_shaderDescOff = m_pSrc->pos();
        m_shaderDescLen = m_pSrc->readInt();
        m_pSrc->skip(m_shaderDescLen);
    }

    virtual void log(const char* name = "") {
        Msg::log("BrainShaderMsg");
    }

};


#endif //BRAIN_MSG_H
