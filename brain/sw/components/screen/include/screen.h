//
// Created by Tom Seago on 2019-07-10.
//

#pragma once

#include "brain_common.h"

#include <driver/gpio.h>
#include <freertos/FreeRTOS.h>
#include <freertos/portable.h>
#include <freertos/timers.h>
#include <freertos/queue.h>

#include "screen_driver.h"
#include "gfxfont.h"

// A little hack to avoid modifying adafruit font files
#ifndef PROGMEM
#define PROGMEM
#endif

/**
 * The Screen class is the standard interface point for drawing
 * related tasks. It exposes a public API that allows other tasks to
 * draw on the screen, but these drawing commands are handled by
 * the passed in driver subclass and they are called on a separate
 * task that is started for screen drawing.
 *
 * Thus all the methods here are thread safe whereas the ones on
 * ScreenDriver are not. After being initialized with a specific
 * ScreenDriver reference, and then started, the screen task will
 * receive commands via function calls from some sort of display
 * manager type class which wants to draw on the screen. These
 * functions will queue up one or more ScreenDriverCommands into
 * a queue which is then handled by the screen task and the
 * commands are handled by the driver.
 *
 * This has a couple side effects. The first is that these commands
 * generally can't return anything because the communication from
 * Screen to ScreenDriver is unidirectional and asynchronous. The
 * second is that data which is needed by the driver has to be
 * carefully managed in lifecycle. See the write() command in
 * particular with it's data buffer.
 *
 * Since fonts are device independent bitmap buffers they are
 * instantiated externally as const data that is then passed in
 * when that font is needed. By default there is no font data.
 *
 * It should be possible to instantiate multiple ScreenDriver
 * instances and then multiple Screen objects with their own
 * associated tasks to handle them independently but that's not
 * really been tested yet so your mileage may vary.
 *
 * At the generic level Screens have uint16_t's for dimensions
 * and currently use uint32_t's for color. However, many of
 * the displays we expect to support will be monochrome so while
 * this is a little overkill, it will protect us when we start
 * doing color.
 */
class Screen {
public:
    Screen(ScreenDriver& driver);

    void start(TaskDef taskDef);

    uint16_t width() { return m_driver.width(); }
    uint16_t height() { return m_driver.height(); }
    uint8_t bpp() { return m_driver.bpp(); }

    // Screen Commands
    void reset();
    void clear();
    void setPattern(enum ScreenDriverCommand::Pattern patternArg);
    void send();
    void pixmap(uint16_t left, uint16_t top, uint16_t width, uint16_t height, const uint8_t* data, bool needsToBeFreed);
//    void rectangle(uint8_t left, uint8_t top, uint8_t width, uint8_t height);
//    void line();
//    void pixmap();

    /**
     * Sets the active color value which is up to the driver to remember and
     * to use when interpreting data and writes. The meaning of this
     * value differs per the display resolution of the display.
     *
     *   1 bit displays = anything other than 0 is inverted
     *   8 bit displays = presumably a color index thing??
     *   16 bit displays = Er, treat them like 8 or something??
     *
     * This is vague because we only have done 1 bit displays so far.
     *
     * The default value when the screen is reset is 0x01
     *
     * @param color
     */
    void setFgColor(uint8_t color);

    /**
     * Similar to fgColor.
     * @param color
     */
    void setBgColor(uint8_t color);

    void setFont(const GFXfont* font);
    uint8_t print(const char* text, uint16_t x, uint16_t y, bool wrap);

    // Semi-private for ESP32 c world
    [[noreturn]] void _task();
private:
    ScreenDriver& m_driver;

    // A queue of ScreenDriverCommands
    QueueHandle_t m_queue;

    // Font handling
    const GFXfont* m_font;

    uint8_t m_fgColor = 1;
    uint8_t m_bgColor = 0;

    void postToQueue(ScreenDriverCommand* cmd);
    void handleQueue();
};