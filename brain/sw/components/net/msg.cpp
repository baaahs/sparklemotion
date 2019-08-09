//
// Created by Tom Seago on 2019-06-02.
//

#include "msg.h"

#include "net_priv.h"

#undef TAG
#define TAG TAG_MSG

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

void Msg::injectFragmentingHeader() {
    static uint8_t messageId = 0;

    if (prepCapacity(m_used + 12)) {
        size_t msgUsed = m_used;
        memmove(m_buf + 12, m_buf, msgUsed);

        rewind();
        writeShort(messageId++);
        writeShort(msgUsed);
        writeInt(msgUsed);
        writeInt(0);

        m_used = msgUsed + 12;
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

bool Msg::addFragment(Msg* pFragment) {
    if (!pFragment) {
        return false;
    }

    // Slightly inefficient but let's grab the header of this new guy
    pFragment->m_cursor = 0;
    auto head = pFragment->readHeader();

    // A little bit of safety never hurts
    if (head.frameOffset < 0 ||
            head.frameOffset + head.frameSize > m_capacity) {
        // Crazy pants. Don't do it.
        return false;
    }

    // Seems legit, let's copy the memory
    uint8_t* dest = m_buf + head.frameOffset;
    uint8_t* src = pFragment->m_buf + pFragment->m_cursor;

    // We can't use head.frameSize directly because that came from the network
    // and therefore might be a lie. Thus we need to use the amount of
    // data that we received in the fragment directly.
    size_t len = pFragment->m_used - pFragment->m_cursor;

    memcpy(dest, src, len);

    // Now does that packet represent itself as the last?
    if (head.frameOffset + head.frameSize == head.msgSize) {
        // Sure, it's the last
        return true;
    }

    return false;
}