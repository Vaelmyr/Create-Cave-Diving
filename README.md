<p align="center">
  <img src="src/main/resources/create_cave_diving.png" alt="Create: Cave Diving" width="256">
</p>

# Create: Cave Diving

Create: Cave Diving is a Create addon that adds configurable underground breathing hazards and respirator progression while using Create backtanks as the air supply.

The hazard system is fully data-driven. Modpacks and datapacks can define any number of hazard levels and decide where they apply by dimension, biome, tags, and Y level.

## Inspiration and Motivation

Create: Cave Diving is inspired by [Thin Air](https://github.com/Fuzss/thinair), but was designed to provide significantly more control over how breathing hazards are defined and scaled.

The mod was originally created for a custom modpack that uses world generation mods capable of extending the Overworld to depths of up to 2048 blocks. In that kind of environment, a fixed or relatively simple altitude-based breathing system is not flexible enough.

For this reason, Create: Cave Diving uses fully data-driven hazard rules that can target specific dimensions, biomes, biome tags, dimension tags, and vertical ranges, with support for priorities and absolute overrides.

## Requirements

- Minecraft 1.21.1
- NeoForge
- Create 6.0.10 or newer within the supported 6.0.x range

## How the system works

When a player is inside an active hazard, they must have:

1. A respirator whose tier is at least the current hazard level.
2. A Create-compatible backtank with air available.

If either requirement is missing, the player cannot breathe because of the Cave Diving hazard.

Cave Diving does not grant underwater breathing by itself. Underwater breathing remains independent, so systems such as Create's Diving Helmet can coexist with the respirator mechanics.

## Built-in respirators

| Item                 | Tier |
| -------------------- | ---: |
| Copper Respirator    |    1 |
| Netherite Respirator |    2 |

Hazard levels themselves are not limited to these values. Datapacks may use any non-negative integer hazard level.

# Datapack guide

## Basic datapack structure

For Minecraft 1.21.1, a minimal datapack can be structured as follows:

```text
create_cave_diving_custom_datapack/
├── pack.mcmeta
└── data/
    └── my_namespace/
        └── hazards/
            └── example.json
```

Hazard rules are loaded from:

```text
data/<namespace>/hazards/*.json
```

The namespace does not need to be `create_cave_diving`. This allows modpacks and other mods to provide their own rules without modifying Cave Diving itself.

Rules are reloaded with the normal Minecraft `/reload` command.

## Basic hazard rule

```json
{
  "hazard_level": 1,
  "priority": 0,
  "conditions": {
    "dimension": "minecraft:overworld",
    "max_y": 0
  }
}
```

This creates Hazard Level 1 in the Overworld at Y 0 and below.

## Rule fields

A hazard rule supports the following root fields:

| Field          | Type    | Required | Default | Description                                                                    |
| -------------- | ------- | -------- | ------- | ------------------------------------------------------------------------------ |
| `hazard_level` | integer | Yes      | None    | Hazard level produced by this rule. Must be `0` or greater.                    |
| `priority`     | integer | No       | `0`     | Determines which matching rule wins. Higher values have higher priority.       |
| `override`     | boolean | No       | `false` | If `true`, this rule always takes precedence over matching non-override rules. |
| `conditions`   | object  | No       | Empty   | Conditions that must all match for the rule to apply.                          |

Unknown fields are rejected, which helps catch spelling mistakes in datapacks.

### `hazard_level`

The hazard level can be any non-negative integer:

```json
{
  "hazard_level": 7
}
```

There is no hardcoded maximum number of hazard tiers.

> [!IMPORTANT]
> Hazard levels are not limited to the respirator tiers included by default. However, Create: Cave Diving currently provides only two respirator tiers: Copper (Tier 1) and Netherite (Tier 2).
>
> As a result, defining Hazard Level 3 or higher will make those areas effectively unbreathable with the mod's default equipment. Unless another mod, addon, or code modification introduces higher-tier respirators, players will not be able to satisfy those hazard requirements.

A rule with no `conditions` is global, so the example above would make Hazard Level 7 apply everywhere unless another rule wins through the precedence system.

A hazard level of `0` represents no Cave Diving breathing hazard and is especially useful with overrides.

## Conditions

The `conditions` object supports:

| Field       | Type    | Meaning                        |
| ----------- | ------- | ------------------------------ |
| `dimension` | string  | Dimension ID or dimension tag. |
| `biome`     | string  | Biome ID or biome tag.         |
| `min_y`     | integer | Minimum Y level, inclusive.    |
| `max_y`     | integer | Maximum Y level, inclusive.    |

All conditions present in the same rule must match.

For example:

```json
{
  "hazard_level": 3,
  "conditions": {
    "dimension": "minecraft:overworld",
    "biome": "minecraft:deep_dark",
    "min_y": -100,
    "max_y": -20
  }
}
```

This rule applies only when all four conditions are true.

If both `min_y` and `max_y` are present, `min_y` cannot be greater than `max_y`.

## Dimension IDs

Use a normal resource location to target one dimension:

```json
{
  "hazard_level": 2,
  "conditions": {
    "dimension": "minecraft:overworld"
  }
}
```

Custom modded dimensions are supported in the same way:

```json
{
  "hazard_level": 4,
  "conditions": {
    "dimension": "example_mod:deep_world"
  }
}
```

## Dimension tags

Prefix a selector with `#` to use a tag instead of a direct ID:

```json
{
  "hazard_level": 3,
  "conditions": {
    "dimension": "#my_namespace:hazardous_dimensions"
  }
}
```

For a tag named `my_namespace:hazardous_dimensions`, create:

```text
data/my_namespace/tags/worldgen/dimension/hazardous_dimensions.json
```

Example:

```json
{
  "replace": false,
  "values": ["minecraft:overworld", "example_mod:deep_world"]
}
```

## Biome IDs

Target a specific biome with its resource location:

```json
{
  "hazard_level": 2,
  "conditions": {
    "biome": "minecraft:deep_dark"
  }
}
```

Modded biomes work the same way:

```json
{
  "hazard_level": 5,
  "conditions": {
    "biome": "example_mod:abyssal_caves"
  }
}
```

## Biome tags

Prefix the biome selector with `#`:

```json
{
  "hazard_level": 3,
  "conditions": {
    "biome": "#my_namespace:dangerous_caves"
  }
}
```

Create the corresponding biome tag at:

```text
data/my_namespace/tags/worldgen/biome/dangerous_caves.json
```

Example:

```json
{
  "replace": false,
  "values": ["minecraft:deep_dark", "example_mod:abyssal_caves"]
}
```

## Y-level conditions

`min_y` is inclusive:

```json
{
  "hazard_level": 2,
  "conditions": {
    "min_y": -128
  }
}
```

This matches Y -128 and above.

`max_y` is also inclusive:

```json
{
  "hazard_level": 2,
  "conditions": {
    "max_y": -128
  }
}
```

This matches Y -128 and below.

They can be combined into a range:

```json
{
  "hazard_level": 4,
  "conditions": {
    "min_y": -1024,
    "max_y": -512
  }
}
```

## Priority

When multiple normal rules match at the same time, the rule with the highest `priority` wins.

For example:

```json
{
  "hazard_level": 2,
  "priority": 0,
  "conditions": {
    "max_y": -200
  }
}
```

and:

```json
{
  "hazard_level": 1,
  "priority": 10,
  "conditions": {
    "biome": "example_mod:safer_deep_caves"
  }
}
```

If both rules match, Hazard Level 1 wins because priority `10` is higher than priority `0`.

Priority can therefore be used both to increase and to decrease the hazard level in more specific situations.

## Override rules

An override rule always takes precedence over every matching normal rule, regardless of their priorities.

Example breathable biome:

```json
{
  "hazard_level": 0,
  "priority": 0,
  "override": true,
  "conditions": {
    "biome": "#my_namespace:breathable"
  }
}
```

Even if a normal Hazard Level 10 rule with priority 1000 also matches, this override still wins.

If multiple override rules match, they are compared using their priorities normally.

## Complete rule precedence

When multiple rules match, Cave Diving resolves them in this order:

1. Override rules beat non-override rules.
2. Between rules of the same type, higher `priority` wins.
3. If both have the same override state and priority, higher `hazard_level` wins.

This makes the result deterministic without depending on datapack file loading order.

## Example: depth progression

`shallow_depths.json`:

```json
{
  "hazard_level": 1,
  "priority": 0,
  "conditions": {
    "dimension": "minecraft:overworld",
    "max_y": -128
  }
}
```

`deep_depths.json`:

```json
{
  "hazard_level": 2,
  "priority": 10,
  "conditions": {
    "dimension": "minecraft:overworld",
    "max_y": -512
  }
}
```

`abyssal_depths.json`:

```json
{
  "hazard_level": 3,
  "priority": 20,
  "conditions": {
    "dimension": "minecraft:overworld",
    "max_y": -1024
  }
}
```

Because deeper rules have higher priorities, the effective progression is:

```text
Y > -128       -> Hazard 0
Y -128 to -511 -> Hazard 1
Y -512 to -1023 -> Hazard 2
Y <= -1024     -> Hazard 3
```

## Example: biome-specific exception

A generic depth rule:

```json
{
  "hazard_level": 2,
  "priority": 0,
  "conditions": {
    "max_y": -256
  }
}
```

A dangerous biome can override that normal progression using a higher priority:

```json
{
  "hazard_level": 5,
  "priority": 50,
  "conditions": {
    "biome": "#my_namespace:extreme_hazard"
  }
}
```

A fully breathable biome can then use an absolute override:

```json
{
  "hazard_level": 0,
  "priority": 0,
  "override": true,
  "conditions": {
    "biome": "#my_namespace:breathable"
  }
}
```

# Server configuration

Cave Diving provides a server config for disabling the system and controlling air consumption.

The default values are equivalent to:

```toml
enabled = true

[airConsumption]
baseAirInterval = 40
intervalReductionPerHazard = 5
minimumAirInterval = 5

[respiratorEfficiency]
maximumEfficiencyBonus = 0.5
efficiencyFalloff = 1.0
```

## `enabled`

```toml
enabled = true
```

Globally enables or disables Cave Diving's runtime breathing hazard mechanics.

When disabled, datapack hazard definitions are still loaded, but they have no gameplay effect.

## Air consumption

### `baseAirInterval`

```toml
baseAirInterval = 40
```

Number of ticks between units of backtank air consumed at Hazard Level 1.

Higher values mean slower air consumption.

### `intervalReductionPerHazard`

```toml
intervalReductionPerHazard = 5
```

Number of ticks removed from the consumption interval for each hazard level above 1.

The base interval is calculated as:

```text
base interval = baseAirInterval - ((hazardLevel - 1) * intervalReductionPerHazard)
```

and cannot go below `minimumAirInterval`.

With the default settings:

| Hazard | Base consumption interval |
| -----: | ------------------------: |
|      1 |                  40 ticks |
|      2 |                  35 ticks |
|      3 |                  30 ticks |
|      4 |                  25 ticks |
|      5 |                  20 ticks |
|      6 |                  15 ticks |
|      7 |                  10 ticks |
|     8+ |           5 ticks minimum |

### `minimumAirInterval`

```toml
minimumAirInterval = 5
```

Minimum number of ticks between air consumption events, regardless of how high the hazard level becomes.

## Respirator efficiency and diminishing returns

A respirator must have a tier equal to or greater than the hazard level to protect the player.

When the respirator tier is higher than required, the air consumption interval is increased. This gives higher-tier respirators better air efficiency in lower-tier hazards.

The bonus is calculated using diminishing returns:

```text
tierDifference = respiratorTier - hazardLevel

bonus = maximumEfficiencyBonus
        * tierDifference
        / (tierDifference + efficiencyFalloff)
```

The final interval is approximately:

```text
final interval = base interval * (1 + bonus)
```

### `maximumEfficiencyBonus`

```toml
maximumEfficiencyBonus = 0.5
```

The theoretical maximum efficiency bonus.

For example, `0.5` means the interval can approach a maximum bonus of +50% as the respirator tier advantage becomes very large.

### `efficiencyFalloff`

```toml
efficiencyFalloff = 1.0
```

Controls how quickly the diminishing returns curve approaches the maximum bonus.

Higher values make additional respirator tiers less effective.

With the default maximum bonus of `0.5` and falloff of `1.0`:

| Tier advantage | Efficiency bonus |
| -------------: | ---------------: |
|              0 |               0% |
|             +1 |              25% |
|             +2 |           ~33.3% |
|             +3 |            37.5% |
|             +4 |              40% |
|     Very large |   Approaches 50% |
