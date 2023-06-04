**Features**
-   Custom logic to dynamically spawn zombies.
-   Zombies don't burn in daylight (cause it would be weird for a zombie apocalypse).
-   Equip randomly enchanted armor, including unique armor trims, to spawned zombies.
-   Impose random status effects on zombies upon spawning.
-   Configure the time range during which zombies spawn.

---

**Planned Features**
-   Random Infinite Status effects (Currently finite).
-   Enhanced configuration options for providing weapon lists.
-   Zombies that inflict burning or frost effects on hit.
-   Explosion zombies that exclusively damage entities.
-   Assisting Zombies (supporting players or fellow zombies).
-   Zombies that hit other zombies to propel them closer to you.
-   Dancing zombie that continually spawns new zombies.

---

Default Config

```toml
zombiesBurnInSunlight = false
spawnInstantly = false
frostZombieChance = 0.01
fireZombieChance = 0.01
minPlayerDistance = 16.0
maxZombieCount = 150
firstChance = 0.05
secondChance = 0.1
maxPotionTimeInTicks = 12000
maxAmplifier = 2
allowedDimensions = ["overworld", "ther_nether", "the_end"]

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

[axisSpawnParameters]
chance = 0.1
min = 16
max = 48

[planeSpawnParameters]
chance = 0.1
min = 16
max = 48

[boxSpawnParameters]
chance = 0.1
min = 24
max = 64

[timeRange]
min = 1000
max = 13000

[enchantmentLevelRange]
min = 5
max = 40
```