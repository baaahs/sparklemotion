//
// Created by Tom Seago on 2019-07-30.
//

#include <stdint.h>
#include <esp_event_base.h>

ESP_EVENT_DECLARE_BASE(BRAIN_UI_BASE);

struct BrainUiEvent{
    enum EventId {
        KeyPress,
    };

    enum KeyCode {
        Left,
        Right,
    };

    EventId id;
    KeyCode code;

    // Modifiers indicate variations in which key was pressed. They are
    // or'ed together into the modifiers value
    static const uint8_t Long = 1;
    uint8_t modifiers;

    BrainUiEvent(EventId id, KeyCode code, uint8_t mods) :
        id(id),
        code(code),
        modifiers(mods)
    {

    }

    void post();

    bool isLong() {
        return modifiers & BrainUiEvent::Long;
    }
};