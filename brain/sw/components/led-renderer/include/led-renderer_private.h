//
// Created by Tom Seago on 2019-06-07.
//

#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#include "esp_err.h"
#include "esp_log.h"

#define TAG "#ledren"

// Rework on Rev D connects the following WROOM module pins:
//   6 (IO34) LED0R <-> 14 (IO12) 3V3IO0
//   7 (IO35) 5VIO_1A <-> 23 (IO15) 5VIO_2A

#define BRN01D_LED1_OUT 32 // IO32_5VIO_0A
#define BRN01D_LED2_OUT 15 // 5VIO_1A

#define BRN01E_LED1_OUT 32
#define BRN01E_LED2_OUT 2

static const int TASK_SHOW_STACK_SIZE = 10240;
static const int TASK_SHOW_PRIORITY = 4;
static const int TASK_SHOW_CORE = 1;

static const int TASK_RENDER_STACK_SIZE = 10240;
static const int TASK_RENDER_PRIORITY = 5;
static const int TASK_RENDER_CORE = 1;

#define FPS 30