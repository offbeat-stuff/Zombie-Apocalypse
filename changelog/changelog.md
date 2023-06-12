## New Features

- Customizable Armor Trims

## Config Changes

```toml
[ArmorTrims]
patterns = ["sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide", "snout", "rib", "spire"]
materials = ["quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst"]
vanillaOnly = true
chance = 0.1

[ArmorTrims.patternsWeights]
weights = [100, 100, 1, 50, 100, 100, 100, 10, 100, 100]
weightsForExtraEntries = 100

[ArmorTrims.materialsWeights]
weights = []
weightsForExtraEntries = 110
```