//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_MSG_SLINGER_H
#define PLAYA_MSG_SLINGER_H

#include "esp_err.h"
#include "freertos/FreeRTOS.h"
#include "freertos/queue.h"
#include "freertos/ringbuf.h"

#include "msg.h"
#include "msg_handler.h"

class IpPort;

class MsgSlinger {
public:
    void start(uint16_t port);

    /**
     * Sends a message to a given address and port. A reference is added to the message.
     *
     * @param ipPort
     * @param msg
     * @return
     */
    esp_err_t sendMsg(IpPort& ipPort, Msg* msg);

    /**
     * Registers a handler which will receive messages as they arrive.
     *
     * @param handler
     */
    void registerHandler(MsgHandler* handler) { m_handler = handler; }

    void inputPump();
    //void outputPump();
    void handleOutputPump(Msg* msg);

private:
    uint16_t m_port;
    int m_sock;
    uint8_t m_buffer[2000];

//    RingbufHandle_t m_hRing;
//    QueueSetHandle_t m_hQueueSet;

    // There can be only one (for now)
    MsgHandler* m_handler;

//    MsgSlinger() {}

};


#endif //PLAYA_MSG_SLINGER_H
