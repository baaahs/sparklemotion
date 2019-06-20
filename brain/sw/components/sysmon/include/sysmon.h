
#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/semphr.h"

// Totally making this up. For the ESP32 this is in bytes
#define TASK_SYSMON_STACK_SIZE 3024

// Lower numbers are less important. Causes memory allocation though
// so don't want it higher than necessary
#define TASK_SYSMON_PRIORITY 50

class SysMon {
public:
    // SysMon();

    void start();

    void _task();
private:
};
