## New Features

- Add vanilla spawn checks with config options
- Specify light level for spawning in config [#5](https://github.com/offbeat-stuff/Zombie-Apocalypse/issues/5)


## Changes

- frost and fire spawning chances are now independent
- Zombies no longer spawn in sun if they burn in sunlight

## Fixes

- Spawn zombies in nether by default


## Config Changes
```toml
[spawn]
vanillaSpawnRestrictionOnFoot = true
checkIfBlockBelowAllowsSpawning = true
lightLevel = 15
allowedDimensions = ["overworld", "the_nether", "the_end"]
```