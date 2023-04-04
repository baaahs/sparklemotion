//
// Created by Tom Seago on 2019-07-10.
//

#include "screen.h"

static const char* TAG = TAG_SCREEN;



static void glue_task(void *pArg) {
    ((Screen*)pArg)->_task();
}

Screen::Screen(ScreenDriver& driver) :
    m_driver(driver)
{
    m_queue = xQueueCreate(100, sizeof(ScreenDriverCommand));
    if (!m_queue) {
        ESP_LOGE(TAG, "OOM: Unable to create send queue");
    }
}

void
Screen::start(TaskDef taskDef) {
    auto tcResult = taskDef.createTask(glue_task, this, nullptr);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create screen task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "Screen task started");
    }
    ESP_LOGD(TAG, "Screen.start() end");
}

[[noreturn]] void
Screen::_task() {
    ESP_LOGD(TAG, "Screen._task()");
    m_driver.start();

    // Queue up our first commands!
    reset();
    setPattern(ScreenDriverCommand::Checkerboard);
    send();

//    TickType_t intervalLength = pdMS_TO_TICKS(1000);
//    TickType_t nextFrameAt = xTaskGetTickCount() + intervalLength;
//    enum ScreenDriverCommand::Pattern nextPattern = ScreenDriverCommand::CheckerboardAlt;

    while(true) {
//        ESP_LOGD(TAG, "Screen._task() handleQueue()");
        // TODO: Flatten this function into here
        handleQueue();

        // Temporary alternating checkerboard animation to make sure the general
        // concept is working
//        TickType_t now = xTaskGetTickCount();
//        if (now > nextFrameAt) {
//            setPattern(nextPattern);
//            blit();
//
//            nextPattern = (nextPattern == ScreenDriverCommand::Checkerboard) ?
//                            ScreenDriverCommand::CheckerboardAlt :
//                            ScreenDriverCommand::Checkerboard;
//            nextFrameAt = now + intervalLength;
//        }
    }
}

/**
 * This receives a single command from the queue and delegates to the driver
 * to handle it. This is the heart of the screen task.
 */
void
Screen::handleQueue() {
    // TaskHandle_t curTask = xTaskGetCurrentTaskHandle();

    // Might as well put this on the static heap instead of the task stack
    static ScreenDriverCommand cmd;

    if (xQueueReceive(m_queue, &cmd, pdMS_TO_TICKS(500))) {
//        ESP_LOGW(TAG, "Received from queue: %p <- &%p (task %p)", pMsg, *(void**)pMsg, curTask);
        m_driver.doCommand(cmd);
        // ESP_LOGI(TAG, "Message handling complete: %p, (task %p)", pMsg, curTask);
    } else {
//        ESP_LOGI(TAG, "No screen message to handle (task %p)", curTask);
    }
}

/**
 * All of the public methods which are thread safe eventually call this method
 * which is the input side of the queuing.
 *
 * @param cmd
 */
void
Screen::postToQueue(ScreenDriverCommand* cmd) {
    // TaskHandle_t curTask = xTaskGetCurrentTaskHandle();

//    ESP_LOGW(TAG, "Posting to queue: %p <- &%p  (task %p)", msg, &msg, curTask);
    if (xQueueSend(m_queue, (void*)cmd, pdMS_TO_TICKS(500)) != pdTRUE) {
        ESP_LOGE(TAG, "Failed to enqueue a screen command!!!!");
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////
//// The public functions that enqueue screen commands

void
Screen::reset() {
    ScreenDriverCommand cmd;
    cmd.kind = ScreenDriverCommand::Reset;

    postToQueue(&cmd);
}

void
Screen::clear() {
    ScreenDriverCommand cmd;
    cmd.kind = ScreenDriverCommand::Clear;

    postToQueue(&cmd);
}

void
Screen::setPattern(enum ScreenDriverCommand::Pattern patternArg) {
    ESP_LOGI(TAG, "setPattern %d", patternArg);

    ScreenDriverCommand cmd;
    cmd.kind = ScreenDriverCommand::Pattern;
    cmd.data.patternData = patternArg;

    postToQueue(&cmd);
}

void
Screen::send() {
    ScreenDriverCommand cmd;
    cmd.kind = ScreenDriverCommand::Send;

    postToQueue(&cmd);
}

void
Screen::pixmap(uint16_t left, uint16_t top, uint16_t width, uint16_t height, const uint8_t* data, bool needsToBeFreed) {
    ScreenDriverCommand cmd;
    cmd.kind = ScreenDriverCommand::Pixmap;

    cmd.data.pixmapData.rect.left = left;
    cmd.data.pixmapData.rect.top = top;
    cmd.data.pixmapData.rect.width = width;
    cmd.data.pixmapData.rect.height = height;
    cmd.data.pixmapData.data = data;
    cmd.data.pixmapData.needsToBeFreed = needsToBeFreed;

    postToQueue(&cmd);
}


//void
//Screen::rectangle(uint8_t left, uint8_t top, uint8_t width, uint8_t height) {
//
//}

void
Screen::setFont(const GFXfont *font) {
    m_font = font;
}

uint8_t
Screen::print(const char* text, uint16_t x, uint16_t y, bool wrap) {
    if (!m_font) {
        ESP_LOGE(TAG, "Attempted to print %s at %d,%d but no font was set", text, x, y);
        return x;
    }

    if (x > m_driver.width()) return x;
    if (y > m_driver.height()) return x;

    // The X and Y are the baseline position
    uint8_t posX = x;
    uint8_t posY = y;

    const char* charCursor = text;
    while(*charCursor) {
        uint16_t c = *charCursor;
        if (c < m_font->first || c > m_font->last) {
            c = m_font->last;
        }

        auto glyph = m_font->glyph[c - m_font->first];

        // TODO: Maybe wrap here if we can't fit this glyph on screen

        pixmap(posX + glyph.xOffset, posY + glyph.yOffset,
              glyph.width, glyph.height,
              m_font->bitmap + glyph.bitmapOffset, false);

        posX += glyph.xAdvance;

        charCursor++;
    }

    return posX;
}