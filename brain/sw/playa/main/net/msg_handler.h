//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_MSG_HANDLER_H
#define PLAYA_MSG_HANDLER_H

#define MAX_MSG_SIZE 2000

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_err.h"

#include "msg.h"

/**
 * Will handle UDP messages on a specific port. It is meant to be used by an application
 * task and will effectively take over the task when receiveMessages() is called.
 */
class MsgHandler {
public:
//    /**&
//     * Creates this handler on a specific port
//     */
//    MsgHandler(uint16_t port);
//
//    /**
//     * Will start receiving messages and will never return.
//     */
//    void receiveMessages();
//
//    /**
//     * Subclasses use this to send messages back. If the to field is NULL then the message
//     * will be sent to the broadcast address and xToLen will be interpreted as the port to
//     * send to.
//     *
//     * @param pMsg
//     * @param xLength
//     * @param to
//     * @param xToLen
//     * @return
//     */
//    esp_err_t sendMessage(uint8_t* pMsg, size_t xLength, struct sockaddr* to, size_t xToLen);

    /**
     * Subclasses need to implement this method.
     */
//    virtual void handleMsg(uint8_t* pMsg, size_t xLength, struct sockaddr* pSender) = 0;
    virtual void handleMsg(Msg* pMsg) = 0;

    /**
     * This entry point is called by a new task that is created when receiveMessages() is
     * called. It will not return. It blocks waiting for people to call the sendMessage()
     * API.
     */
//    void sendMessages();

private:
//    uint16_t m_port;
//    char m_buffer[MAX_MSG_SIZE];
//    int m_sock;
//
//    esp_err_t doBind();
//
//    TaskHandle_t hSender = NULL;
//
//
//    void startSending();
};


#endif //PLAYA_MSG_HANDLER_H
