//
// Created by Tom Seago on 9/22/21.
//

#include "widget_wrangler.h"

static const char* TAG = TAG_SCREEN;

static void glue_task(void *pArg) {
    ((WidgetWrangler*)pArg)->_task();
}

WidgetWrangler::WidgetWrangler() {

}

void
WidgetWrangler::start(TaskDef taskDef) {
    auto tcResult = taskDef.createTask(glue_task, this, nullptr);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create widget wrangler task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "Widget Wrangler task started");
    }
}

void
WidgetWrangler::addWidget(Widget* pWidget) {

    if (!m_pWidgetListHead) {
        m_pWidgetListHead = pWidget;
        return;
    }

    Widget* pCursor = m_pWidgetListHead;
    while(pCursor->m_pNext) {
        pCursor = pCursor->m_pNext;
    }

    pCursor->m_pNext = pWidget;
}

void
WidgetWrangler::_task() {

    TickType_t lastRun = xTaskGetTickCount();
    while(true) {
        // Since ticks are 10ms, Just delay one. This isn't energy
        // efficient or anything but we don't care so whatever. This
        // means we at least get to poll for widget changes at an
        // update frequency that should be reasonable ish???
        vTaskDelayUntil(&lastRun, 1);

        if (anyWidgetIsDirty()) {
            drawAllWidgets();
        }
    }
}


bool
WidgetWrangler::anyWidgetIsDirty() {
    Widget* pCursor = m_pWidgetListHead;

    while(pCursor) {
        if (pCursor->isDirty()) return true;

        pCursor = pCursor->m_pNext;
    }

    return false;
}


void
WidgetWrangler::drawAllWidgets() {
    Widget* pCursor = m_pWidgetListHead;

    while(pCursor) {
        pCursor->draw(m_pScreen);

        pCursor = pCursor->m_pNext;
    }
}


