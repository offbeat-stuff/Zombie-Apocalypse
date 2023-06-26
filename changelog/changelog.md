## New Features

- Change Spawning and add new options to handle instant spawning


## Config changes

```toml
[spawn]
spawnInstantly = false
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