# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v20.4.1-1.20.4] - 2024-02-05
### Fixed
- Fix rendered crafting tables contents failing to rotate towards the closest player

## [v20.4.0-1.20.4] - 2024-01-27
- Rewritten for Minecraft 1.20.4
- All modded crafting tables are supported automatically, use `visualworkbench:unaltered_workbenches` for individual exclusions
- New implementation should work much more reliably with mods like CraftTweaker
- Greatly reduce number of update packets send to clients
- Publish for NeoForge
