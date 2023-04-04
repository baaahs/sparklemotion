//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "iso-shader-stack.h"
#include "cs-shader.h"
#include "menu-commands.h"

struct MenuItem {
    const char* label;
    uint8_t token;
};

struct MenuTopic {
    const char* m_label;

    MenuItem m_items[20];
    uint8_t m_len;
    uint8_t m_curItem;
};

class Menu : public LEDShader {
public:
    Menu(MenuListener& listener);

    void beginShade(LEDShaderContext* pCtx) override;
    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) override;
    void endShade() override;

    void evtNext();
    void evtSelect();

private:
    MenuListener& m_listener;
    CSShader m_Shader;

    MenuTopic m_topics[10];
    uint8_t m_numTopics;

    bool m_changingTopics = true;
    uint8_t m_curTopic;

    void updateText();
};


