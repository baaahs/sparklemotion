//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_BRAIN_H
#define PLAYA_BRAIN_H

#include "msg_handler.h"
#include "msg_slinger.h"

#include "led-renderer.h"
#include "shade-tree.h"

class Brain : public MsgHandler {
public:
    Brain();

    virtual void handleMsg(Msg* pMsg);

    void start();

    void maybeSendHello();

private:
    MsgSlinger m_msgSlinger;

    TimeBase m_timeBase;
    LEDRenderer m_ledRenderer;

    LEDShader* m_ledShader;

    ShadeTree m_shadeTree;

    void msgBrainPanelShade(Msg* pMsg);
    void msgMapperHello(Msg* pMsg);
    void msgBrainMapping(Msg* pMsg);
    void msgPinkyPong(Msg* pMsg);
};


#endif //PLAYA_BRAIN_H
