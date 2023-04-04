//
// Created by Tom Seago on 8/31/21.
//

#include "screen_test.h"
#include "cstring"
#include "Fonts/FreeSans9pt7b.h"
#include "Fonts/FreeSerifBoldItalic12pt7b.h"

static const char* TAG = TAG_SCREEN_TEST;

static const uint8_t sq1Data[] = {
        0b11111111, 0b11111111,
        0b11000000, 0b00000001,
        0b10000000, 0b00000001,
        0b10000000, 0b00000001,
        0b10000000, 0b00000001,
        0b10000000, 0b00000001,
        0b10101010, 0b01010101
};

static const uint8_t sq1DataX[] = {
        0b11111111, 0b11111111,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
};

static const uint8_t happyFaceData[] = {
        0b11111111, 0b11111111,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
        0b10000000, 0b00000000,
};

static void st_glue_task(void *pArg) {
    ((ScreenTest*)pArg)->_task();
}

ScreenTest::ScreenTest(Screen& screen) :
    m_screen(screen)
{

}

void
ScreenTest::start(TaskDef taskDef) {
    auto tcResult = taskDef.createTask(st_glue_task, this, nullptr);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create screen test task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "Screen test task started");
    }
    ESP_LOGD(TAG, "ScreenTest.start() end");
}


[[noreturn]] void
ScreenTest::_task() {
    ESP_LOGD(TAG, "ScreenTest._task()");
    // Queue up our first commands!
    m_screen.setPattern(ScreenDriverCommand::Checkerboard);
    m_screen.send();

    m_screen.setFont(&FreeSans9pt7b);

    TickType_t intervalLength = pdMS_TO_TICKS(1000);
//    TickType_t nextFrameAt = xTaskGetTickCount() + intervalLength;
    enum ScreenDriverCommand::Pattern nextPattern = ScreenDriverCommand::CheckerboardAlt;

    TickType_t lastRun = xTaskGetTickCount();
    while(true) {
        vTaskDelayUntil(&lastRun, intervalLength);

//
//        // Temporary alternating checkerboard animation to make sure the general
//        // concept is working
//        TickType_t now = xTaskGetTickCount();
//        if (now > nextFrameAt) {
            m_screen.setPattern(nextPattern);
//            m_screen.clear();
//            writeSq1();

            m_screen.setFont(&FreeSans9pt7b);
            m_screen.print("Hello World", 0, 20, false);

            m_screen.setFont(&FreeSerifBoldItalic12pt7b);
            m_screen.print("Tom is cool", 10, 44, false);

            //            m_screen.setPattern(ScreenDriverCommand::Corners);
            m_screen.send();

            nextPattern = (nextPattern == ScreenDriverCommand::Checkerboard) ?
                          ScreenDriverCommand::CheckerboardAlt :
                          ScreenDriverCommand::Checkerboard;
            nextPattern = ScreenDriverCommand::Corners;
//            nextFrameAt = now + intervalLength;
//        }
//
//        // This is mildly convoluted but whatever
//        vTaskDelayUntil(&now, nextFrameAt - now);
    }
}

void
ScreenTest::writeSq1() {
    void* dataIn = malloc(sizeof(sq1Data));
    if (!dataIn) {
        ESP_LOGE(TAG, "OOM Error passing in bitmap for s1");
        return;
    }
    memcpy(dataIn, sq1Data, sizeof(sq1Data));
    m_screen.pixmap(10,20,16,7, (const uint8_t *)dataIn, true);
}