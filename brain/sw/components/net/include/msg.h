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
//#include <internal/RgbColor.h>
#include "RgbColor.h"

#include "ip_port.h"
#include <brain_common.h>

class BrainHelloMsg;
class BrainShaderMsg;
class PingMsg;

/**
 * A Msg is our base construct in the Pinky/Brain protocol. It is
 * transmitted in one or more UDP datagrams which have a 12 byte header
 * that is used to reassemble the full message buffer.
 *
 * Instead of having separate classes for reading and writing the bytes
 * in a message as a stream all that functionality is including in this
 * class directly.
 *
 */
class Msg {
public:

    // A convenience association for the slinger
    IpPort dest;

    enum class Type : uint8_t {
        BRAIN_HELLO,       // Brain -> Pinky|Mapper
        BRAIN_PANEL_SHADE, // Pinky -> Brain
        MAPPER_HELLO,      // Mapper -> Pinky
        BRAIN_ID_REQUEST,  // Mapper -> Brain
        BRAIN_MAPPING,
        PING,
        USE_FIRMWARE,
    };

    /**
     * The maximum size of a single message.
     */
    static const size_t FRAGMENT_MAX = 1500;
    static const size_t HEADER_SIZE = 12;

    struct Header {
        int16_t id;
        int16_t frameSize;
        int32_t msgSize;
        int32_t frameOffset;
    };

    /**
     * This is how you get a new message instead of creating one on the
     * heap by yourself. The idea is that we might want to pool these to
     * save memory allocations since they're very similarly sized. Right now
     * we don't actually do that, but having everything go through this
     * method will save future time if we need to implement that.
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
     * This should only be used to initialize a Msg that was created on the stack
     * using an existing buffer. Don't call `release()`!
     *
     * TODO: refactor to extract buffer streaming.
     */
    void reuse(uint8_t* buf, size_t capacity, size_t used, size_t cursor = 0) {
        m_buf = buf;
        m_capacity = capacity;
        m_used = used;
        m_cursor = cursor;
    }

    /**
     * Adds one more to the ref count. Must be paired with an eventual release().
     * Since obtain() returns a message with a ref count of 1 this is only needed
     * when a message is being passed around elsewhere.
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

    void setUsed(int used) { m_used = MIN(used, m_capacity); }

    void rewind(int pos = 0) { m_cursor = pos; }
    void skip(int amt) { m_cursor += amt; }
    size_t pos() { return m_cursor; }

    //////////////////

    void writeBoolean(bool b) {
        if (prepCapacity(m_used + 1)) {
            m_buf[m_cursor++] = (uint8_t)(b ? 1 : 0);
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeByte(int8_t v) {
        if (prepCapacity(m_used + 1)) {
            m_buf[m_cursor++] = (uint8_t)v;
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeShort(int16_t i) {
        auto ui = (uint16_t)i;
        if (prepCapacity(m_used + 2)) {
            m_buf[m_cursor++] = (uint8_t)((ui >>  8) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((ui      ) & 0x000000ff);
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeInt(int32_t i) {
        auto ui = (uint32_t)i;
        if (prepCapacity(m_used + 4)) {
            m_buf[m_cursor++] = (uint8_t)((ui >> 24) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((ui >> 16) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((ui >>  8) & 0x000000ff);
            m_buf[m_cursor++] = (uint8_t)((ui      ) & 0x000000ff);
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    void writeBytes(const uint8_t *bytes, size_t len) {
        if (prepCapacity(4 + len)) {
            writeInt(len);

            for (int i = 0; i < len; i++) {
                writeByte(bytes[i]);
            }
        }
    }

    void writeFloat(float d) {

    }

    void writeNullableString(const char* sz) {
        writeBoolean(sz);
        writeString(sz);
    }

    void writeString(const char* sz) {
        if (!sz) return;

        size_t len = strlen(sz);
        size_t xtra = capFor(sz);
        if (prepCapacity(m_used + xtra)) {
            writeInt(len);
            for ( int i = 0; i < len; i++ ) {
                m_buf[m_cursor++] = (uint8_t)sz[i];
            }
            if (m_cursor > m_used) m_used = m_cursor;
        }
    }

    // writeNullableString
    // writeBytes
    // writeNBytes???

    /**
     * Returns the capacity required for a given null terminated C string
     *
     * @param sz
     * @return
     */
    size_t capFor(const char* sz) {
        if (!sz) return 0;

        return 4 + strlen(sz);
    }


