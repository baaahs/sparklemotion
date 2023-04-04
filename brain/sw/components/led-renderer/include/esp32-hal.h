//
// Created by Tom Seago on 2019-06-07.
//

#ifndef BRAIN_ESP32_HAL_H
#define BRAIN_ESP32_HAL_H

#include "esp_log.h"

#define log_v(format, ...) ESP_LOGV( "HAL", format, ##__VA_ARGS__ )
#define log_d(format, ...) ESP_LOGD( "HAL", format, ##__VA_ARGS__ )
#define log_i(format, ...) ESP_LOGI( "HAL", format, ##__VA_ARGS__ )
#define log_w(format, ...) ESP_LOGW( "HAL", format, ##__VA_ARGS__ )
#define log_e(format, ...) ESP_LOGE( "HAL", format, ##__VA_ARGS__ )

#ifdef __cplusplus
extern "C" {
#endif

void yield();

//void pinMode(uint8_t pin, uint8_t mode);
//void digitalWrite(uint8_t pin, uint8_t val);
//
//#define INPUT 0
//#define OUTPUT 1
//#define LOW 0
//#define HIGH 0

#define ESP_REG(addr) *((volatile uint32_t *)(addr))
#define NOP() asm volatile ("nop")

// #define GPIO_PIN_COUNT                  40
#include <driver/gpio.h>
#include "esp32-hal-gpio.h"

uint32_t millis();
uint32_t micros();
void delay(uint32_t amt);

#ifdef __cplusplus
}
#endif

#endif //BRAIN_ESP32_HAL_H
