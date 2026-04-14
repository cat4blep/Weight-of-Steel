# Weight of Steel

![Weight of Steel icon](src/main/resources/icon.png)

Weight of Steel is a lightweight armor-weight mod by **cat4blep**.
It slows the player based on the actual weight of equipped armor without using the vanilla `Slowness` status effect.

## This branch

- Loader: `Forge`
- Minecraft version: `1.20.1`
- Branch: `forge-1.20.1`
- Built jar: `armor-weight-forge-1.20.1-1.0.0.jar`

## Features

- No vanilla `Slowness` effect.
- Speed reduction is applied through the player's real movement speed logic.
- Armor tooltip shows the weight used by the calculation.
- JSON config supports global tuning and per-item overrides.
- Modded armor can be tuned by exact item id such as `modid:custom_chestplate`.

## Installation

1. Install `Forge` for Minecraft `1.20.1`.
2. Put `armor-weight-forge-1.20.1-1.0.0.jar` into your `mods` folder.
3. Start the game once to generate `config/armorweight.json`.
4. Edit the config if you want different weights or slowdown strength.
5. Restart the game after changing the config.

## Configuration

Config file:

```text
config/armorweight.json
```

Example:

```json
{
  "showTooltip": true,
  "weightToSlowdown": 0.013,
  "maxSlowdown": 0.35,
  "toughnessWeight": 0.5,
  "knockbackWeight": 8.0,
  "itemOverrides": {
    "minecraft:netherite_chestplate": {
      "useCustomWeight": true,
      "customWeight": 25.0
    },
    "modid:custom_helmet": {
      "useCustomWeight": true,
      "customWeight": 7.5
    }
  }
}
```

## Building

Windows:

```powershell
.\gradlew.bat build
```

Linux / GitHub Actions:

```bash
./gradlew build
```

This branch uses Java `21 for CI / 17 bytecode target` for builds.
GitHub Actions uploads the built jar from `build/libs` as an artifact after a successful run.
