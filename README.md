# Create: Cave Diving

Create: Cave Diving is a Create addon that adds configurable underground breathing hazards, respirators, and tiered filters while using Create backtanks as the air supply.

The hazard system is fully data-driven. Modpacks and datapacks can define any number of hazard levels and decide where they apply by dimension, biome, tags, and Y level.

## Requirements

- Minecraft 1.21.1
- NeoForge
- Create 6.0.10 or newer within the supported 6.0.x range

## How the system works

When a player is inside an active hazard, they must have:

1. A respirator equipped in the head slot.
2. A filter installed in that respirator whose tier is at least the current hazard level.
3. A Create-compatible backtank with air available.

If any requirement is missing, the player cannot breathe because of the Cave Diving hazard.

Respirators themselves no longer have protection tiers. Their role is to hold an installed filter, while the filter determines which hazard levels the player can survive.

Cave Diving does not grant underwater breathing by itself. Underwater breathing remains independent, so systems such as Create's Diving Helmet can coexist with the respirator mechanics.

Creative and spectator players are ignored by the hazard system.

## Built-in respirators

| Item                 |
| -------------------- |
| Copper Respirator    |
| Netherite Respirator |

Both respirators use the same filter-tier system. Their material and durability differ, but neither respirator has an inherent hazard-protection tier.

## Built-in filters

| Item             | Default tier |
| ---------------- | -----------: |
| Charcoal Filter  |            1 |
| Layered Filter   |            2 |
| Composite Filter |            3 |

A filter protects against hazards whose level is less than or equal to its tier.

For example:

```text
Charcoal Filter (Tier 1)  -> protects against Hazard 1
Layered Filter (Tier 2)   -> protects against Hazard 1-2
Composite Filter (Tier 3) -> protects against Hazard 1-3
```

Hazard and filter tiers are not limited to these built-in values. Datapacks may use higher hazard levels and may redefine filter tiers.

## Installing and removing filters

While wearing a respirator, sneak-use a valid filter item to install it.

Installing another filter replaces the currently installed one and returns the previous filter to the player.

Sneak-use the respirator itself while holding it to remove its installed filter.

The respirator tooltip shows the currently installed filter and its tier, while filter items show their configured tier.

# Datapack guide

## Basic datapack structure

For Minecraft 1.21.1, a minimal datapack can be structured as follows:

```text
my_cave_diving_pack/
├── pack.mcmeta
└── data/
    └── my_namespace/
        └── hazards/
            └── example.json
```

Example `pack.mcmeta`:

```json
{
  "pack": {
    "pack_format": 48,
    "description": "Custom Create: Cave Diving hazards"
  }
}
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
  "values": [
    "minecraft:overworld",
    "example_mod:deep_world"
  ]
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
  "values": [
    "minecraft:deep_dark",
    "example_mod:abyssal_caves"
  ]
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

# Filter datapack guide

Respirator filters are also data-driven.

Filter definitions are loaded from:

```text
data/<namespace>/respirator_filters/*.json
```

Each definition maps an already registered Minecraft item to a Cave Diving filter tier.

A basic filter definition looks like this:

```json
{
  "item": "create_cave_diving:charcoal_filter",
  "tier": 1
}
```

The supported fields are:

| Field  | Type              | Required | Description                                             |
| ------ | ----------------- | -------- | ------------------------------------------------------- |
| `item` | resource location | Yes      | Registered item that should act as a respirator filter. |
| `tier` | integer           | Yes      | Filter protection tier. Must be `1` or greater.         |

This system is not limited to Cave Diving's built-in filter items. Any already registered item from another mod or from a scripting mod such as KubeJS can be declared as a respirator filter.

For example:

```text
data/my_namespace/respirator_filters/abyssal_filter.json
```

```json
{
  "item": "example_mod:abyssal_filter",
  "tier": 5
}
```

That item will then behave as a Tier 5 respirator filter.

The filename does not determine the item or the tier. The `item` field is the item being registered as a filter.

Filter definitions are reloaded with the normal Minecraft `/reload` command.

## Rebalancing the built-in filters

The built-in progression defaults to Tier 1, Tier 2, and Tier 3, but modpacks can redefine those tiers through datapack filter definitions.

For example, a pack could use:

```text
Charcoal Filter  -> Tier 1
Layered Filter   -> Tier 3
Composite Filter -> Tier 5
```

and then define intermediate hazard levels that require progression to the next physical filter.

This allows the three built-in items to support much broader progression without hardcoding a maximum hazard tier.

# Server configuration

Cave Diving provides a server config for disabling the system and controlling air consumption.

The default values are equivalent to:

```toml
enabled = true

[airConsumption]
baseAirInterval = 40
intervalReductionPerHazard = 5
minimumAirInterval = 5

[filterEfficiency]
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

## Filter efficiency and diminishing returns

The installed filter must have a tier equal to or greater than the hazard level to protect the player.

When the installed filter tier is higher than required, the air consumption interval is increased. This gives higher-tier filters better air efficiency in lower-tier hazards.

The bonus is calculated using diminishing returns:

```text
tierDifference = filterTier - hazardLevel

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

Higher values make additional filter tiers less effective.

With the default maximum bonus of `0.5` and falloff of `1.0`:

| Tier advantage | Efficiency bonus |
| -------------: | ---------------: |
|              0 |               0% |
|             +1 |              25% |
|             +2 |          \~33.3% |
|             +3 |            37.5% |
|             +4 |              40% |
|     Very large |   Approaches 50% |

# Datapack validation

Malformed hazard rules are rejected individually and logged as errors. Other valid rules can still load.

Cave Diving validates, among other things:

- Missing `hazard_level`
- Negative hazard levels
- Unknown root fields
- Unknown condition fields
- Invalid resource locations
- Empty tags such as `"#"`
- Non-object `conditions`
- `min_y` greater than `max_y`

Filter definitions are also validated, including:

- Missing `item` or `tier`
- Invalid item resource locations
- Unknown or unregistered items
- Filter tiers lower than `1`
- Unknown filter-definition fields
- Multiple filter definitions targeting the same item

Using `/reload` while developing a datapack is therefore enough to reload the rules and inspect validation errors in the game/server log.
