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
    const char* name;
    uint32_t stack;
    UBaseType_t priority;

    BaseType_t createTask(TaskFunction_t fn, void * const parameters, TaskHandle_t * const taskHandle);
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
    };

    /**
     * The netInput task reads messages from the network and dispatches them
     * through the message handler hierarchy.
     */
    TaskDef netInput = {
            .name = "netInput",
            .stack = 10240,
            .priority = 2,
    };

    /**
     * The netOutput task accepts outgoing messages and sends them over the
     * transport layer one at a time.
     */
    TaskDef netOutput = {
            .name = "netOutput",
            .stack = 10240,
            .priority = 1,
    };
};

/**
 * This global constant holds the actual instance of the BrainTasks structure.
 */
extern const BrainTasks DefaultBrainTasks;
