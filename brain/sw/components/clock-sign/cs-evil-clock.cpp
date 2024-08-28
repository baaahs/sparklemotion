//
// Created by Tom Seago on 8/26/24.
//

#include "cs-evil-clock.h"
#include "time.h"
#include <cmath>

#define TAG TAG_CS

// Constants
const double PI = 3.141592653589793;
const int SECONDS_IN_8_HOURS = 8 * 3600;
const int SECONDS_IN_4_HOURS = 4 * 3600;
const double DAY_IN_SECONDS = 7 * 24 * 3600;

double calculateFrequency(double frequency_value) {
    double f_min = 1.0 / SECONDS_IN_8_HOURS;
    double f_max = 1.0 / SECONDS_IN_4_HOURS;
    return f_min + (f_max - f_min) * frequency_value;
}

void adjustTimeWithSineWave(struct timeval& time_val, double frequency_value, double amplitude) {
    // Calculate current time in seconds since the epoch
    double current_time = time_val.tv_sec + time_val.tv_usec / 1e6;

    // Calculate varying frequency
    double frequency = calculateFrequency(frequency_value);

    // Apply sine wave adjustment
    double sine_adjustment = amplitude * sin(2 * PI * frequency * current_time);

    // Adjust the time_val with sine wave
    double adjusted_time = current_time + sine_adjustment;

    // Update timeval structure
    time_val.tv_sec = static_cast<long>(adjusted_time);
    time_val.tv_usec = static_cast<long>((adjusted_time - time_val.tv_sec) * 1e6);
}

CSEvilClock::CSEvilClock() :
        CSShader(28, g7SegFont)
{

}

void
CSEvilClock::beginShade(LEDShaderContext* pCtx) {
    CSShader::beginShade(pCtx);

    timeval tv;
    gettimeofday(&tv, nullptr);

    // Now be evil
    uint64_t distance = 1725062400 - tv.tv_sec;
    uint64_t before = tv.tv_sec;
//    ESP_LOGE(TAG, "tv.tv_sec = %lld distance = %lld", tv.tv_sec, distance);

    if (distance > 357000) {
        distance = 1;
    }

    uint64_t magnitude = ((357000 - distance) * (3*60*60)) / 357000;
    double freq = (double)distance / (double)3570000 ;
    adjustTimeWithSineWave(tv, freq, magnitude);

    ESP_LOGE(TAG, "before = %lld, tv.tv_sec = %lld, distance = %lld, adjustment=%lld",
             before, tv.tv_sec, distance, tv.tv_sec - before);

    auto time = localtime(&tv.tv_sec);

    if (time->tm_hour == 0) {
        time->tm_hour = 12;
    } else if (time->tm_hour > 12) {
        time->tm_hour -= 12;
    }

    char buf[20];
    buf[4] = 0;
    buf[5] = 0;
//    sprintf(buf, "%02d%02d", time->tm_hour, time->tm_min);
    sprintf(buf, "%2d%02d", time->tm_hour, time->tm_min);

    if (time->tm_sec % 2 == 0) {
        buf[4] = '8';
    }
//    sprintf(buf, "6789");

    setText(new CSText(buf));
}
