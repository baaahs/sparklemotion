//
// Created by Tom Seago on 12/30/19.
//

#include "cs-nye-clock.h"
#include "time.h"

CSNYEClock::CSNYEClock() :
    CSShader(24, gCircleFont)
{

}

void
CSNYEClock::beginShade(LEDShaderContext* pCtx) {
    CSShader::beginShade(pCtx);

    timeval tv;
    gettimeofday(&tv, nullptr);
    auto time = localtime(&tv.tv_sec);

    if (time->tm_hour == 0) {
        time->tm_hour = 12;
    } else if (time->tm_hour > 12) {
        time->tm_hour -= 12;
    }

    char buf[20];
    sprintf(buf, "  %2d:%02d%02d", time->tm_hour, time->tm_min, time->tm_sec);

    setText(new CSText(buf));
}
