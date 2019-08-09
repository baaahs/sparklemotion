
#pragma once

#include "brain_common.h"
#include "freertos/semphr.h"

#define TIMING_RENDER 0
#define TIMING_SHOW_OUTPUTS 1
#define TIMING_LAST 2

#define HISTORY_COUNT 21

#define SYSMON_INTERVAL_SECONDS 10

class SysMon {
public:
    void start(TaskDef taskDef);

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

    void addMemInfo();
    void addAppDesc();
    void addMac();

    void logStats();

    // A place to build up our log messages
    char m_szTmp[2000];
    char* m_tmpHead;
    char* m_tmpEnd;
    size_t m_tmpRemaining;
};

extern SysMon gSysMon;