    /**
     * Returns the capacity required for a nullable C string
     *
     * @param sz
     * @return
     */
    size_t capForNullable(const char* sz) {
        if (!sz) return 1;

        return 5 + strlen(sz);
    }

    inline bool available(size_t len) const { return m_cursor + len <= m_used; }

    bool readBoolean() {
        if (!available(1)) {
            return false;
        }
        return m_buf[m_cursor++];
    }

    int8_t readByte() {
        if (!available(1)) {
            return false;
        }
        return (int8_t)m_buf[m_cursor++];
    }

    int16_t readShort() {
        if (!available(2)) {
            return 0;
        }
        int16_t out  = m_buf[m_cursor++] << 8;
        out |= m_buf[m_cursor++];

        return out;
    }

    int32_t readInt() {
        if (!available(4)) {
            return 0;
        }
        int32_t out  = m_buf[m_cursor++] << 24;
        out |= m_buf[m_cursor++] << 16;
        out |= m_buf[m_cursor++] <<  8;
        out |= m_buf[m_cursor++];
        return out;
    }

    float readFloat() {
        if (!available(4)) {
            return 0;
        }
        int32_t bits = m_buf[m_cursor++] << 24;
        bits |= m_buf[m_cursor++] << 16;
        bits |= m_buf[m_cursor++] <<  8;
        bits |= m_buf[m_cursor++];

        // Hey, maybe this will work...
        return *((float*)(&bits));
    }

    size_t readString(char* sz, uint32_t max) {
        if (!sz) return 0;

        auto len = (size_t)readInt();
        if (!available(len)) {
            return 0;
        }
        for (int i=0; i<len; i++) {
            sz[i] = (char)m_buf[m_cursor++];
        }
        return len;
    }

    uint32_t readNullableString(bool* notNullOut, char* sz, size_t max) {
        if (!sz) return 0;

        bool notNull = readBoolean();
        if (notNullOut) {
            *notNullOut = notNull;
        }
        if (!notNull) {
            // AKA it _is_ null,,,
            return 0;
        }

        return readString(sz, max);
    }

    /**
     * Copies at most `len` bytes, or at least as many bytes are available to be read.
     *
     * @param dest The buffer to fill.
     * @param len The size of the `dest` buffer.
     * @return The number of bytes read.
     */
    size_t readBytes(uint8_t* dest, size_t len) {
        int32_t available = m_used - m_cursor;
        if (available < 0) {
            return 0;
        }

        size_t toCopy = MIN(len, available);

        memcpy(dest, m_buf + m_cursor, toCopy);
        m_cursor += toCopy;
        return toCopy;
    }

    /**
     * Allocates new memory and produces a copy of a byte array.
     *
     * @param ppDest
     * @return
     */
    size_t copyBytes(uint8_t** ppDest) {
        auto srcLen = (size_t)readInt();

        if (!ppDest) {
            return 0;
        }

        *ppDest = (uint8_t*)malloc(srcLen);
        if (!*ppDest) return 0;
        memcpy(*ppDest, m_buf + m_cursor, srcLen);

        m_cursor += srcLen;

        return srcLen;
    }


    /**
     * Reads bytes without regard to a length specifier. Normally one wants to use
     * the readBytes() call instead of this.
     * @param dest
     * @param len
     */
    void readNBytes(uint8_t* dest, uint32_t len) {
        if (m_cursor + len > m_used) {
            return;
        }

        memcpy(dest, m_buf + m_cursor, len);
        m_cursor += len;
    }

