//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_BRAIN_H
#define PLAYA_BRAIN_H

#include "msg_handler.h"
#include "msg_slinger.h"

#include "led-renderer.h"
#include "shade-tree.h"
#include "sysmon.h"
#include "brain-ui.h"

#define DEFAULT_PIXEL_COUNT 60

class Brain : public MsgHandler {
public:
    Brain();

    virtual void handleMsg(Msg* pMsg);

    void start();

    void maybeSendHello();

private:
    MsgSlinger m_msgSlinger;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer;

    Surface m_surface = Surface(m_pixelCount);
    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    BrainUI m_brainUI;

    void msgBrainPanelShade(Msg* pMsg);
    void msgMapperHello(Msg* pMsg);
    void msgBrainMapping(Msg* pMsg);
    void msgPinkyPong(Msg* pMsg);
};


#endif //PLAYA_BRAIN_H
