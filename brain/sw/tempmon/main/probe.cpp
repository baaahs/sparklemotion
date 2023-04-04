//
// Created by Tom Seago on 6/4/20.
//

#include "probe.h"
#include "brain_common.h"

#include "esp_log.h"

#include "freertos/FreeRTOS.h"
#include "freertos/timers.h"
#include "freertos/task.h"

#include "sysmon.h"

static const char* TAG = TAG_PROBE;

static void glue_task(void* pvParameters) {
    ((Probe*)pvParameters)->_task();
}
//
/////////////////////////////////////////////////////////////////
////#include "freertos/FreeRTOS.h"
////#include "esp_system.h"
//#include "driver/gpio.h"
////#include "rom/ets_sys.h"
//#include "esp32/rom/ets_sys.h"
//
//gpio_num_t DS_GPIO;
//int init=0;
///// Sends one bit to bus
//void ds18b20_send(char bit){
//    gpio_set_direction(DS_GPIO, GPIO_MODE_OUTPUT);
//    gpio_set_level(DS_GPIO,0);
//    ets_delay_us(5);
//    if(bit==1)gpio_set_level(DS_GPIO,1);
//    ets_delay_us(80);
//    gpio_set_level(DS_GPIO,1);
//}
//// Reads one bit from bus
//unsigned char ds18b20_read(void){
//    unsigned char PRESENCE=0;
//    gpio_set_direction(DS_GPIO, GPIO_MODE_OUTPUT);
//    gpio_set_level(DS_GPIO,0);
//    ets_delay_us(2);
//    gpio_set_level(DS_GPIO,1);
//    ets_delay_us(15);
//    gpio_set_direction(DS_GPIO, GPIO_MODE_INPUT);
//    if(gpio_get_level(DS_GPIO)==1) PRESENCE=1; else PRESENCE=0;
//    return(PRESENCE);
//}
//// Sends one byte to bus
//void ds18b20_send_byte(char data){
//    unsigned char i;
//    unsigned char x;
//    for(i=0;i<8;i++){
//        x = data>>i;
//        x &= 0x01;
//        ds18b20_send(x);
//    }
//    ets_delay_us(100);
//}
//// Reads one byte from bus
//unsigned char ds18b20_read_byte(void){
//    unsigned char i;
//    unsigned char data = 0;
//    for (i=0;i<8;i++)
//    {
//        if(ds18b20_read()) data|=0x01<<i;
//        ets_delay_us(15);
//    }
//    return(data);
//}
//// Sends reset pulse
//unsigned char ds18b20_RST_PULSE(void){
//    // The OneWire library has these different timings
//    // 480, 70, 410
//    unsigned char PRESENCE;
//
//    // Make sure the wire is high before we start resetting.
//    // If it's not high, then the chip isn't there!
//    uint8_t retries = 125;
//    do {
//        if (--retries == 0) {
//            ESP_LOGD(TAG, "Pin was never high for reset. Connection must be wrong or bad");
//            return 0;
//        }
//        ets_delay_us(2);
//    } while ( !gpio_get_level(DS_GPIO) );
//
//    ESP_LOGD(TAG, "OneWire pin is high with %d retries remaining", retries);
//
//    // Drive output low
//    gpio_set_direction(DS_GPIO, GPIO_MODE_OUTPUT);
//    gpio_set_level(DS_GPIO,0);
//    // ets_delay_us(500);
//    ets_delay_us(480);
//
//    // Now let the pin float and wait for the read interval
//    gpio_set_level(DS_GPIO,1);
//    gpio_set_direction(DS_GPIO, GPIO_MODE_INPUT);
////    ets_delay_us(30);
//    ets_delay_us(70);
//    if(gpio_get_level(DS_GPIO)==0) PRESENCE=1; else PRESENCE=0;
//    ESP_LOGD(TAG, "presence 1 = %d", PRESENCE);
//    ets_delay_us(410);
//
//    // This is enough for the other library...
//
//    // But here, let's make sure it went back to high which means
//    // the chip is there. Basically we are just checking on this
//    // end in addition to the beginning
//
////    ets_delay_us(470);
//    if(gpio_get_level(DS_GPIO)==1) PRESENCE=1; else PRESENCE=0;
//    ESP_LOGD(TAG, "presence 2 = %d", PRESENCE);
//    return PRESENCE;
//}
//// Returns temperature from sensor
//float ds18b20_get_temp(void) {
//    if(init==1){
//        unsigned char check;
//        char temp1=0, temp2=0;
//        check=ds18b20_RST_PULSE();
//        if(check==1)
//        {
//            ESP_LOGD(TAG, "Doing check");
//            ds18b20_send_byte(0xCC);
//            ds18b20_send_byte(0x44);
//            vTaskDelay(750 / portTICK_RATE_MS);
//            check=ds18b20_RST_PULSE();
//            ds18b20_send_byte(0xCC);
//            ds18b20_send_byte(0xBE);
//            temp1=ds18b20_read_byte();
//            temp2=ds18b20_read_byte();
//            check=ds18b20_RST_PULSE();
//            ESP_LOGD(TAG, "temp1=%d temp2=%d", temp1, temp2);
//
//            float temp=0;
//            temp=(float)(temp1+(temp2*256))/16;
//            return temp;
//        }
//        else{return 0;}
//
//    }
//    else{return 0;}
//}
//void ds18b20_init(gpio_num_t GPIO){
//    DS_GPIO = GPIO;
//    gpio_pad_select_gpio(DS_GPIO);
//    init=1;
//}

