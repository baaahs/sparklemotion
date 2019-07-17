#include "brain-ui-priv.h"
#include "brain-ui.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

BrainUI::BrainUI() :
    leftEye(LEDC_CHANNEL_1, 16), // Left Eye
    rightEye(LEDC_CHANNEL_0, 4), // Right Eye
    rgbR(LEDC_CHANNEL_2, 12), // 34 is bad Rev D, 12 is Rev D rework, 13 is Rev E
    rgbG(LEDC_CHANNEL_3, 33),
    rgbB(LEDC_CHANNEL_4, 14),

    btnLeft(GPIO_NUM_0, true, *this),
    btnRight(GPIO_NUM_2, false, *this) // Rev D this is IO2, Rev E is IO34, both are low default
{

}

#define TASK_BRAINUI_STACK_SIZE 2048

// Lower numbers are less important.
#define TASK_BRAINUI_PRIORITY 0

void static task_brainui(void* pvParameters) {
    ((BrainUI*)pvParameters)->_task();
}


void
BrainUI::_task() {
    TickType_t xLastWakeTime = xTaskGetTickCount();
    const TickType_t xFrequency = pdMS_TO_TICKS(250);

    uint8_t val = 0;
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

        rgbR.setValue(val);
        rgbG.setValue(val);
        rgbB.setValue((uint8_t)255-val);
    }

    // Just in case we ever exit, we're supposed to do this.
    // This seems to _work_ more or less, but sure doesn't seem like
    // the safest thing because like, there are callbacks bro!
    vTaskDelete(nullptr);
}

void
BrainUI::start() {
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
    leftEye.setValue(0);
    rightEye.setValue(0);

    rgbR.setValue(0);
    rgbG.setValue(0);
    rgbB.setValue(0);

    TaskHandle_t tHandle = NULL;

    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting brainui task...");
    // vTaskDelay(5 * xPortGetTickRateHz());

    tcResult  = xTaskCreate(task_brainui, "brainui", TASK_BRAINUI_STACK_SIZE,
                            this, TASK_BRAINUI_PRIORITY, &tHandle);

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
    } else {
        rightEye.setValue(rightEye.getValue() ? 0 : 255);
    }
};

void
BrainUI::buttonUp(BrainButton& sw) {
    bool isLeft = (&sw == &btnLeft);

    ESP_LOGI(TAG, "%s Button  ^", isLeft ? "Left " : "Right");
};
