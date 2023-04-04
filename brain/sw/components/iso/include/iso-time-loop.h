//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "time-base.h"

/**
 * A time loop keeps track of a loop which repeats in time. Think of things
 * like animations which run for a period of time and then restart back at
 * the beginning.
 *
 * A time loop has a duration, which is generally larger than a second
 * but not necessarily. The duration is measured in milliseconds.
 *
 * A time loop also has a phase, ranging from 0 to 1, where 0 indicates
 * that the time loop boundary is exactly aligned with the brain time epoch
 * which is also used to align output frame boundaries. The phase is
 * an offset from the epoch.
 *
 * Future work here might include uniquely identifying each time loop with
 * an index value as well as "nudging" the phase slightly so that it aligns
 * with some outside system.
 */
class IsoTimeLoop {
public:
    /**
     * Returns the progress value (0.0 to 1.0) for this time loop given
     * the current time.
     *
     * @param at the current time
     * @return a value 0f to 1f indicating position within the loop
     */
    float progress(braintime_t at) {
        uint32_t intProg = (at / 1000) % m_duration;
        float progress = ((float)intProg / (float)m_duration) + m_phaseOffset;
        if (progress > 1.0f) {
            progress -= 1.0f;
        }
        return progress;
    }

    /**
     * The duration in milliseconds
     * @return
     */
    uint32_t duration() {
        return m_duration;
    }

    /**
     * Changes the duration without regard to the position. Setting the duration
     * this way will cause a subsequent progress call to be disjoint with a previous
     * one. It could move backward in position or jump forward in position. This
     * will not change the phase.
     *
     * The duration must be greater than 0.
     *
     * @param duration the duration in milliseconds for this loop
     */
    void setDuration(uint32_t duration) {
        if (duration == 0) return;

        m_duration = duration;
    }

    /**
     * Change the duration by the given amount keeping the same progress value. This
     * will cause a change to the phase. If the resulting duration would be 0 this
     * has no effect.
     *
     * The time the change is occurring must be provided so that the progress at that
     * time can be preserved.
     *
     * @param amount the positive or negative amount in milliseconds
     * @param at the time at which the change is occurring
     */
    void changeDuration(int32_t amount, braintime_t at) {
        if (!amount) return;
        uint32_t newDuration = m_duration + amount;
        if (newDuration == 0) return;

        float oldProgress = progress(at);
        m_duration = newDuration;
        float newProgress = progress(at);
        m_phaseOffset = oldProgress - newProgress;

        // Keep the bounds on the phase thing sane
        adjustPhaseBounds();
    }

    /**
     * Adjust the phase up or down without modifying the duration.
     * @param amount
     */
    void changePhase(float amount) {
        m_phaseOffset += amount;
        adjustPhaseBounds();
    }

private:
    uint32_t m_duration = 1000;

    float m_phaseOffset;

    void adjustPhaseBounds() {
        while (m_phaseOffset > 1.0) {
            m_phaseOffset -= 1.0;
        }
        while (m_phaseOffset < -1.0) {
            m_phaseOffset += 1.0;
        }
    }
};