//
// Created by Tom Seago on 12/30/19.
//

#include "menu.h"

#include "cs-text.h"


Menu::Menu(MenuListener& listener) :
        m_listener(listener),
        m_Shader(24, gCircleFont)
{
    m_enabled = false;
    m_Shader.setText(new CSText("Menu"));

    m_topics[0].m_label = "BRTE";
    m_topics[0].m_items[0].label = "LOW";
    m_topics[0].m_items[0].token = CMD_BRIGHTNESS_LOW;
    m_topics[0].m_items[1].label = "MED";
    m_topics[0].m_items[1].token = CMD_BRIGHTNESS_MED;
    m_topics[0].m_items[2].label = "HI";
    m_topics[0].m_items[2].token = CMD_BRIGHTNESS_HI;
    m_topics[0].m_len = 3;

    m_topics[1].m_label = "MODE";
    m_topics[1].m_items[0].label = "CLK";
    m_topics[1].m_items[0].token = CMD_MODE_CLOCK;
    m_topics[1].m_items[1].label = "T-";
    m_topics[1].m_items[1].token = CMD_MODE_TMINUS;
    m_topics[1].m_items[2].label = "WRD";
    m_topics[1].m_items[2].token = CMD_MODE_WORDS;
    m_topics[1].m_len = 3;

    m_topics[2].m_label = "RNBOW";
    m_topics[2].m_items[0].label = "ON";
    m_topics[2].m_items[0].token = CMD_RAINBOW_ON;
    m_topics[2].m_items[1].label = "OFF";
    m_topics[2].m_items[1].token = CMD_RAINBOW_OFF;
    m_topics[2].m_len = 2;

    m_topics[3].m_label = "COLOR";
    m_topics[3].m_items[0].label = " 1";
    m_topics[3].m_items[0].token = CMD_COLOR_BASE;
    m_topics[3].m_items[1].label = " 2";
    m_topics[3].m_items[1].token = CMD_COLOR_BASE +  1;
    m_topics[3].m_items[2].label = " 3";
    m_topics[3].m_items[2].token = CMD_COLOR_BASE +  2;
    m_topics[3].m_items[3].label = " 4";
    m_topics[3].m_items[3].token = CMD_COLOR_BASE +  3;
    m_topics[3].m_items[4].label = " 5";
    m_topics[3].m_items[4].token = CMD_COLOR_BASE +  4;
    m_topics[3].m_items[5].label = " 6";
    m_topics[3].m_items[5].token = CMD_COLOR_BASE +  5;
    m_topics[3].m_items[6].label = " 7";
    m_topics[3].m_items[6].token = CMD_COLOR_BASE +  6;
    m_topics[3].m_items[7].label = " 8";
    m_topics[3].m_items[7].token = CMD_COLOR_BASE +  7;
    m_topics[3].m_items[8].label = " 9";
    m_topics[3].m_items[8].token = CMD_COLOR_BASE +  8;
    m_topics[3].m_items[9].label = "10";
    m_topics[3].m_items[9].token = CMD_COLOR_BASE +  9;
    m_topics[3].m_items[10].label = "11";
    m_topics[3].m_items[10].token = CMD_COLOR_BASE + 10;
    m_topics[3].m_items[11].label = "12";
    m_topics[3].m_items[11].token = CMD_COLOR_BASE + 11;
    m_topics[3].m_items[12].label = "13";
    m_topics[3].m_items[12].token = CMD_COLOR_BASE + 12;
    m_topics[3].m_items[13].label = "14";
    m_topics[3].m_items[13].token = CMD_COLOR_BASE + 13;
    m_topics[3].m_items[14].label = "15";
    m_topics[3].m_items[14].token = CMD_COLOR_BASE + 14;
    m_topics[3].m_items[15].label = "16";
    m_topics[3].m_items[15].token = CMD_COLOR_BASE + 15;

    m_topics[3].m_len = 16;

    m_numTopics = 4;
}


void
Menu::beginShade(LEDShaderContext* pCtx) {
    m_Shader.beginShade(pCtx);
}

void
Menu::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    color[0] = m_changingTopics ? 0 : 255;
    color[1] = m_changingTopics ? 255 : 0;
    color[2] = 0;
    m_Shader.Apply(indexPixel, color, currentColor);
}

void
Menu::endShade() {
    m_Shader.endShade();
}

void
Menu::evtNext() {
    if (m_changingTopics) {
        m_curTopic++;
        if (m_curTopic >= m_numTopics) {
            m_curTopic = 0;
        }
    } else {
        m_topics[m_curTopic].m_curItem++;
        if (m_topics[m_curTopic].m_curItem >= m_topics[m_curTopic].m_len) {
            m_topics[m_curTopic].m_curItem = 0;
        }
    }

    updateText();
}

void
Menu::evtSelect() {
    if (m_changingTopics) {
        m_changingTopics = false;
    } else {
        // Handle the token
        uint8_t cmd =  m_topics[m_curTopic].m_items[m_topics[m_curTopic].m_curItem].token;

        m_listener.doCommand(cmd);
        m_changingTopics = true;
    }

    updateText();
}

void
Menu::updateText() {
    char buf[100];

    sprintf(buf, "%s %s", m_topics[m_curTopic].m_label,
            m_topics[m_curTopic].m_items[m_topics[m_curTopic].m_curItem].label);
    m_Shader.setText(new CSText(buf));
}