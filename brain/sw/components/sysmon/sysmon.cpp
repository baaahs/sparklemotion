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
#include <esp_timer.h>

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

    // Start all the counter timers so that when an event occurs
    // they can be ended and restarted
    for (uint8_t i = 0; i < COUNTER_LAST; i++) {
        startTiming(i);
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

//    outputTestVals();

    uint32_t count = 0;

    while(1) {
        outputToLog();

        if (count % 2 == 0) {
            outputTaskInfo();
        }

        count++;

        vTaskDelayUntil( &xLastWakeTime, xFrequency );
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

    // Increment the value
    m_values[timing] += 1;

}

SysMon::TimingInfo
SysMon::getInfo(uint8_t timing) {
    TimingInfo info;
    info.min = INT64_MAX;

    switch(timing) {
        case COUNTER_UDP_RECV:
            strncpy(info.name, "UdpRecv", sizeof(info.name)-1);
            break;

        case COUNTER_MSG_LOST:
            strncpy(info.name, "MsgLost", sizeof(info.name)-1);
            break;

        case COUNTER_MSG_BAD_ID:
            strncpy(info.name, "MsgBdId", sizeof(info.name)-1);
            break;

        case COUNTER_MSG_FRAG_OK:
            strncpy(info.name, "MsgFgOk", sizeof(info.name)-1);
            break;

        case COUNTER_MSG_SINGLE_OK:
            strncpy(info.name, "MsgSgOk", sizeof(info.name)-1);
            break;

        case COUNTER_MSG_SENT:
            strncpy(info.name, "MsgSent", sizeof(info.name)-1);
            break;

        case COUNTER_PIXEL_UNDERFLOW:
            strncpy(info.name, "PxUndFl", sizeof(info.name)-1);
            break;

        case TIMING_RENDER:
            strncpy(info.name, "Render ", sizeof(info.name)-1);
            break;

        case TIMING_SHOW_OUTPUTS:
            strncpy(info.name, "ShowOut", sizeof(info.name)-1);
                break;

        case TIMING_OTA_HTTP_READ:
            strncpy(info.name, "OtaRead", sizeof(info.name)-1);
            break;

        case TIMING_OTA_WRITE:
            strncpy(info.name, "OtaWrte", sizeof(info.name)-1);
            break;

        default:
            strncpy(info.name, "Unknown", sizeof(info.name)-1);
            break;
    }

    int64_t* pCursor = m_firstHistory[timing];
    int64_t* pEnd = &(m_history[timing][HISTORY_COUNT]);
    while(pCursor != m_nextHistory[timing]) {
        info.historyCount++;
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

    if (info.historyCount > 0) {
        info.average = info.average / info.historyCount;
    } else {
        // Fix this up for things that haven't happened so the display is nice
        info.min = 0;
    }

    info.value = m_values[timing];

    return info;
}

uint64_t SysMon::increment(uint8_t counter) {
    if (counter > TIMING_LAST) return 0;

    // Stop and restart the timer associated with it
    endTiming(counter);
    startTiming(counter);

    return m_values[counter];
}

/**
 * Outputs a time value and suffix, always occupying 5 digits
 * plus 2 characters for units prior to the suffix. So 7 characters
 * of width total not counting suffix.
 *
 * @param microSeconds - The time value to add to the output buffer
 * @param spacing - A suffix to append after the time value, like spaces or a newline
 */
void SysMon::addTimeValue(int64_t microSeconds, const char* spacing) {
    m_tmpHead = m_tmpEnd - m_tmpRemaining;

    // Let's try to keep this to 4 digits of precision
    if (microSeconds < 1000) {
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                   "%5lldus%s",
                                   microSeconds, spacing);
    } else if (microSeconds < 1000000) {
        // Keep everything integer so we don't get pinned to a core
        // by the floating point unit. Unfortunately that means this
        // gets a little verbose.
        int64_t whole = microSeconds / 1000;
        int64_t frac = microSeconds % 1000;
        uint8_t wholeWidth = 0;
        uint8_t fracWidth = 0;
        if (microSeconds < 10000) {
            wholeWidth = 1;
            fracWidth = 3;
        } else if (microSeconds < 100000) {
            wholeWidth = 2;
            fracWidth = 2;
            frac /= 10;
        } else {
            wholeWidth = 3;
            fracWidth = 1;
            frac /= 100;
        }
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                   "%*lld.%0*lldms%s",
                                   wholeWidth, whole, fracWidth, frac, spacing);
    } else {
        // We're getting lazy, just do seconds
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                   "%5llds %s",
                                   microSeconds / 1000000, spacing);
    }
}

