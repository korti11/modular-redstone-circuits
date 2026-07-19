# Modular Redstone Circuits — Claude Instructions

## Toolchain & Build

- **Java 25** — matches the JDK Mojang ships with MC 26.2.
- **NeoForge moddev plugin** `2.0.142` via `net.neoforged.moddev`.
- **Key Gradle tasks:** `build`, `runClient`, `runServer`, `runData`, `runGameTestServer`.
- **Configuration cache is enabled** — build scripts must not use mutable state (`org.gradle.configuration-cache=true`).
- **`gradle.properties` is the single source of truth** for versions and mod identity. All `${variable}` tokens in `src/main/templates/` are expanded from it at build time by `generateModMetadata`.

## IntelliJ MCP Server

Prefer `mcp__IntelliJ__*` tools over generic equivalents whenever one covers the task:

| Task | Prefer | Over |
|------|--------|------|
| Read a file | `mcp__IntelliJ__get_file_text_by_path` | `Read` |
| Search text | `mcp__IntelliJ__search_in_files_by_text` / `search_in_files_by_regex` | `Grep` |
| Find files | `mcp__IntelliJ__find_files_by_name_keyword` / `find_files_by_glob` | `Glob` |
| Build | `mcp__IntelliJ__build_project` | `Bash ./gradlew build` |
| Compiler errors | `mcp__IntelliJ__get_file_problems` | — |
| Edit a file | `mcp__IntelliJ__replace_text_in_file` / `apply_patch` | `Edit` / `Write` |
| Rename a symbol | `mcp__IntelliJ__rename_refactoring` | manual find-replace |

## Package & File Structure

- **Root package:** `io.korti.mrc`
- **Subpackages by domain:** `block`, `item`, `blockentity`, `screen`, `recipe`, `datagen`, etc.
- `ModularRedstoneCircuits.java` — common entry point (`@Mod`)
- `ModularRedstoneCircuitsClient.java` — client-only entry point (`@Mod(dist = Dist.CLIENT)`)
- `Config.java` — config spec; add one file per config type if multiple configs are needed
- Dedicated registry classes (e.g. `ModBlocks`, `ModItems`) hold the `DeferredRegister` and all `DeferredHolder` constants; each register is attached to the mod bus in the main class constructor

## NeoForge Patterns

### Deferred Registration

- All registry objects are `public static final DeferredHolder` / `DeferredBlock` / `DeferredItem` constants on the registry class.
- The `DeferredRegister` itself is `private static final`.
- `.register(modEventBus)` is called in the main class constructor.

### Event Registration

Three forms exist — pick the right one:

| Use case | Pattern |
|----------|---------|
| Mod lifecycle events (`FMLCommonSetupEvent`, etc.) | `modEventBus.addListener(this::method)` in constructor; no annotation on the method |
| Game/server events (instance) | `NeoForge.EVENT_BUS.register(this)` in constructor + `@SubscribeEvent` on the instance method |
| Client-only static handlers | `@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)` on the class + `static @SubscribeEvent` method |

### Config

`ModConfigSpec.Builder` → static `ConfigValue<T>` fields → `SPEC = BUILDER.build()` — all in `Config.java`.  
Registered via `modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)` in the main constructor.  
`IConfigScreenFactory` extension point registered in the client constructor for the in-game config screen.

### Mod Metadata

`neoforge.mods.toml` lives in `src/main/templates/META-INF/` (not `src/main/resources/`).  
`${variable}` tokens are expanded at build time. Do not put token-expanded files in `src/main/resources/`.

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Mod ID | lowercase, no separator | `modularredstonecircuits` |
| Java package | `io.korti.mrc.<domain>` | `io.korti.mrc.block` |
| Registry constants | `UPPER_SNAKE_CASE` | `CIRCUIT_PLATE`, `CIRCUIT_PLATE_ITEM` |
| Config values | `UPPER_SNAKE_CASE` | `MAGIC_NUMBER` |
| Config TOML keys | `camelCase` | `"magicNumber"` |
| Resource / block / item IDs | `snake_case` | `circuit_plate` |
| Lang keys (game objects) | `<type>.<modid>.<id>` | `block.modularredstonecircuits.circuit_plate` |
| Lang keys (config) | `<modid>.configuration.<key>` | `modularredstonecircuits.configuration.logDirtBlock` |

## Resource Organization

- **Static assets:** `src/main/resources/assets/modularredstonecircuits/`
- **Processed metadata:** `src/main/templates/META-INF/`
- **Datagen output:** `src/generated/resources/` — never edit manually; regenerate with `runData`.
- **Data generation is preferred over static data files.** Recipes, loot tables, tags, block states, models, and advancements must be produced by data generators in `src/main/java/io/korti/mrc/datagen/`. Write static files in `src/main/resources/data/` only when datagen cannot express the output.
