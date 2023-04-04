//
// Created by Tom Seago on 2019-08-05.
//

#pragma once

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

/**
 * This is our definition structure for task creation. Generally when a singleton object
 * is going to create OS tasks it will have a start() method that takes one or more of
 * these structures.
 */
struct TaskDef {
    /**
     * No task should ever be defined with a priority higher than this. It's
     * an arbitrary number but serves as documentation for the system more
     * than anything else. We don't want it to be too large because of the
     * memory allocation thing. Also, we only need a couple of priorities
     * anyway.
     */
    static const UBaseType_t MAX_PRIORITY = 10;

    const char* name;
    uint32_t stack;
    UBaseType_t priority;
    BaseType_t  coreId; // Generally should be tskNO_AFFINITY;

    BaseType_t createTask(TaskFunction_t fn, void * const parameters, TaskHandle_t * const taskHandle) const;
};

/**
 * This structure holds our global definitions of tasks so that we can easily look
 * in the one file to see what all is going on.
 *
 * The stack size is defined in bytes. We generally use 10k but this should be
 * tuned at some point.
 *
 * The priorities range from 0 - the lowest, up to whatever. Higher numbers mean
 * higher priority. A higher priority task will be chosen to run over a lower
 * priority task if both are in a runnable state. Priorities should not be set
 * arbitrarily high because an array from lowest to highest is created in the
 * system so these should stay fairly packed together.
 *
 * Multiple tasks can have the same priority, so think of this as a Unix "nice"
 * level more than anything. We should group similar tasks at the same priority
 * level.
 */
struct BrainTasks {
    BrainTasks() { }

    /**
     * The net task doesn't actually do anything right now. The idea was that
     * it might need to handle interface changes, but honestly it looks like
     * all of that is inside the apis. Keeping it for the moment though as the
     * network stuff is still a little in flux.
     */
    TaskDef net = {
            .name = "net",
            .stack = 10240,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * The netInput task reads messages from the network and dispatches them
     * through the message handler hierarchy.
     */
    TaskDef netInput = {
            .name = "netInput",
            .stack = 10240,
            .priority = 2,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * The netOutput task accepts outgoing messages and sends them over the
     * transport layer one at a time.
     */
    TaskDef netOutput = {
            .name = "netOutput",
            .stack = 10240,
            .priority = 1,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * The httpd server which is really only used for OTA configuration. The
     * default priority is tskIDLE_PRIORITY+5 which seems really way to high.
     * Default stack is 4096 with no core affinity.
     */
    TaskDef httpd = {
            .name = "httpd",
            .stack = 10240,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * SysMon hangs out in the background and periodically prints interesting
     * data to the console like memory usage. It doesn't actually need much
     * of a stack other than that it's going to do a lot printf stuff.
     */
    TaskDef sysmon = {
            .name = "sysmon",
            .stack = 3024,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * Probe periodically polls the temperature and then broadcasts
     * an event about it
     */
    TaskDef probe = {
            .name = "probe",
            .stack = 3024,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * The screen task handles communication via I2C to oled panels and eventually
     * maybe to other things like LCD panels.
     */
    TaskDef screen = {
            .name = "screen",
            .stack = 3024,
            .priority = 0,
            .coreId = APP_CPU_NUM,
    };

    /**
     * A test animation loop for screens
    */
    TaskDef screenTest = {
            .name = "screen test",
            .stack = 3024,
            .priority = 0,
            .coreId = APP_CPU_NUM,
    };

    /**
     * Probe periodically polls the temperature and then broadcasts
     * an event about it
     */
    TaskDef widgetWrangler = {
            .name = "widgetWrangler",
            .stack = 3024,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * The UI task processes button presses and turns them into key events.
     * It also drives the on-board leds like the eyes and the tri-color
     * led.
     */
    TaskDef ui = {
            .name = "ui",
            .stack = 3000,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };

    /**
     * The show task takes the rendered pixel data and pushes it out to the
     * pixels on a set schedule. It's pretty important thus a high priority.
     */
    TaskDef show = {
            .name = "show",
            .stack = 10240,
            .priority = 4,
            .coreId = APP_CPU_NUM,
    };

    /**
     * The render task is responsible for parsing the "shade" messages,
     * and doing what they say to do. It's decoupled from the act of
     * actually pushing the pixels out to the strand for flexibility.
     * Ultimately we may not need this much flexibility, but for the
     * moment it's there.
     */
    TaskDef render = {
            .name = "render",
            .stack = 10240,
            .priority = 5,
            .coreId = APP_CPU_NUM,
    };

    /**
     * The long term poll task is started by the STA interface when it
     * hits it's max number of immediate reconnects. We don't want to
     * keep reconnecting ALL the time, so this lets us drop from an
     * immediate attempt to a slow poll. It comes and goes in the system
     * based on how connections go.
     *
     * Since it's about networking we put it on the protocol CPU.
     */
     TaskDef longTermSTAPoll = {
             .name = "staLTP",
             .stack = 4096,
             .priority = 1,
             .coreId = PRO_CPU_NUM,
     };

    /**
     * The ota fetcher will go download stuff from a URL and write it
     * to the next ota partition. It seems to conflict with other
     * things so it gets a nice high priority.
     */
    TaskDef otaFetcher = {
            .name = "otaFetcher",
            .stack = 10240,
            .priority = 10,
            .coreId = APP_CPU_NUM,
    };


    /**
     * This task sends the brain hello messages. These were originally sent
     * on the timer task but that would periodically hit a stack overflow, so
     * kind of like sysmon - yet another task.
     *
     */
    TaskDef hello = {
            .name = "hello",
            .stack = 3024,
            .priority = 0,
            .coreId = tskNO_AFFINITY,
    };
};

/**
 * This global constant holds the actual instance of the BrainTasks structure.
 */
extern const BrainTasks DefaultBrainTasks;
