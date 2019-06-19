//
// Created by Tom Seago on 2019-06-08.
//

#pragma once

#include <sys/time.h>
#include <freertos/FreeRTOS.h>

typedef struct timeval timeval;

/**
 * We're going to define the type braintime_t to be the number of microseconds since
 * June 1, 2019 UTC. This way we can do simpler calculations at a pretty high resolution
 * without complexity. Does this mean that you can't use the firmware in some number
 * of years - sure. Do I care? Nope. Not at all.
 */
typedef int32_t braintime_t;

#define BRAIN_TIME_EPOCH_SEC 1559347200

#define USEC_IN_SEC 1000000

#define DEFAULT_FPS 30
// #define DEFAULT_FPS 1

class TimeBase {
public:
    void setFPS(uint16_t fps) {
        m_fps = fps;
        m_frameDuration = USEC_IN_SEC / (braintime_t)m_fps;
    }

    uint16_t getFPS() { return m_fps; }

    braintime_t getFrameDuration() { return m_frameDuration; }

    braintime_t currentTime() {
        timeval tv;
        gettimeofday(&tv, nullptr);

        braintime_t out;
        if (tv.tv_sec > BRAIN_TIME_EPOCH_SEC) {
            // If we have a synced clock (probably via SNTP) then remove
            // the epoch time
            out = (tv.tv_sec - BRAIN_TIME_EPOCH_SEC) * USEC_IN_SEC;
        } else {
            // If we don't have a synced clock, assume the clock began at the epoch
            out = tv.tv_sec * USEC_IN_SEC;
        }

        out += tv.tv_usec;

        return out;
    }

    TickType_t ticksToNextFrame() {
        braintime_t now = currentTime();
        return toTicks(nextFrameFrom(now) - now);
    }

    braintime_t nextFrameFrom(braintime_t now) {
        return ((now / m_frameDuration) + 1) * m_frameDuration;
    }

    TickType_t toTicks(braintime_t t) { return t * xPortGetTickRateHz() / USEC_IN_SEC; }

    /**
     * Do integer math to find the location in an interval, such as how far along a count of
     * leds the given time represents.
     *
     * @param at
     * @param duration
     * @param intervalSize
     * @return
     */
    uint16_t posInInterval(braintime_t at, braintime_t duration, uint16_t intervalSize) {
        return ((at % duration) * intervalSize) / duration;
    }

    /**
     * Return an braintime_t representing the given duration specified in more convenient
     * units. Handy for use with `posInInterval` for example.
     *
     * @param seconds
     * @param millis
     * @param usec
     * @return
     */
    braintime_t duration(braintime_t seconds, braintime_t millis = 0, braintime_t usec = 0) {
        return (seconds * USEC_IN_SEC) + (millis * 1000) + usec;
    }

private:
    uint16_t m_fps = DEFAULT_FPS;
    braintime_t m_frameDuration = (USEC_IN_SEC / (braintime_t)DEFAULT_FPS);
};