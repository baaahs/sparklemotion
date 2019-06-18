//
// Created by Tom Seago on 2019-06-02.
//

#ifndef BRAIN_MSG_HANDLER_H
#define BRAIN_MSG_HANDLER_H

#include "msg.h"

/**
 * Interface class for things that can handle messages - probably just the Brain
 * class.
 */
class MsgHandler {
public:
    /**
     * Subclasses need to implement this method.
     */
    virtual void handleMsg(Msg* pMsg) = 0;

};


#endif //PLAYA_MSG_HANDLER_H
