# SharpScale

Fine-grained GUI scale for Minecraft (Fabric). Pick any scale from x0.5 to x8 in 0.25 steps, and the interface stays sharp: text and icons are drawn at the exact fractional scale and snapped to whole pixels, so there is no blur.

- **Minecraft:** 26.3
- **Loader:** Fabric (Fabric Loader 0.19.5+ and Fabric API). Mod Menu is optional.
- **Side:** client only
- **Author:** Adrián García
- **License:** MIT

## Use

- `]` raises the scale, `[` lowers it (rebindable in Controls, Miscellaneous).
- Or open Mod Menu > SharpScale and use the slider.
- The value is stored in `config/sharpscale.txt` as a number of quarter steps (`0` = vanilla).
- The scale is limited so the interface is never smaller than 240x180 GUI units.

## Compatibility

All mixins are optional. If another mod changes the same part of the GUI pipeline, SharpScale disables that part instead of crashing. Tested together in one instance: Sodium, Iris (no shaders), Sodium Extra, Reese's Sodium Options, Mod Menu, FancyMenu, Drippy Loading Screen, Dark Loading Screen, ImmediatelyFast, OptiGUI, Continuity, AppleSkin, BetterF3, Jade, Raised, JEI, Inventory Profiles Next, ShulkerBoxTooltip, Legendary Tooltips, Controlling, Xaero's Minimap and World Map, JourneyMap, Litematica, Tweakeroo, Zoomify, Mouse Tweaks, EaseGUI, SmoothGui and SmoothHUD. Not tested: EMI, Exordium, Detail Armor Bar, Iris with shaders.

## Build

```
./gradlew build
```

The jar is written to `build/libs/`. Requires JDK 25.

## AI disclosure

The source code was written with AI assistance under the author's direction. The idea, design decisions and testing are the author's.

## License

MIT, see `LICENSE`.
