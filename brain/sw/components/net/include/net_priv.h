//
// Created by Tom Seago on 2019-06-03.
//

#ifndef NET_PRIV_H
#define NET_PRIV_H

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#include "esp_err.h"
#include "esp_log.h"

#define TAG "#   net"

static const int INPUT_STACK = 10240; // Because 10k seems reasonable???
static const int OUTPUT_STACK = 10240; // Because 10k seems reasonable???

// TODO: Manage priorities more centrally perhaps?
static const int INPUT_PRIO = 2;
static const int OUTPUT_PRIO = 1;

// Totally making this up. For the ESP32 this is in bytes
static const int TASK_NET_STACK_SIZE = 10240;

// Lower numbers are less important. Causes memory allocation though
// so don't want it higher than necessary
static const int TASK_NET_PRIORITY = 0;

#endif // NET_PRIV_H
