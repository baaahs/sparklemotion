#include "led-renderer.h"


#include "led-renderer_private.h"

LEDRenderer::LEDRenderer() :
    m_pixels(24, BRN01D_LED_OUT)
{
    // Do something
}

void static task_ledren(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_task();
}

void
LEDRenderer::start() {
    static uint8_t vParameters = 0;
    TaskHandle_t tHandle = nullptr;
    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting ledren task...");

    tcResult  = xTaskCreate(task_ledren, "ledren", TASK_LEDREN_STACK_SIZE,
                            this, TASK_LEDREN_PRIORITY, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create led render task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "LED task started");
    }

}

void
LEDRenderer::_task() {
    TickType_t xLastWakeTime;
//    const TickType_t xFrequency = 1 * xPortGetTickRateHz();
    const TickType_t xFrequency = pdMS_TO_TICKS(500);

    // Initialization

    xLastWakeTime = xTaskGetTickCount();

    bool isRed = false;

    // Make not so bright for now...
    m_pixels.Begin();
    m_pixels.SetBrightness(32);

    while(1) {
        // Do network stuff
        vTaskDelayUntil( &xLastWakeTime, xFrequency );
        ESP_LOGI(TAG, "LED task loop");

        RgbColor color = isRed ? RgbColor(255, 0, 0) : RgbColor(0, 0, 255);
        m_pixels.ClearTo(color);
        m_pixels.Show();

        isRed = !isRed;
    }

    // Just in case we ever exit, we're supposed to do this
    vTaskDelete(NULL);

}