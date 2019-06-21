#pragma once

#include <brain-led.h>

class BrainUI {
public:
    BrainUI();

    void start();
    void _task();

private:
    BrainLed green;
    BrainLed blue;

    BrainLed rgbR;
    BrainLed rgbG;
    BrainLed rgbB;
};