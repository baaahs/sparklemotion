#pragma once

#include "brain_common.h"

// Include led-renderer.h to get the NeoPixelBus stuff with the ported bits
#include "led-renderer.h"
#include "iso-palette.h"
#include "iso-time-loop.h"

/**
 * An instance of IsoControlState holds the state information that is
 * used by the various shaders during the rendering process. This
 * information is separate from the particular shader which interprets
 * the things here as the shader sees fit.
 *
 * The idea is that this holds things like colors the user has selected,
 * the current palette, geometry, etc.
 */
class IsoControlState {
public:
    IsoControlState(uint8_t maxChoices = 2);

    RgbColor chosenColor(uint8_t ix);
    void setChosenColor(uint8_t ix, RgbColor color);

    IsoPalette& currentPalette() {
        return m_currentPalette;
    }

    /**
     * For looping animations this is the overarching time loop at which
     * the animation should repeat.
     */
    IsoTimeLoop m_masterAnimationLoop;

    // Hacking this in for wigsnatch rotation
    uint8_t orientation = 2; // 0 = z, 1=y, 2=x

private:
    uint8_t m_maxChoices;
    RgbColor *m_pColorChoices;

    IsoPalette &m_currentPalette = gPaletteRYBRainbow;
};