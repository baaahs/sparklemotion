//
// Created by Tom Seago on 2019-06-18.
//

#include "sysmon.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "esp_log.h"
#include "esp_heap_caps.h"
#include <string.h>
#include <inttypes.h>

#define TAG "#sysmon"

SysMon gSysMon;

static void task_sysmon(void* pvParameters) {
    ((SysMon*)pvParameters)->_task();
}


void
SysMon::start() {
    for(uint8_t i = 0; i < TIMING_LAST; i++) {
        m_nextHistory[i] = m_firstHistory[i] = &(m_history[i][0]);
    }

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
    const TickType_t xFrequency = SYSMON_INTERVAL_SECONDS * xPortGetTickRateHz();

    while(1) {
        vTaskDelayUntil( &xLastWakeTime, xFrequency );
        ESP_LOGE(TAG, "===========================================");
        ESP_LOGE(TAG, "free=%d  largest=%d", xPortGetFreeHeapSize(),
                heap_caps_get_largest_free_block(MALLOC_CAP_DEFAULT));
        logTimings();
        ESP_LOGE(TAG, "===========================================");
    }

    // Just in case we ever exit, we're supposed to do this.
    // This seems to _work_ more or less, but sure doesn't seem like
    // the safest thing because like, there are callbacks bro!
    vTaskDelete(nullptr);
}

void
SysMon::startTiming(uint8_t timing) {
    m_starts[timing] = esp_timer_get_time();
}

void
SysMon::endTiming(uint8_t timing) {
    int64_t duration = esp_timer_get_time() - m_starts[timing];

    // Always store it in "next"
    *(m_nextHistory[timing]) = duration;

    // Advance "next" by one
    m_nextHistory[timing] += 1;

    // Figure out if this wraps etc.
    int64_t* pEnd = &(m_history[timing][HISTORY_COUNT]);
    if (m_nextHistory[timing] == pEnd) {
        m_nextHistory[timing] = &(m_history[timing][0]);
    }

    // When next and first meet after advancement, push first
    // to be one further along. This means in effect that next
    // always points at an unused location, which means that we have
    // HISTORY_COUNT-1 actual values stored.
    if (m_nextHistory[timing] == m_firstHistory[timing]) {
        m_firstHistory[timing] += 1;

        if (m_firstHistory[timing] == pEnd) {
            m_firstHistory[timing] = &(m_history[timing][0]);
        }
    }
}

SysMon::TimingInfo
SysMon::getInfo(uint8_t timing) {
    TimingInfo info;
    info.min = INT64_MAX;

    switch(timing) {
    case TIMING_RENDER:
        strncpy(info.name, "Render", sizeof(info.name)-1);
        break;

    default:
        strncpy(info.name, "Unknown", sizeof(info.name)-1);
        break;
    }

    int64_t* pCursor = m_firstHistory[timing];
    int64_t* pEnd = &(m_history[timing][HISTORY_COUNT]);
    while(pCursor != m_nextHistory[timing]) {
        info.count++;
        info.average += *pCursor;

        if (*pCursor < info.min) {
            info.min = *pCursor;
        }
        if (*pCursor > info.max) {
            info.max = *pCursor;
        }

        pCursor++;
        if (pCursor == pEnd) {
            pCursor = &(m_history[timing][0]);
        }
    }

    if (info.count > 0) {
        info.average = info.average / info.count;
    } else {
        // Fix this up for things that haven't happened so the display is nice
        info.min = 0;
    }

    return info;
}

void
SysMon::logTimings() {
    for(uint8_t i = 0; i < TIMING_LAST; i++) {
        TimingInfo info = getInfo(i);
        ESP_LOGI(TAG, "%s  %d  %" PRId64 "  %" PRId64 "  %" PRId64, info.name, info.count, info.average, info.max, info.min);
    }
}