//
// Created by Tom Seago on 9/18/21.
//

#pragma once

class Display;

/**
 * Widget is the base class for things that draw on a Screen. Widgets
 * have position and size, but these must be handled internally by
 * the Widget itself when drawing into the Screen. Things are very
 * loose and cooperative here on purpose for flexibility and because
 * our ultimate target is relatively constrained.
 *
 * It's not so much about boundaries between potentially competing
 * applications as it is about organization and every widget getting
 * it's own chance to draw.
 *
 * Since the target display hardware holds an image until it changes
 * widgets have this "dirty" concept which they have to manage
 * themselves internally. The idea being that the DisplayManager will
 * scan the list of Widgets and if no one is dirty then great, no work
 * to do, but if any one widget is dirty than the whole widget list
 * needs to draw. This should save a little CPU time which feels
 * prudent given that these displays are typically not the main
 * focus of the overall system.
 */
class Widget {
public:
    Widget *m_pNext;

    Widget() {}
    virtual ~Widget() {};

    void setPos(uint16_t x, uint16_t y) {
        m_x = x;
        m_y = y;
    }

    void setSize(uint16_t width, uint16_t height) {
        m_width = width;
        m_height = height;
    }

    void pos(uint16_t &x, uint16_t &y) {
        x = m_x;
        y = m_y;
    }

    void size(uint16_t &width, uint16_t &height) {
        width = m_width;
        height = m_height;
    }

    virtual void draw(Screen *pScreen) = 0;

    bool isDirty() { return m_dirty; }

protected:
    uint16_t m_x, m_y, m_width, m_height;

    bool m_dirty;
};