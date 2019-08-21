//
// Created by Tom Seago on 2019-08-20.
//

#pragma once

#include <brain_common.h>
#include <msg.h>

class ArtMsg : public Msg {
public:
    static ArtMsg* obtain() {
        auto m = new ArtMsg();
        return m;
    }
};