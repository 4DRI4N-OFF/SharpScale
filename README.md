# SharpScale

Fine-grained GUI scale for Minecraft (Fabric). Pick any scale from x0.5 to x8 in 0.25 steps. Minecraft bitmap text keeps pixel-aligned rendering; JetBrains Mono uses normal TrueType rendering at the selected fractional scale. Scales below x1 make text very small and are less readable.

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

The scale/render mixins are optional; the font switch is validated for Minecraft 26.3. Optional scale/render hooks can be skipped if a target changes; this is not a guarantee against all mod conflicts. The required font hook must match Minecraft 26.3. Tested together in one instance: Sodium, Iris (no shaders), Sodium Extra, Reese's Sodium Options, Mod Menu, FancyMenu, Drippy Loading Screen, Dark Loading Screen, ImmediatelyFast, OptiGUI, Continuity, AppleSkin, BetterF3, Jade, Raised, JEI, Inventory Profiles Next, ShulkerBoxTooltip, Legendary Tooltips, Controlling, Xaero's Minimap and World Map, JourneyMap, Litematica, Tweakeroo, Zoomify, Mouse Tweaks, EaseGUI, SmoothGui and SmoothHUD. Not tested: EMI, Exordium, Detail Armor Bar, Iris with shaders.

## Build

```
./gradlew build
```

The jar is written to `build/libs/`. Requires JDK 25.

## AI disclosure

The source code was written with AI assistance under the author's direction. The idea, design decisions and testing are the author's.

## License

MIT, see `LICENSE`.

## Game font
JetBrains Mono is the default for standard text throughout the game, including menus, chat, HUD and world labels. In Mod Menu > SharpScale, switch Game font to Minecraft to turn it off without restarting. The choice is saved separately in `config/sharpscale-font.txt`; existing scale settings are unchanged. Special icon, player and custom resource fonts are preserved. Unsupported characters fall back to Minecraft's font.

The unmodified JetBrains Mono font is bundled under SIL OFL 1.1. Its copyright notice and license are in `assets/sharpscale/font/ofl.txt`. SharpScale's own license is unchanged.

1.3.0 QA: all 33 scale-selector positions checked with both fonts in sample text, SharpScale and GuiAnim screens. English and Spanish matrices: 264 screenshots each at 1920x1080. Additional x6.00 through x8.00 matrix: 72 screenshots at 3840x2160, including actual x6.25 through x8.00. A 666-frame isolated-world run covered HUD/chat, inventory gathering, pause and language-list scroll reversals. All screenshots/frames were visually reviewed in contact sheets, with selected native-resolution checks. This does not establish compatibility of the new font with every mod listed above.
