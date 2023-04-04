//
// Created by Tom Seago on 2019-06-02.
//

#ifndef BRAIN_MSG_SLINGER_H
#define BRAIN_MSG_SLINGER_H

#include "brain_common.h"

#include "esp_event.h"

#include "msg.h"
#include "msg_handler.h"

#include "esp_netif.h"

class IpPort;

class MsgSlinger {
public:
    void start(uint16_t port, TaskDef input, TaskDef output);

    /**
     * Will cause all tasks to exit. They can't be restarted.
     */
    void stop() { m_timeToDie = true; }

    /**
     * Sends a message to a given address and port. A reference is added to the message.
     *
     * @param ipPort
     * @param msg
     * @return
     */
    esp_err_t sendMsg(Msg* msg);

    /**
     * Registers a handler which will receive messages as they arrive.
     *
     * @param handler
     */
    void registerHandler(MsgHandler* handler) { m_handler = handler; }

    void _gotIp(int32_t id, void* data);
    void _inputPump();
    void _handleNetOut(Msg *pMsg);

private:
    bool m_timeToDie = false;

    uint16_t m_port;
    int m_sock = -1;

    ip4_addr_t m_broadcastEth;
    ip4_addr_t m_broadcastSta;

    esp_event_loop_args_t m_argsOutputLoop;
    esp_event_loop_handle_t m_hOutputLoop;

    // There can be only one (for now)
    MsgHandler* m_handler;
    Msg* m_pFraggedMsg;
    int16_t m_nFraggedMsgId;

    void bindSocket();
    void updateBroadcasts();

    void handleNetIn(Msg *pMsg);
    void dispatch(Msg* pMsg);
};


#endif //BRAIN_MSG_SLINGER_H
