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
- `bentobox` 2.7.1-SNAPSHOT — BentoBox framework

## Branches

- `master` — release branch (removes `-SNAPSHOT` suffix via Maven profile)
- `develop` — development branch
