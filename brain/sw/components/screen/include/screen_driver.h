//
// Created by Tom Seago on 2/26/21.
//

#pragma once

#include "brain_common.h"

/**
 * This struct implements the command messages that are pushed into
 * the screen task queue using the Screen object for handling by the
 * screen task. That means any data that the ScreenDriver needs to
 * do it's job needs to be represented here.
 *
 * In general this maps directly to method parameters for each of the
 * commands.
 */
struct ScreenDriverCommand {

    // See the corresponding method calls on the Screen class for
    // a description of each Kind.
    enum Kind {
        Reset,
        Clear,
        Pattern,
        Send, // Send internal buffer to device
//        Rectangle,
//        Line,
        Pixmap,
    };

    Kind kind;

    enum Pattern {
        Diagonals,
        Checkerboard,
        CheckerboardAlt,
        Corners,
    };

    struct RectangleData {
        bool on;
        uint8_t left;
        uint8_t top;
        uint8_t width;
        uint8_t height;
    };

    struct PixmapData {
        RectangleData rect;

        // This data is assumed to be long enough for the rect and is
        // laid out left to right, top to bottom like a sane person.
        const uint8_t *data;

        bool needsToBeFreed;
    };

    struct ColorData {
        uint8_t fgColor;
        uint8_t bgColor;
    };

    union CommandData {
        enum Pattern patternData;
        ColorData colorData;
        RectangleData rectangleData;
        PixmapData pixmapData;
    } data;
};

/**
 * Subclasses of ScreenDriver implement the actual "do something" for
 * each ScreenCommand message. For probably most implementations of
 * ScreenDriver what you're going to see is sending one or more serial
 * commands to another piece of hardware. And that other piece of
 * hardware could be across any random data network off course.
 *
 * Because ScreedDriver handlers are called in the context of the
 * Screen task they can generally block on I/O or whatever as they
 * deal with a remote device.
 */
class ScreenDriver {
public:
    /**
     * Construct a ScreenDriver with a particular width and height.
     * The ScreenDriver just holds onto these for convenience to be
     * exposed through Screen. The base class doesn't manage any
     * sort of bitmap buffers.
     *
     * @param width
     * @param height
     */
    ScreenDriver(uint16_t width = 0, uint16_t height = 0, uint8_t bpp = 1 );
    virtual ~ScreenDriver();

    virtual void start() = 0;

    uint16_t width() const { return m_width; }
    uint16_t height() const { return m_height; }
    uint16_t bpp() const { return m_bpp; }

    /**
     * This is the main entry to the screen driver. In general subclasses don't
     * need to override this, but they could if they wanted to. The default
     * implementation in this class is to call the specific command functions
     * which is likely all that anyone will ever need to do.
     *
     * @param cmd
     */
    virtual void doCommand(ScreenDriverCommand& cmd);

protected:
    uint16_t m_width;
    uint16_t m_height;
    uint16_t m_bpp;

    virtual void handleReset() = 0;
    virtual void handleClear() = 0;
    virtual void handlePattern(enum ScreenDriverCommand::Pattern pattern) = 0;
    virtual void handleSend() = 0;
    virtual void handlePixmap(ScreenDriverCommand::PixmapData& data) = 0;
};