    /**
     * Reads a 4 byte color value, ARGB, discarding the first byte
     *
     * @return
     */
    RgbColor readColor() {
        RgbColor out(0,0,0);

        if (!available(4)) {
            return out;
        }

        // Ignore Alpha
        m_cursor++;

        // Get the RGB
        memcpy((void*)&out, m_buf + m_cursor, 3);
        m_cursor += 3;

        return out;
    }


    Header readHeader() {
        Header h = {};
        h.id = readShort();
        h.frameSize = readShort();
        h.msgSize = readInt();
        h.frameOffset = readInt();
        return h;
    }

    //////////////////
    void injectFragmentingHeader();
    void rewindToPostFragmentingHeader();
    bool isSingleFragmentMessage();

    /**
     * Adds a fragment to a message of the same id. The data from the
     * fragment is copied into the internal storage of the receiving
     * message so the fragment can disappear without worries.
     *
     * In normal circumstances we think the fragments will be in order.
     * If that is not the case then a system wide counter is incremented
     * just so that the condition is visible externally, but the data
     * is still dutifully copied in at the proper location. Presuming the
     * world is a happy place where packets arrive in order, the return
     * value for this function indicates that the packet filled the
     * last position in the message and thus we can sort of assume
     * that the message is maybe probably all here and ready for dispatch.
     *
     * As the subsequent fragments are copied in the internal cursor
     * position isn't changed.
     *
     * @param pFragment
     * @return
     */
    bool addFragment(Msg* pFragment);
    //////////////////

    // Msg* parse();

    virtual void log(const char* name = "Unknown") {
        ESP_LOGD(TAG_MSG, "%s Msg cap=%d used=%d cursor=%d type=%d dest=%s msgId=%d", name, m_capacity, m_used,
                m_cursor, m_capacity > 13 ? m_buf[13] : -1, dest.toString(),
                 (((int) m_buf[0]) & 0xff) * 256 + ((int) m_buf[1] & 0xff));
        ESP_LOG_BUFFER_HEXDUMP(TAG_MSG, m_buf, m_used, ESP_LOG_VERBOSE);
    }

    virtual ~Msg() { }

    uint8_t* cursor() { return m_buf + m_cursor; }

private:
    uint8_t m_refCount = 1;
    uint8_t* m_buf = nullptr;

    size_t m_capacity = 0;
    size_t m_used = 0;
    size_t m_cursor = 0;
};


class BrainHelloMsg : public Msg {
public:
    BrainHelloMsg(const char *brainId,
            const char *panelName,
            const char *firmwareVersion,
            const char *idfVersion) {
        // Need capacity for:
        //      id byte
        //      brainId string
        //      panelName NullableString (adds 1 byte boolean)
        //      firmwareVersion string
        //      idfVersion string
        if (prepCapacity(
                1 +
                capFor(brainId) +
                capForNullable(panelName) +
                capForNullable(firmwareVersion) +
                capForNullable(idfVersion)
                )) {
            writeByte(static_cast<int>(Msg::Type::BRAIN_HELLO));

            writeString(brainId);
            writeNullableString(panelName);
            writeNullableString(firmwareVersion);
            writeNullableString(idfVersion);
        }
    }

    virtual void log(const char* name = "") {
        Msg::log("BrainHelloMsg");
    }
};

class PingMsg : public Msg {
public:
    PingMsg(const uint8_t* data, size_t dataLen, bool isPong = false) {
        if (prepCapacity(1 + 4 + dataLen)) {
            writeByte(static_cast<int>(Msg::Type::PING));
            writeBoolean(isPong);
            writeBytes(data, dataLen);
        }
    }

    virtual void log(const char* name = "") {
        Msg::log("PingMsg");
    }
};


#endif //BRAIN_MSG_H
