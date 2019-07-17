
#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"

// Totally making this up. For the ESP32 this is in bytes
#define TASK_SYSMON_STACK_SIZE 3024

// Lower numbers are less important. Causes memory allocation though
// so don't want it higher than necessary
#define TASK_SYSMON_PRIORITY 0

#define TIMING_RENDER 0
#define TIMING_LAST 1

#define HISTORY_COUNT 21

#define SYSMON_INTERVAL_SECONDS 10

class SysMon {
public:
    // SysMon();

    void start();

    void _task();

    void startTiming(uint8_t timing);
    void endTiming(uint8_t timing);

    struct TimingInfo {
    public:
        char name[32];
        uint8_t count = 0;
        int64_t average = 0;
        int64_t max = 0;
        int64_t min = 0;
    };

    TimingInfo getInfo(uint8_t timing);

    void logTimings();

private:
    int64_t m_starts[TIMING_LAST];
    int64_t m_history[TIMING_LAST][HISTORY_COUNT];
    int64_t* m_firstHistory[TIMING_LAST];
    int64_t* m_nextHistory[TIMING_LAST];
};

extern SysMon gSysMon;