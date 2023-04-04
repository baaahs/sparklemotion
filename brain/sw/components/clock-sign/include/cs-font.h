//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "brain_common.h"

class CSFont {
public:
    /**
     * For a given character, is this pixel on or off?
     * @param c
     * @param pixel
     * @return
     */
    virtual bool isOn(char c, uint8_t pixel);
};

class CSFontCircles : public CSFont {
public:
    bool isOn(char c, uint8_t pixel) override {
        // Convert lowercase to uppercase
        if (c >= 92 && c <= 122) {
            c -= 32;
        }

        uint8_t ix = c - 32;
        if (ix > 58) return false;

        uint32_t glyphData = CSFontCircles::m_font[ix];
        uint32_t bitmask = 1 << pixel;
        return glyphData & bitmask;
    };

private:
    static uint32_t m_font[];
};
