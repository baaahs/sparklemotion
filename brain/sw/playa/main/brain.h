//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_BRAIN_H
#define PLAYA_BRAIN_H

#include "net/msg_handler.h"
#include "net/msg_slinger.h"

#include "led-renderer.h"

class Brain : public MsgHandler {
public:
    Brain();

    virtual void handleMsg(Msg* pMsg);

    void start();

    void maybeSendHello();

private:
    MsgSlinger m_msgSlinger;
    LEDRenderer m_ledRenderer;
};


#endif //PLAYA_BRAIN_H
