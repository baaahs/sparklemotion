//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_MSG_H
#define PLAYA_MSG_H

#include "esp_log.h"
#include "freertos/FreeRTOS.h"

#include <string.h>
#include <cstdlib>
#include <stdint.h>
#include <esp_types.h>
#include <sys/param.h>

#include "ip_port.h"

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

    void setUsed(size_t used) { m_used = MIN(used, m_capacity); }

    void rewind() { m_cursor = 0; }

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

    //////////////////

    //////////////////

    Msg* parseAndRelease();

    virtual void log() {
        ESP_LOGI("msg", "Msg cap=%d used=%d type=%d", m_capacity, m_used,
                m_capacity ? m_buf[0] : -1);
    }

protected:
    virtual ~Msg() { }

    uint8_t m_refCount = 1;
    uint8_t* m_buf = (uint8_t*)NULL;

    size_t m_capacity = 0;
    size_t m_used = 0;
    size_t m_cursor = 0;
};


class BrainHelloMsg : public Msg {
public:
    BrainHelloMsg(const char* brainId, const char* panelName) {
        if (prepCapacity(capFor(brainId) + capFor(panelName) + 1)) {
            writeByte(static_cast<int>(Msg::Type::BRAIN_HELLO));
            writeString(brainId);
            writeString(panelName);
        }
        rewind();
    }

    virtual void log() {
        ESP_LOGI("msg", "BrainHelloMsg: ");
    }
};

class BrainShaderMsg : public Msg {
public:
    BrainShaderMsg(Msg* pMsg) {
        BrainShaderMsg* pSrc = (BrainShaderMsg*)pMsg;
        m_buf = (uint8_t*)malloc(pSrc->m_used);
        if (!m_buf) {
            ESP_LOGE("msg", "OOM BrainShaderMsg");
            return;
        }

        memcpy(m_buf, pSrc->m_buf, pSrc->m_used);
        m_capacity = pSrc->m_used;
        m_used = pSrc->m_used;

        // TODO: Dig into the message I guess?
        // Or maybe the user will now start calling readXXXX methods maybe...
    }

    virtual void log() {
        ESP_LOGI("msg", "BrainShaderMsg: ");
    }

};


#endif //PLAYA_MSG_H
