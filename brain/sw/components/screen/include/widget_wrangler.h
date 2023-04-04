//
// Created by Tom Seago on 9/18/21.
//

#pragma once

#include "brain_common.h"
#include "screen.h"
#include "widget.h"

class WidgetWrangler {
public:
    WidgetWrangler();

    void start(TaskDef taskDef);

    void setScreen(Screen *pScreen) {
        m_pScreen = pScreen;
    }

    void addWidget(Widget *pWidget);

    // Semi-private for ESP32 c world
    [[noreturn]] void _task();
private:
    Screen *m_pScreen;

    Widget* m_pWidgetListHead;

    bool anyWidgetIsDirty();
    void drawAllWidgets();
};