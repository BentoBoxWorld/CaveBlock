# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CaveBlock is a BentoBox GameMode Addon for Minecraft (Bukkit/Spigot). It creates underground cave-based survival worlds where players mine, explore, and build within a solid stone environment. Players get an island (cave) carved out of solid rock rather than placed in an ocean.

## Build Commands

```bash
mvn clean package          # Build the plugin JAR
mvn test                   # Run tests (surefire)
mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=BentoBoxWorld_CaveBlock  # CI build with SonarCloud
```

Java 21 is required. The output JAR goes to `target/`.

## Architecture

**Entry point chain:** `CaveBlockPladdon` (Bukkit plugin entry) → `CaveBlock` (BentoBox `GameModeAddon`)

**Key components:**
- `CaveBlock.java` — Main addon lifecycle (`onLoad`, `onEnable`, `createWorlds`). Creates 3 world generators (normal, nether, end) and registers flags/listeners.
- `Settings.java` — 50+ annotated config fields implementing BentoBox's `WorldSettings`. Maps to `config.yml`.
- `generators/ChunkGeneratorWorld.java` — Core chunk generator. Fills world with stone/netherrack/endstone, supports two modes: legacy (custom ore placement) and new (vanilla-like with `NewMaterialPopulator`).
- `generators/populators/` — `MaterialPopulator` (legacy ore placement), `NewMaterialPopulator` (vanilla-style), `EntitiesPopulator` (mob spawning), `FlatBiomeProvider` (biome distribution).
- `listeners/CustomHeightLimitations.java` — Prevents players from going above the configured world depth. Respects `SKY_WALKER_FLAG` and creative/op bypass.

**BentoBox framework patterns to follow:**
- Addons use `GameModeAddon` lifecycle, not Bukkit's `JavaPlugin` directly
- Configuration uses BentoBox's annotation-based `@ConfigEntry` and `@ConfigComment` system
- Commands extend BentoBox's `CompositeCommand` hierarchy
- Flags (feature toggles) are created via `Flag.Builder` and registered in `onEnable()`
- Player-facing strings come from locale files in `src/main/resources/locales/`

## Key Dependencies (provided scope — server supplies at runtime)

- `paper-api` 1.21.11 — Paper API
- `bentobox` 3.14.1-SNAPSHOT — BentoBox framework

## Branches

- `master` — release branch (removes `-SNAPSHOT` suffix via Maven profile)
- `develop` — development branch

## Dependency Source Lookup

When you need to inspect source code for a dependency (e.g., BentoBox, addons):

1. **Check local Maven repo first**: `~/.m2/repository/` — sources jars are named `*-sources.jar`
2. **Check the workspace**: Look for sibling directories or Git submodules that may contain the dependency as a local project (e.g., `../bentoBox`, `../addon-*`)
3. **Check Maven local cache for already-extracted sources** before downloading anything
4. Only download a jar or fetch from the internet if the above steps yield nothing useful

Prefer reading `.java` source files directly from a local Git clone over decompiling or extracting a jar.

In general, the latest version of BentoBox should be targeted.

## Project Layout

Related projects are checked out as siblings under `~/git/`:

**Core:**
- `bentobox/` — core BentoBox framework

**Game modes:**
- `addon-acidisland/` — AcidIsland game mode
- `addon-bskyblock/` — BSkyBlock game mode
- `Boxed/` — Boxed game mode (expandable box area)
- `CaveBlock/` — CaveBlock game mode
- `OneBlock/` — AOneBlock game mode
- `SkyGrid/` — SkyGrid game mode
- `RaftMode/` — Raft survival game mode
- `StrangerRealms/` — StrangerRealms game mode
- `Brix/` — plot game mode
- `parkour/` — Parkour game mode
- `poseidon/` — Poseidon game mode
- `gg/` — gg game mode

**Addons:**
- `addon-level/` — island level calculation
- `addon-challenges/` — challenges system
- `addon-welcomewarpsigns/` — warp signs
- `addon-limits/` — block/entity limits
- `addon-invSwitcher/` / `invSwitcher/` — inventory switcher
- `addon-biomes/` / `Biomes/` — biomes management
- `Bank/` — island bank
- `Border/` — world border for islands
- `Chat/` — island chat
- `CheckMeOut/` — island submission/voting
- `ControlPanel/` — game mode control panel
- `Converter/` — ASkyBlock to BSkyBlock converter
- `DimensionalTrees/` — dimension-specific trees
- `discordwebhook/` — Discord integration
- `Downloads/` — BentoBox downloads site
- `DragonFights/` — per-island ender dragon fights
- `ExtraMobs/` — additional mob spawning rules
- `FarmersDance/` — twerking crop growth
- `GravityFlux/` — gravity addon
- `Greenhouses-addon/` — greenhouse biomes
- `IslandFly/` — island flight permission
- `IslandRankup/` — island rankup system
- `Likes/` — island likes/dislikes
- `Limits/` — block/entity limits
- `lost-sheep/` — lost sheep adventure
- `MagicCobblestoneGenerator/` — custom cobblestone generator
- `PortalStart/` — portal-based island start
- `pp/` — pp addon
- `Regionerator/` — region management
- `Residence/` — residence addon
- `TopBlock/` — top ten for OneBlock
- `TwerkingForTrees/` — twerking tree growth
- `Upgrades/` — island upgrades (Vault)
- `Visit/` — island visiting
- `weblink/` — web link addon
- `CrowdBound/` — CrowdBound addon

**Data packs:**
- `BoxedDataPack/` — advancement datapack for Boxed

**Documentation & tools:**
- `docs/` — main documentation site
- `docs-chinese/` — Chinese documentation
- `docs-french/` — French documentation
- `BentoBoxWorld.github.io/` — GitHub Pages site
- `website/` — website
- `translation-tool/` — translation tool

Check these for source before any network fetch.

## Key Dependencies (source locations)

- `world.bentobox:bentobox` → `~/git/bentobox/src/`
