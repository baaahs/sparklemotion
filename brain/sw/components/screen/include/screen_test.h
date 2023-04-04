//
// Created by Tom Seago on 8/31/21.
//

#pragma once

#include "brain_common.h"
#include "screen.h"

class ScreenTest {
public:
    ScreenTest(Screen& screen);

    void start(TaskDef taskDef);

    [[noreturn]] void _task();

private:
    Screen& m_screen;

    void writeSq1();
};