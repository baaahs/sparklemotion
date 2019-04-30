# Sparkle Motion Show API Proposal

## Color

We should probably define what a color is. Are we working in RGB space? ARGB?
CMYK, hex, etc.?

Potential Usage:
```
func color(<color>: someColorValue)
func color(<RGB>: someColorValue)
func color(<CMYK>: someColorValue)
func color(<hex>: someColorValue)
func color(<ARGB>: someColorValue)
```

## Color Palette

Allows setting the color palette of a light show. This ranges from 1 color, which
makes all the LEDs one single color, to 16777216 colors, which is the result of
256 * 256 * 256 colors within the RGB color space.

It may be possible to add an overlay effect over the color palette, which could
produce more than 1 color even though the palette is set to be one color.

Usage:
```
func setColorPalette(<Array of colors>)
```

Example A (sets the lights a uniform color, red):
```
setColorPalette([
  { "r": 255, "g": 0, "b": 0 },
])
```

Example B (sets a color palette with five colors):
```
setColorPalette([
  "#FF8A47",
  "#FC6170",
  "#8CEEEE",
  "#26BFBF",
  "#FFD747",
])
```

Example C (sets a color palette with the gay pride flag colors):
```
setColorPalette([
  { "r": 231, "g": 0, "b": 0 },
  { "r": 255, "g": 140, "b": 0 },
  { "r": 255, "g": 239, "b": 0 },
  { "r": 0, "g": 129, "b": 31 },
  { "r": 0, "g": 68, "b": 255 },
  { "r": 118, "g": 0, "b": 137 },
])
```

## Shaders

TBD

## Set Pixel Color

No idea if this will be useful at all, but sets the individual pixel within a given panel number. I'm not sure how or if we want to handle getting the number of pixels within a given panel?

Usage
```
func setPixelColor(
  <Panel Number>: panel,
  <Pixel Number>: pixel,
  <Color>: color,
)
```

## Set Panel Color

Sets a panel color by the panel number from the sheep model

## Set Panel Neighbors

Sets the color of the panels immediately touching the given panel number

## Get Time

Gets the current UNIX time

## Get Frame Number

Gets the current frame number of a light show