///////////////////////////////////////////////////////////////

Probe::Probe() :
    m_dhtSensor(GPIO_NUM_19, *this)
{

}

void
Probe::start(TaskDef taskDef) {
//    ds18b20_init(GPIO_NUM_21);

//    m_oneWire.begin(21);
//    m_dallasTemp.setOneWire(&m_oneWire);
//    m_dallasTemp.begin();
//    ESP_LOGI(TAG, "===> Number of temp devices: %d", m_dallasTemp.getDeviceCount());

    m_dhtSensor.start();


    auto tcResult = taskDef.createTask(glue_task, this, nullptr);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create probe task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "Probe task started");
    }
}

void
Probe::dhtSensorReadData(DHTSensor& sensor) {
    ESP_LOGW(TAG, "tempReading at=%lld, rh=%f, tempC=%f, tempF=%f",
             sensor.getReadingAt(), sensor.getRelativeHumidity(),
             sensor.getTemperature(), sensor.getTemperature(true));

}


#define LOOP_SECONDS 10

//void
//Probe::_task() {
//    // Initialization
//    // Task actions
//    TickType_t xLastWakeTime = xTaskGetTickCount();
//    const TickType_t xFrequency = LOOP_SECONDS * xPortGetTickRateHz();
//
////    outputTestVals();
//
//    while(1) {
//        gSysMon.startTiming(TIMING_RENDER);
////        m_dallasTemp.begin();
////        ESP_LOGI(TAG, "===> Number of temp devices: %d", m_dallasTemp.getDeviceCount());
//        this->getTemp();
//        gSysMon.endTiming(TIMING_RENDER);
//        vTaskDelayUntil( &xLastWakeTime, xFrequency );
//    }
//
//    // Just in case we ever exit, we're supposed to do this.
//    // This seems to _work_ more or less, but sure doesn't seem like
//    // the safest thing because like, there are callbacks bro!
//    vTaskDelete(nullptr);
//}

void
Probe::_task() {
    uint32_t notifVal;

    m_dhtSensor.setTaskHandle(xTaskGetCurrentTaskHandle());
    TickType_t xLastWakeTime = xTaskGetTickCount();

    while(1) {
        xTaskNotifyWait(0xffffffff, 0x0, &notifVal, 2);

        if (notifVal) {
            m_dhtSensor._edgeNotify(++m_notifCount);
        }

        m_dhtSensor._edgeTimeout();

        // Maybe we start another read?
        TickType_t xWakeTime = xTaskGetTickCount();
        if (xWakeTime - xLastWakeTime > pdMS_TO_TICKS(10000)) {
            // Do a start time
            xLastWakeTime = xWakeTime;
            //m_dhtSensor.maybeRead();
            getTemp();
        }
    }
}

float
Probe::getTemp() {
//    float t = ds18b20_get_temp();

//    m_dallasTemp.requestTemperatures();
//    float tempC = m_dallasTemp.getTempCByIndex(0);
////
//    ESP_LOGI(TAG, "getTemp() = %f", tempC);
//    return tempC;

    /// DHT Stuff
    m_dhtSensor.maybeRead();

    return 0;
}