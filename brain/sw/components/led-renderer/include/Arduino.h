//
// Created by Tom Seago on 2019-06-07.
//

#ifndef BRAIN_ARDUINO_H
#define BRAIN_ARDUINO_H

// Fake out the Arduino includes...

// For things like uint8_t
#include <stdint.h>

// For size_t and mem functions
#include <string.h>

// For the C++ String

#include <math.h>
#define PI 3.1415926535897932384626433832795
#define HALF_PI 1.5707963267948966192313216916398
#define TWO_PI 6.283185307179586476925286766559


//////////
// A little spray and pray
#include <stdbool.h>
// #include <stdint.h>
#include <stdarg.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
// #include <string.h>
#include <inttypes.h>

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/semphr.h"
//#include "esp32-hal.h"
//#include "esp8266-compat.h"
#include "soc/gpio_reg.h"

//#include "stdlib_noniso.h"
//#include "binary.h"

//uint8_t pgm_read_byte(const uint8_t *pAddr);
//uint32_t pgm_read_dword(const uint32_t *pAddr);
#include "pgmspace.h"
#include "esp32-hal.h"


    // For pointers
//typedef uint8_t * PGM_P;

//////
// From esp32-hal.h and friends
#ifndef F_CPU
#define F_CPU (CONFIG_ESP32_DEFAULT_CPU_FREQ_MHZ * 1000000U)
#endif


#endif //BRAIN_ARDUINO_H
