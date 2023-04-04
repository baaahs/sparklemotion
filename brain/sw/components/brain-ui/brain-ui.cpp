//#include <esp_event_base.h>
#include "brain_common.h"
#include "brain-ui-priv.h"
#include "brain-ui.h"
#include "brain-ui-events.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

ESP_EVENT_DEFINE_BASE(BRAIN_UI_BASE);

BrainUI::BrainUI() :
    leftEye(LEDC_CHANNEL_1, BRAIN_GPIO_LED_LEFT_EYE), // Left Eye
    rightEye(LEDC_CHANNEL_0, BRAIN_GPIO_LED_RIGHT_EYE), // Right Eye
    rgbR(LEDC_CHANNEL_2, BRAIN_GPIO_LED_RED), // 34 is bad Rev D, 12 is Rev D rework, 13 is Rev E
    rgbG(LEDC_CHANNEL_3, BRAIN_GPIO_LED_GREEN),
    rgbB(LEDC_CHANNEL_4, BRAIN_GPIO_LED_BLUE),

    btnLeft(BRAIN_GPIO_BUTTON_LEFT, BRAIN_BUTTON_LEFT_ACTIVELOW, *this),
    btnRight(BRAIN_GPIO_BUTTON_RIGHT, BRAIN_BUTTON_RIGHT_ACTIVELOW, *this) // Rev D this is IO2, Rev E is IO34, both are low default
{

}

void static task_brainui(void* pvParameters) {
    ((BrainUI*)pvParameters)->_task();
}


void
BrainUI::_task() {
    TickType_t xLastWakeTime = xTaskGetTickCount();
    const TickType_t xFrequency = pdMS_TO_TICKS(500);

    uint8_t val = 32;
    while(1) {
        vTaskDelayUntil( &xLastWakeTime, xFrequency );

        val += 16;
        // if (val > 255) val = 0;

//        if (!val) {
//           ESP_LOGI(TAG, "Left  Button %s", btnLeft.readDown() ? "down" : "up");
//           ESP_LOGI(TAG, "Right Button %s", btnRight.readDown() ? "down" : "up");
//        }

        // ESP_LOGI(TAG, "Update LEDs to %d", val);
//        leftEye.setValue(val);
//        rightEye.setValue((uint8_t)255-val);

//        rgbR.setValue(val);
//        rgbG.setValue(val);
//        rgbB.setValue((uint8_t)255-val);
//        rgbR.incrementBy(val);
//        rgbG.incrementBy(val);
//        rgbB.incrementBy(val);

        // TODO: Start showing status using the RGB leds, at least until a user interaction
    }

    // Just in case we ever exit, we're supposed to do this.
    // This seems to _work_ more or less, but sure doesn't seem like
    // the safest thing because like, there are callbacks bro!
    vTaskDelete(nullptr);
}

void
BrainUI::start(TaskDef taskDef) {
    // Start all the LEDs first
    leftEye.start();
    rightEye.start();
    rgbR.start();
    rgbG.start();
    rgbB.start();

    // Also some buttons
    btnLeft.start();
    btnRight.start();

    // Then we can do stuff
    leftEye.setValue(255);
    rightEye.setValue(128);

    rgbR.setValue(0);
    rgbG.setValue(0);
    rgbB.setValue(0);

    // Let's go nuts balls and try to create a unique UI state
    // based on the build string plus date and time.
    auto verNum = GlobalConfig.versionHash();

    leftEye.setValue((verNum >> 24) & 0x00ff);
    rightEye.setValue((verNum >> 16) & 0x00ff);
    rgbR.setValue((verNum >> 7) & 0x00ff);
    rgbG.setValue((verNum >> 15) & 0x00ff);
    rgbB.setValue((verNum >> 23) & 0x00ff);

    TaskHandle_t tHandle = NULL;

    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting brainui task...");
    // vTaskDelay(5 * xPortGetTickRateHz());

    tcResult = taskDef.createTask(task_brainui, this, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create brainui task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "BrainUI task started");
    }
}

void
BrainUI::buttonDown(BrainButton& sw, bool longPress) {
    bool isLeft = (&sw == &btnLeft);

    ESP_LOGI(TAG, "%s Button  V  isLong=%d", isLeft ? "Left " : "Right", longPress);

    if (isLeft) {
        leftEye.setValue(leftEye.getValue() ? 0 : 255);
        BrainUiEvent evt(BrainUiEvent::KeyPress, BrainUiEvent::Left, longPress ? 0 : BrainUiEvent::Long);
        evt.post();
    } else {
        rightEye.setValue(rightEye.getValue() ? 0 : 255);
        BrainUiEvent evt(BrainUiEvent::KeyPress, BrainUiEvent::Right, longPress ? 0 : BrainUiEvent::Long);
        evt.post();
    }
};

void
BrainUI::buttonUp(BrainButton& sw) {
    bool isLeft = (&sw == &btnLeft);

    ESP_LOGI(TAG, "%s Button  ^", isLeft ? "Left " : "Right");
};
