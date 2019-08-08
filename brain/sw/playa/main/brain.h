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

// 960 is 16 * 60. The power supply in the tester boxes is 10A which can support
// 16.6 strands, but for integer-ness we call that 16 and then say that is the
// limit of the tester and only ever address that many pixels.
#define DEFAULT_PIXEL_COUNT 960

class Brain : public MsgHandler {
public:
    Brain();

    virtual void handleMsg(Msg* pMsg);

    void start();

    void maybeSendHello();

private:
    char m_brainId[8];

    MsgSlinger m_msgSlinger;

    TimeBase m_timeBase;

    uint16_t m_pixelCount = DEFAULT_PIXEL_COUNT;
    LEDRenderer m_ledRenderer;

    Surface m_surface = Surface(m_pixelCount);
    ShadeTree m_shadeTree = ShadeTree(&m_surface);

    BrainUI m_brainUI;

    void msgBrainPanelShade(Msg* pMsg);
    void msgBrainIdRequest(Msg* pMsg);
    void msgBrainMapping(Msg* pMsg);
    void msgPinkyPong(Msg* pMsg);

    void sendHello(const IpPort &port);
};


#endif //PLAYA_BRAIN_H
