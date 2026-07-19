# Modular Redstone Circuits

> Redstone, but grown up — compact, nestable logic blocks for Minecraft Java Edition 26.2.

[![Build](https://github.com/korti11/modular-redstone-circuits/actions/workflows/build.yml/badge.svg)](https://github.com/korti11/modular-redstone-circuits/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Minecraft](https://img.shields.io/badge/Minecraft-26.2-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-26.2.x-orange)

**Modular Redstone Circuits (MRC)** lets you design compact 3D redstone circuits inside single blocks. Circuits can nest inside other circuits, enabling modular, reusable logic that scales from simple gates to full multi-tier computing structures — all without sprawling wire farms.

## Features

### Circuit Plates (Tier 0)
The smallest buildable unit. Design on a 5×5×2 grid using only vanilla redstone primitives. A standard library of pre-built plates is included: AND, OR, NOT, NAND, NOR, XOR, XNOR, SR Latch, D Latch, D Flip-Flop, T Flip-Flop, Half Adder, Full Adder, Clock Divider, Pulse Shortener, and Edge Detector.

### Circuit Blocks (Mk1–Mk3)
Three tiers of nestable logic blocks with increasing grid sizes and nesting depth:

| Tier | Block | Grid | Max Nesting Depth |
|------|-------|------|-------------------|
| Mk1 | Circuit Block Mk1 | 3×3×3 | 1 |
| Mk2 | Circuit Block Mk2 | 5×5×5 | 2 |
| Mk3 | Circuit Block Mk3 | 7×7×7 | 3 |

### Bundled Cables (Mk2+)
Route up to 16 signal channels (one per dye color) through a single cable cell. Splitter and Merger cells unpack and repack channel subsets. Two cables only connect when their channel sets match exactly.

### Circuit Builder Workbench
A craftable block that opens a full 3D editor with Blender-style orbit/zoom/pan, signal simulation mode (powered cells glow warm red), a component palette, and a port manager for naming I/O ports (e.g. "Clock", "Reset", "Output A").

### Circuit Library
A storage block for saving, tagging, previewing, and sharing named circuits. Pre-populated with the standard library of logic gates and latches. Circuits are self-contained NBT items and can be traded between players.

### Circuit Printer (Late Game)
Exports and imports circuits as shareable string codes.

## Crafting Progression

| Item | Key Materials | Stage |
|------|---------------|-------|
| Circuit Plate | Stone Slab + Redstone Dust + Iron Nugget | Early game |
| Circuit Block Mk1 | Circuit Board (Iron Plate + Redstone Dust + Gold Nugget) | Iron age |
| Circuit Builder Workbench | Wood Planks + Iron + Redstone Block | Early (alongside Mk1) |
| Circuit Block Mk2 | Etched Circuit Board (Gold + Lapis + functional Mk1) | Mid game |
| Circuit Library | Bookshelves + Iron + Circuit Board | Mid game (alongside Mk2) |
| Circuit Block Mk3 | Crystalline Logic Core (Diamond + Emerald + Etched Circuit Board + functional Mk2) | Late game |
| Circuit Printer | Redstone Block + Observer + Crystalline Logic Core | Late game (alongside Mk3) |

## Development Setup

**Requirements:** JDK 25, Git

```bash
git clone https://github.com/korti11/modular-redstone-circuits.git
cd modular-redstone-circuits
./gradlew build
```

Open the project in IntelliJ IDEA or Eclipse. If you encounter missing libraries, run `./gradlew --refresh-dependencies`. To launch a development client: `./gradlew runClient`.

## License

This project is licensed under the [MIT License](LICENSE).
