//
// Created by Tom Seago on 2019-06-08.
//

#pragma once

#include <brain_common.h>
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

/**
 * The TimeBase is used to determine when to show a frame of data. A frame
 * is a buffer that gets written out to pixels. This happens in a double buffered
 * manner independently of how quickly or slowly frames are actually generated.
 *
 * Because the serial protocol to the pixels runs at a fixed speed, there is a
 * relationship between the number of pixels that are being driven and the maximum
 * frame rate. The frame rate can be specified as either frames per second (FPS) or
 * the duration of a single frame as these are reciprocals. Durations are
 * communicated and manipulated in microseconds and the FPS must be 1 or greater.
 *
 * Frames are locked to the epoch (June 1, 2019) - that is, since the system has
 * a concept of calendar time, the elapsed time since the epoch is divided by
 * the frame duration to find frame boundaries. This means that if you want
 * to synchronize two independent systems to output their frames at the same
 * time, all you need to do is synchronize their underlying system clocks. The
 * unique epoch was chosen so that we can work with microsecond values.
 */
class TimeBase {
public:
    void setFPS(uint16_t fps) {
        m_fps = fps;
        m_frameDuration = USEC_IN_SEC / (braintime_t)m_fps;
    }

    /**
     * Set the duration in microseconds.
     */
    void setDuration(braintime_t duration) {
        m_fps = USEC_IN_SEC / duration;
        m_frameDuration = duration;
    }

    uint16_t getFPS() { return m_fps; }

    /**
     * The frame duration in microseconds.
     */
    braintime_t getFrameDuration() { return m_frameDuration; }

    /**
     * The current time in microseconds since the epoch.
     * @return
     */
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

    /**
     * The number of ticks (useful for the FreeRTOS wait functions) until the next
     * frame boundary. Note the ticks are pretty low resolution, but that's what we
     * need for the waits.
     * @return
     */
    TickType_t ticksToNextFrame() {
        braintime_t now = currentTime();
        return toTicks(nextFrameFrom(now) - now);
    }

    /**
     * At what time will the next frame boundary occur, given a particular start time.
     * @param now
     * @return
     */
    braintime_t nextFrameFrom(braintime_t now) {
        return ((now / m_frameDuration) + 1) * m_frameDuration;
    }

    /**
     * Converts a braintime_t to a number of ticks
     * @param t
     * @return
     */
    TickType_t toTicks(braintime_t t) { return t * xPortGetTickRateHz() / USEC_IN_SEC; }

//    /**
//     * Do integer math to find the location in an interval, such as how far along a count of
//     * leds the given time represents.
//     *
//     * @param at
//     * @param duration
//     * @param intervalSize
//     * @return
//     */
//    uint16_t posInInterval(braintime_t at, braintime_t duration, uint16_t intervalSize) {
//        return ((at % duration) * intervalSize) / duration;
//    }
//
//    /**
//     * Return an braintime_t representing the given duration specified in more convenient
//     * units. Handy for use with `posInInterval` for example.
//     *
//     * @param seconds
//     * @param millis
//     * @param usec
//     * @return
//     */
//    braintime_t duration(braintime_t seconds, braintime_t millis = 0, braintime_t usec = 0) {
//        return (seconds * USEC_IN_SEC) + (millis * 1000) + usec;
//    }



private:
    uint16_t m_fps = BRAIN_DEFAULT_FPS;
    braintime_t m_frameDuration = (USEC_IN_SEC / (braintime_t)BRAIN_DEFAULT_FPS);
};