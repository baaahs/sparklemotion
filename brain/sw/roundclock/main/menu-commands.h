//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#define CMD_BRIGHTNESS_LOW 1
#define CMD_BRIGHTNESS_MED 2
#define CMD_BRIGHTNESS_HI  3

#define CMD_MODE_CLOCK      10
#define CMD_MODE_TMINUS     11
#define CMD_MODE_WORDS      12

#define CMD_RAINBOW_ON      20
#define CMD_RAINBOW_OFF     21

#define CMD_COLOR_BASE       200


class MenuListener {
public:
    virtual void doCommand(uint8_t cmd) = 0;
};