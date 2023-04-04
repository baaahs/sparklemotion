//
// Created by Tom Seago on 2/26/21.
//

#include "screen_driver.h"
#include "brain_common.h"

//static const char* TAG = TAG_SCREEN;

ScreenDriver::ScreenDriver(uint16_t width, uint16_t height, uint8_t bpp) :
    m_width(width), m_height(height), m_bpp(bpp)
{
}

ScreenDriver::~ScreenDriver() {
}

void
ScreenDriver::doCommand(ScreenDriverCommand& cmd) {
    switch(cmd.kind) {
        case ScreenDriverCommand::Reset:
            handleReset();
            break;

        case ScreenDriverCommand::Clear:
            handleClear();
            break;

        case ScreenDriverCommand::Pattern:
            handlePattern(cmd.data.patternData);
            break;

        case ScreenDriverCommand::Send:
            handleSend();
            break;

        case ScreenDriverCommand::Pixmap:
            handlePixmap(cmd.data.pixmapData);
            break;
    }
}