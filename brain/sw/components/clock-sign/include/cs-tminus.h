//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "cs-shader.h"

class CSTMinus : public CSShader {
public:
    CSTMinus() : CSShader(24, gCircleFont) {};

    void beginShade(LEDShaderContext* pCtx) override {
        CSShader::beginShade(pCtx);

        timeval tv;
        gettimeofday(&tv, nullptr);
        auto time = localtime(&tv.tv_sec);

        char buf[20];
        if (time->tm_year == 2020) {
            // New year same as last
            sprintf(buf, "T+%2d:%02d%02d",
                    time->tm_hour, time->tm_min, time->tm_sec);
        } else {
            // sub it out!!!
            uint8_t h = 23 - time->tm_hour;
            uint8_t m = 59 - time->tm_min;
            uint8_t s = 59 - time->tm_sec;

            sprintf(buf, "T-%2d:%02d%02d",h,m,s);
        }

        setText(new CSText(buf));
    };
};