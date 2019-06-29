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

#define BRN01D_LED_OUT 32 // IO32_5VIO_0A

#define PIXEL_COUNT 32

static const int TASK_LEDREN_STACK_SIZE = 10240;
static const int TASK_LEDREN_PRIORITY = 5;

#define FPS 30