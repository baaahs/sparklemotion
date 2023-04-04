
#pragma once

#include "brain_common.h"
#include "freertos/semphr.h"

// The TIMING_ and COUNTER_ stuff is done as #defines to avoid a
// bunch of C++ typecasting that is just not really very useful.

// Keep the counter timings at the beginning of the list so counter
// ids are always valid timing ids as well.

// After defining a new counter or timing here you also need to
// add it's name to SysMon::getInfo().

#define COUNTER_UDP_RECV        0
#define COUNTER_MSG_LOST        1
#define COUNTER_MSG_BAD_ID      2
#define COUNTER_MSG_FRAG_OK     3
#define COUNTER_MSG_SINGLE_OK   4
#define COUNTER_MSG_SENT        5
#define COUNTER_PIXEL_UNDERFLOW 6

#define COUNTER_LAST            7

#define TIMING_RENDER           COUNTER_LAST
#define TIMING_SHOW_OUTPUTS     (COUNTER_LAST + 1)
#define TIMING_OTA_HTTP_READ    (COUNTER_LAST + 2)
#define TIMING_OTA_WRITE        (COUNTER_LAST + 3)

#define TIMING_LAST             (COUNTER_LAST + 4)

#define HISTORY_COUNT 21

class SysMon {
public:

    /**
     * Initializes SysMon and will start it's task running using the provided
     * definition. It is recommended that it be a pretty low priority, but it
     * does need a non-trivial stack because it does large log output.
     *
     * @param taskDef
     */
    void start(TaskDef taskDef);

    /**
     * The internal task which waits on a timer to periodically output stats.
     * @private
     */
    void _task();

    /**
     * To time a section of code call this with one of the predefined
     * TIMINIG_xxxx values.
     *
     * @param timing
     */
    void startTiming(uint8_t timing);

    /**
     * End the timing of a section of code. There should be one end for
     * every call to startTiming().
     *
     * @param timing
     */
    void endTiming(uint8_t timing);

    /**
     * Data that is retained for each named type of code section.
     */
    struct TimingInfo {
    public:
        char name[32];
        uint8_t historyCount = 0;
        int64_t average = 0;
        int64_t max = 0;
        int64_t min = 0;
        uint64_t value = 0;
    };

    /**
     * Get timing info for one of the named timing sections.
     * @param timing
     * @return
     */
    TimingInfo getInfo(uint8_t timing);

    /**
     * Increment a named counter. This will also cause the associated
     * timer to be recorded so that the time between events can be
     * shown in the regular timing output.
     *
     * @returns The new value of the counter.
     */
    uint64_t increment(uint8_t counter);

    // TODO: Reset counters?

    /**
     * Outputs to the log immediately. Useful if you are about to reboot
     * or something like that and you want the latest data.
     */
    void outputToLog();

private:
    // Timing stuff
    int64_t m_starts[TIMING_LAST];
    int64_t m_history[TIMING_LAST][HISTORY_COUNT];
    int64_t* m_firstHistory[TIMING_LAST];
    int64_t* m_nextHistory[TIMING_LAST];

    // Counter stuff
    int64_t m_values[TIMING_LAST];

    void addMac();
    void addAppDesc();
    void addMemInfo();

    void addTimeValue(int64_t t, const char* spacing);
    void addMetrics();


    void addTestVal(int64_t v);
    void outputTestVals();

    void outputTaskInfo();

    // A place to build up our log messages
    char m_szTmp[2000];
    char* m_tmpHead;
    char* m_tmpEnd;
    size_t m_tmpRemaining;
};

extern SysMon gSysMon;