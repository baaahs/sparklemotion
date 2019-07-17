#pragma once

#include <brain-led.h>
#include <brain-button.h>

class BrainUI : public BrainButtonListener {
public:
    BrainUI();

    void start();
    void _task();

    void buttonDown(BrainButton& sw, bool longPress) override;
    void buttonUp(BrainButton& sw) override;

private:
    BrainLed leftEye;
    BrainLed rightEye;

    BrainLed rgbR;
    BrainLed rgbG;
    BrainLed rgbB;

    BrainButton btnLeft;
    BrainButton btnRight;
};