//
// Created by Tom Seago on 2019-08-05.
//

#include "brain_common.h"

const BrainTasks DefaultBrainTasks;

BaseType_t
TaskDef::createTask(TaskFunction_t fn, void * const parameters, TaskHandle_t * const taskHandle) const {
    return xTaskCreate(fn, name, stack, parameters, priority, taskHandle);
}

