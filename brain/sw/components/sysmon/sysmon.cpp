//
// Created by Tom Seago on 2019-06-18.
//

#include "sysmon.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "esp_log.h"
#include "esp_heap_caps.h"
#include "esp_ota_ops.h"
#include <string.h>
#include <inttypes.h>

#define TAG TAG_SYSMON

SysMon gSysMon;

static void glue_task(void* pvParameters) {
    ((SysMon*)pvParameters)->_task();
}


void
SysMon::start(TaskDef taskDef) {
    for(uint8_t i = 0; i < TIMING_LAST; i++) {
        m_nextHistory[i] = m_firstHistory[i] = &(m_history[i][0]);
    }

    auto tcResult = taskDef.createTask(glue_task, this, nullptr);

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
    const TickType_t xFrequency = BRAIN_SYSMON_SECONDS * xPortGetTickRateHz();

    while(1) {
        logStats();
        vTaskDelayUntil( &xLastWakeTime, xFrequency );
//        ESP_LOGE(TAG, "================= Sys Mon ==================");
//        ESP_LOGE(TAG, "             %s", GlobalConfig.macStr());
//        logTimings();
//        ESP_LOGE(TAG, "============================================");
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
        strncpy(info.name, "Render ", sizeof(info.name)-1);
        break;

    case TIMING_SHOW_OUTPUTS:
        strncpy(info.name, "ShowOut", sizeof(info.name)-1);
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
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "Timer   Count Avg    Min    Max\n");

    for(uint8_t i = 0; i < TIMING_LAST; i++) {
        TimingInfo info = getInfo(i);
        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                "%s  %d  %lluS  %lluS  %lluS\n",
                info.name, info.count, info.average, info.min, info.max);
    }
}

void SysMon::addMemInfo() {
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "   Memory : free( %d )    largest( %d )\n",
            xPortGetFreeHeapSize(),
            heap_caps_get_largest_free_block(MALLOC_CAP_DEFAULT));

}

void SysMon::addAppDesc() {
    auto desc = esp_ota_get_app_description();
    if (!desc) {
        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "Unable to get ota app description\n");
    } else {
        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  version = %s\n", desc->version);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  name    = %s\n", desc->project_name);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  time    = %s\n", desc->time);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  date    = %s\n", desc->date);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  idf_ver = %s\n", desc->idf_ver);
    }
}


void SysMon::addMac() {
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "            %s\n", GlobalConfig.macStr());
}

void SysMon::logStats() {
    m_szTmp[0] = 0;
    m_tmpRemaining = sizeof(m_szTmp) - 1;

    m_tmpEnd = m_szTmp + m_tmpRemaining;

    // Use the next two lines as the template for printing into the output buffer
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "\n"
            "====================== SysMon ===================\n");

    // An easy to see tag for debugging OTA
//    m_tmpHead = m_tmpEnd - m_tmpRemaining;
//    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
//            "  George \n");

    addMac();
    addAppDesc();
    addMemInfo();

    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "=================================================\n");

    ESP_LOGE(TAG, "%s", m_szTmp);
}
