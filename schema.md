# Sparkle Motion schema (WIP)

All record types have `createdAt` (and maybe `modifiedAt`?).

### User

```js
{
  "id": ___,
  "username": "xian",
  "password": "...",      // salted hash of pw
  "name": "Full Name",
  "priority": 123.4,      // e.g. for locking out other users during a performance
}
```

### Gadget Presets

Gadgets offer the ability to save their current values as a user preset, or to load presets.

```js
{
  "id": ___,
  "userId": ___,
  "name": "Pride Flag",
  "type": "Palette",      // the type of the gadget
  "data": { ... },        // type-dependent
}
```

#### Color Gadget Data
```js
{
  "color": 0xfedbca,
}
```

#### Palette Gadget Data
```js
{
  "type": "analogous" | "monochromatic" | "triad" | "complementary" | "compound" | "shades" | "custom",

  // e.g. for type "custom":
  "values": [0xE70000, 0xFF8C00, 0xFFEF00, 0x00811F, 0x0044FF, 0x760089],
}

```

#### Slider Gadget Data
```js
{
  "value": 0.7325,
}
```

### Show Presets

All of the current gadget settings for a show can be saved as a preset, which then shows up in the show selector.

```js
{
  "userId": ___,
  "name": "Awesome",
  "showType": "TexturesAndStuff",
  "gadgets": [
    {
      "id": "texture",    // the show's id for the gadget
      "type": "Texture",  // the type of the gadget
      "data": { "file": "/textures/tiger-print.png" }, // type-dependent
    }
  ]
}
```

### Moving Head Positions

These represent a focal point for the moving heads.

Currently I'm thinking the eyes work like this: in the UI, you can specify a base position for moving heads. When
`show.nextFrame()` is called, each moving head is already set to that base position, and it can then be altered
arbitrarily by the show.

These are saved position presets. 

```js
{
  "name": "Headlight Mode",
  "target": { "x": ___, "y": ___, "z": ___ },
}

```

These could also be represented using `pan`/`tilt`, but then each head would need its own entry. That might make sense
for disco ball mode, where only the left eye can hit the disco balls (and the other might point straight up).

### Panel Mapping Data

These are collected on each panel mapping run.

Maybe needs more consideration:
* how does Pinky determine which one is authoritative? (I think we want the one with the greatest area.)
* how do we do admin overrides (e.g. a Brain dies and we need to swap a new one in)?

```js
{
  "surfaceName": "21P",
  "brainId": ___,
  "areaInSqPixels": 12345, // the area of the surface as captured by the camera for this sample
  "image": "/mapping/___", // probably only populated in dev mode
  "outdatedAt": Time,      // or do we just delete? "ignore: true"?
}
```

### Pixel Mapping Data

These are collected on each pixel mapping run.

```js
{
  "surfaceName": "21P",
  "pixels": [
    {
      "hotspotX": 12.3,    // within the surface-space, where (0, 0) is the (bottom, left) of a rectangle which exactly
      "hotspotY": 45.6,    //   contains the normalized surface
      "brightness": 0.7,
      "leftLimitX": 3.1,   // these compose a rectangle containing 90% of the light emitted from the pixel
      "topLimitY": 55.1,
      "rightLimitX": 16.1,
      "bottomLimitY": 3.1,
      "image": "/mapping/___", // probably only populated in dev mode
    }
  ],
  "areaInSqPixels": 12345, // the area of the surface as captured by the camera for this sample
  "image": "/mapping/___", // probably only populated in dev mode
}
```
