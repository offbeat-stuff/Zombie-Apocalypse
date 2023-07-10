**Features**
-   Custom logic to dynamically spawn zombies.
-   Zombies don't burn in daylight (cause it would be weird for a zombie apocalypse).
-   Equip randomly enchanted armor, including unique armor trims, to spawned zombies.
-   Impose random status effects on zombies upon spawning.
-   Configure the time range during which zombies spawn.
-   Random Infinite Status effects (Currently finite).
-   Enhanced configuration options for providing weapon lists.
-   Zombies that inflict burning or frost effects on hit.

---

**Planned Features**
-   Explosion zombies that exclusively damage entities.
-   Assisting Zombies (supporting players or fellow zombies).
-   Zombies that hit other zombies to propel them closer to you.
-   Dancing zombie that continually spawns new zombies.

---

I plan to keep this mod fully server side with no client side functionality.

No custom resourcepack stuff too.

I do plan to add the ability to specify zombies from other mods in the config.

Try [Tissou's Zombie Pack](https://www.curseforge.com/minecraft/texture-packs/tissous-zombie-pack-optifine-1-7x-1-19) with this pack for the feels.

---

Latest Default Config

```toml
zombiesBurnInSunlight = false
doScream = true
frostZombieChance = 0.01
fireZombieChance = 0.01
```

If zombies burn in sunlight then they wont spawn under the sky in the day.

Frost Zombies give freezing effect on hit.

Fire Zombie set player on fire on hit.

Scream is just a on screen red message for now, will add sound later.


```toml
[spawn]
spawnInstantly = false
vanillaSpawnRestrictionOnFoot = true
checkIfBlockBelowAllowsSpawning = true
lightLevel = 15
minPlayerDistance = 16.0
maxZombieCount = 150
allowedDimensions = ["overworld", "ther_nether", "the_end"]

[spawn.instantSpawning]
maxSpawnAttemptsPerTick = 100
maxSpawnsPerTick = 10

[spawn.axisSpawnParameters]
chance = 0.1
min = 16
max = 48

[spawn.planeSpawnParameters]
chance = 0.1
min = 16
max = 48

[spawn.boxSpawnParameters]
chance = 0.1
min = 24
max = 64

[spawn.timeRange]
min = 1000
max = 13000
```

LightLevel will stay between 0 to 15.

`checkIfBlockBelowAllowsSpawning` will block spawning if block state does not allow spawning zombies.

`vanillaSpawnRestrictionOnFoot` will check for redstone power and `PREVENT_MOB_SPAWNING_INSIDE`,`INVALID_SPAWN_INSIDE` block tags.

Instant spawning basically spawns them while disregarding the chances.

Time Range value will stay between 0 and 23999.

> Note: day is 1000 and midnight is 18000.

```toml
[Armor]
helmets = ["netherite", "diamond", "iron", "gold", "chainmail", "leather", "turtle"]
chestplates = ["netherite", "diamond", "iron", "gold", "chainmail", "leather"]
leggings = ["netherite", "diamond", "iron", "gold", "chainmail", "leather"]
boots = ["netherite", "diamond", "iron", "gold", "chainmail", "leather"]

[Armor.chancesPerSlot]
chances = [0.1, 0.1, 0.1, 0.1]
defaultChance = 0.1

[Armor.materialWeights]
weights = [1, 10, 100, 50, 50, 100]
weightsForExtraEntries = 10
```

Slots are in order: `[head,chest,legs,boots]` 

```toml
[Weapon]
swords = ["netherite", "diamond", "iron", "gold", "stone", "wood"]
shovels = ["netherite", "diamond", "iron", "gold", "stone", "wood"]
pickaxes = ["netherite", "diamond", "iron", "gold", "stone", "wood"]
axes = ["netherite", "diamond", "iron", "gold", "stone", "wood"]
hoes = ["netherite", "diamond", "iron", "gold", "stone", "wood"]
chance = 0.1

[Weapon.weightsForTools]
weights = [100, 20, 20, 75, 1]
weightsForExtraEntries = 100

[Weapon.commonWeights]
weights = [1, 10, 100, 50, 50, 100]
weightsForExtraEntries = 100
```

Tools are in order: `[swords,shovels,pickaxes,axes,hoes]`

```toml
[ArmorTrims]
patterns = ["sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide", "snout", "rib", "spire"]
materials = ["quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst"]
vanillaOnly = true
chance = 0.1

[ArmorTrims.patternsWeights]
weights = []
weightsForExtraEntries = 110

[ArmorTrims.materialsWeights]
weights = [100, 100, 1, 50, 100, 100, 100, 10, 100, 100]
weightsForExtraEntries = 100
```

> Note: Armor trims can be added through datapacks and are tied to a world.

```toml
[enchantmentLevelRange]
min = 5
max = 40

[statusEffects]
effects = ["speed", "haste", "strength", "jump_boost", "regeneration", "resistance", "fire_resistance", "water_breathing", "invisibility", "health_boost", "absorption", "saturation", "slow_falling", "conduit_power", "dolphins_grace"]
maxTimeInTicks = -1
incrementalChances = [0.1, 0.5, 0.8, 0.9]
maxAmplifier = 5
```

Will add a chance for enchanted gear later. For now everything is enchanted.

If maxTimeInTicks is -1 that means all effects will be infinite.