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
armorChance = 0.5
armorPieceChances = [5.0E-4, 0.0025]
weaponChance = 0.5
axeChance = 0.3
weaponChances = [0.001, 0.0075, 0.01]
axisSpawnChance = 1.0
axisRangeMin = 24
axisRangeMax = 48
planeSpawnChance = 1.0
planeRangeMin = 24
planeRangeMax = 48
boxSpawnChance = 1.0
boxSpawnMin = 32
boxSpawnMax = 64
minTime = 1000
maxTime = 13000
minEnchantmentLevel = 5
maxEnchantmentLevel = 40
minPlayerDistance = 24.0
maxZombieCount = 150
firstChance = 0.05
secondChance = 0.1
maxPotionTimeInTicks = 12000
maxAmplifier = 2
```

Default Config next beta
```toml
zombiesBurnInSunlight = false
armorChance = 0.5
armorPieceChances = [5.0E-4, 0.0025, 0.049999997, 0.99999994]
weaponChance = 0.5
axeChance = 0.3
weaponChances = [0.001, 0.0075, 0.01, 0.04641589, 0.21544349, 1.0000001]
minPlayerDistance = 24.0
maxZombieCount = 150
firstChance = 0.05
secondChance = 0.1
maxPotionTimeInTicks = 12000
maxAmplifier = 2
allowedDimensions = ["minecraft:overworld", "minecraft:ther_nether", "minecraft:the_end"]

[axisSpawnParameters]
chance = 1.0
min = 24
max = 48

[planeSpawnParameters]
chance = 1.0
min = 24
max = 48

[boxSpawnParameters]
chance = 1.0
min = 32
max = 64

[timeRange]
min = 1000
max = 13000

[enchantmentLevelRange]
min = 5
max = 40

```