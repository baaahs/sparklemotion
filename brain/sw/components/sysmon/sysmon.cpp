//
// Created by Tom Seago on 2019-06-18.
//

#include "sysmon.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "esp_log.h"
#include "esp_heap_caps.h"

#define TAG "#sysmon"

static void task_sysmon(void* pvParameters) {
    ((SysMon*)pvParameters)->_task();
}


void
SysMon::start() {
    auto tcResult  = xTaskCreate(task_sysmon, "sysmon", TASK_SYSMON_STACK_SIZE,
                                 (void*)this, TASK_SYSMON_PRIORITY, nullptr);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create sysmon task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "Sysmon task started");
    }
}

void
SysMon::_task() {
    // Initialization
    // Task actions
    TickType_t xLastWakeTime = xTaskGetTickCount();
    const TickType_t xFrequency = 1 * xPortGetTickRateHz();

    while(1) {
        vTaskDelayUntil( &xLastWakeTime, xFrequency );
        ESP_LOGE(TAG, "===========================================");
        ESP_LOGE(TAG, "free=%d  largest=%d", xPortGetFreeHeapSize(),
                heap_caps_get_largest_free_block(MALLOC_CAP_DEFAULT));
        ESP_LOGE(TAG, "===========================================");
    }

    // Just in case we ever exit, we're supposed to do this.
    // This seems to _work_ more or less, but sure doesn't seem like
    // the safest thing because like, there are callbacks bro!
    vTaskDelete(nullptr);
}