void SysMon::addMetrics() {
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "  Name    Count     Avg      Min      Max     perSec\n"
            "-------- -------  -------  -------  -------  --------\n");
    //                                                     123 /s
    //                1         2         3         4
    //       1234567890123456789012345678901234567890

    for(uint8_t i = 0; i < TIMING_LAST; i++) {
        TimingInfo info = getInfo(i);
        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                   "%.7s  %7llu  ",
                                   info.name, info.value);

        addTimeValue(info.average, "  ");
        addTimeValue(info.min, "  ");
        addTimeValue(info.max, "  ");

        if (info.average == 0) {
            m_tmpHead = m_tmpEnd - m_tmpRemaining;
            m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                       "   0 /s\n");
        } else if (info.average > 1000000) {
            m_tmpHead = m_tmpEnd - m_tmpRemaining;
            m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                       "  <1 /s\n");
        } else {
            m_tmpHead = m_tmpEnd - m_tmpRemaining;
            m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                                       " %3lld /s\n",
                                       1000000 / info.average);

        }
//        "%7s  %5d  %lluS  %lluS  %lluS\n",
//                info.name, info.value, info.average, info.min, info.max);
    }
}

void SysMon::addMemInfo() {
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "   Memory = bytes free( %6ld )    largest( %6d )\n",
            xPortGetFreeHeapSize(),
            heap_caps_get_largest_free_block(MALLOC_CAP_DEFAULT));

}

void SysMon::addAppDesc() {
    auto desc = esp_app_get_description();
    if (!desc) {
        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "Unable to get ota app description\n");
    } else {
        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  version = %s\n",
                desc->version);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  idf_ver = %s\n",
                desc->idf_ver);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "    built = %s %s\n",
                desc->time, desc->date);

        m_tmpHead = m_tmpEnd - m_tmpRemaining;
        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, " ver hash = 0x%08lx\n",
                                   GlobalConfig.versionHash());

        //        m_tmpHead = m_tmpEnd - m_tmpRemaining;
//        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  name    = %s\n", desc->project_name);
//
//        m_tmpHead = m_tmpEnd - m_tmpRemaining;
//        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  time    = %s\n", desc->time);
//
//        m_tmpHead = m_tmpEnd - m_tmpRemaining;
//        m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "  date    = %s\n", desc->date);
//
    }
}


void SysMon::addMac() {
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "            %s\n", GlobalConfig.macStr());
}

void SysMon::outputToLog() {
    m_szTmp[0] = 0;
    m_tmpRemaining = sizeof(m_szTmp) - 1;

    m_tmpEnd = m_szTmp + m_tmpRemaining;

    // Use the next two lines as the template for printing into the output buffer
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining, "\n"
            "====================== SysMon =======================\n");

    // An easy to see tag for debugging OTA
//    m_tmpHead = m_tmpEnd - m_tmpRemaining;
//    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
//            "  George \n");

    addMac();
    addAppDesc();
    addMemInfo();
    addMetrics();

    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "=====================================================\n");

    ESP_LOGE(TAG, "%s", m_szTmp);
}

void SysMon::addTestVal(int64_t v) {
    addTimeValue(v, " = ");

    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                               "%lld\n", v);
}

void SysMon::outputTestVals() {
    m_szTmp[0] = 0;
    m_tmpRemaining = sizeof(m_szTmp) - 1;

    m_tmpEnd = m_szTmp + m_tmpRemaining;

    // Use the next two lines as the template for printing into the output buffer
    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
            "\n"
            "== SysMon Test ==\n");

    addTestVal(0);
    addTestVal(1);
    addTestVal(21);
    addTestVal(321);
    addTestVal(4321);
    addTestVal(54321);
    addTestVal(50021);
    addTestVal(654321);
    addTestVal(7654321);
    addTestVal(87654321);
    addTestVal(987654321);
    addTestVal(1000);
    addTestVal(10000);
    addTestVal(100000);
    addTestVal(1000000);
    addTestVal(10000000);

    m_tmpHead = m_tmpEnd - m_tmpRemaining;
    m_tmpRemaining -= snprintf(m_tmpHead, m_tmpRemaining,
                               "=================================================\n");

    ESP_LOGE(TAG, "%s", m_szTmp);
}

void SysMon::outputTaskInfo() {
    auto count = uxTaskGetNumberOfTasks();

    if (count * 50 > sizeof(m_szTmp) - 1) {
        ESP_LOGE(TAG, "m_szTemp is too small to output task info, count = %d", count);
        return;
    }

    vTaskList(m_szTmp);
    //             IDLE           	R	0	1000	5	0
    ESP_LOGW(TAG, "Task Status:\n"
                  "Name             State Pri StackLeft Num Core\n"
                  "------------------------------------------\n"
                  "%s", m_szTmp);

    vTaskGetRunTimeStats(m_szTmp);
    ESP_LOGW(TAG, "Task total runtime stats:\n"
                  "Name             Ticks       Percent\n"
                  "------------------------------------\n"
                 //show           	194259		<1%
//                  "show           \t194259\t\t<1%\n"
                  "%s", m_szTmp);

}