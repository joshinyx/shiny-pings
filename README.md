<p align="center">
  <img src="docs/mod-header.png" alt="Shiny Pings" width="1200">
</p>

A **client-side** mod for Minecraft **26.1.2** (Fabric). Ping whatever you're looking at with
the mouse wheel. Everything is local — nothing is sent to the server, so it works on any
server without special permissions.

## What it does

- **Ping an entity** → it gets a **glow** outline and a **fake advancement toast** pops in the
  top-right ("You spotted a Zombie") using vanilla's **real advancement UI** (smooth slide +
  sound), with the mob's **representative drop** as the icon (zombie → rotten flesh, creeper →
  gunpowder, …).
- **The ping spreads** → when you spot a mob, the spot **jumps on its own** to mobs within a
  10-block radius, and from each of those to *their* neighbours, wave by wave (with an amethyst
  sound at each hop). Each mob is spotted only once, so it **never loops**: if A → B → C and C
  is next to A, A is skipped because it was already marked.
- **Ping a dropped item** → a **card** appears at the top-centre of the screen: the item inside
  a **dark slot** (the F4 menu slot) with the **distance** to the item **below** it (updated
  live as you move). It lasts ~10 seconds, and the item on the ground also **glows** so you can
  find it in the world.

Both the item card and the mob toast show **one at a time**: if you ping something else before
the previous one fades, it's cut and the new one takes over (they don't stack).

## Controls

- **Middle mouse button (wheel)** by default. Rebindable in **Options → Controls → Shiny Pings**.

## Configuration

Created automatically at `.minecraft/config/shiny-pings.json`. Durations are in *ticks*
(20 ticks = 1 second):

| Field | Default | What it does |
|---|---|---|
| `toastHeader` | `"Spotted!"` | Top (gold) line of the toast |
| `toastTitle` | `"You spotted a {mob}"` | Bottom line; `{mob}` = mob name |
| `toastSound` | `"minecraft:block.amethyst_block.fall"` | Toast sound (sound id) |
| `expandEnabled` | `true` | Enables the ping spreading to nearby mobs |
| `expandRadius` | `10.0` | Radius (blocks) the spot jumps between mobs |
| `expandDelayTicks` | `4` (0.2 s) | Delay between cascade waves |
| `expandSound` | `"minecraft:block.amethyst_block.resonate"` | Sound at each mob the cascade reaches |
| `entityGlowDurationTicks` | `100` (5 s) | How long a pinged entity glows |
| `itemSpotDurationTicks` | `200` (10 s) | How long the item card stays on screen |
| `itemSpotText` | `"Spotted at {dist}m"` | Text under the slot; `{dist}` = distance (blocks) |
| `pingRange` | `64.0` | Max ray reach, in blocks (item or mob) |

## Build & install

Requires **JDK 25** (mandatory for 26.1). The Gradle wrapper (9.4.0) is included.

```powershell
# build the .jar
./gradlew build

# run a dev client (launches Minecraft with the mod loaded)
./gradlew runClient
```

The `.jar` lands in `build/libs/shiny-pings-1.0.0.jar`. Drop it in your `mods/` folder along
with **Fabric API** and **Fabric Loader 0.19.3+**.

> In IntelliJ IDEA (2025.3+): *Open* the folder → import the Gradle project → `runClient` is
> ready as a run configuration.
>
> Verified: compiles against the real Minecraft 26.1.2 mappings (Loom 1.16.3, Fabric API
> 0.151.0+26.1.2).

## Structure

```
src/main/java/com/shinypings/
├── ShinyPingsClient.java        # entrypoint: keybind + tick + HUD registration
├── config/PingConfig.java       # JSON config (text, sounds, durations, range)
├── ping/
│   ├── PingManager.java         # raycast from the camera + item vs entity dispatch
│   ├── PingExpansion.java        # wave-by-wave cascade to nearby mobs (loop-safe)
│   └── PingTracker.java         # glow + item-card state (countdown)
├── hud/
│   ├── FakeAdvancement.java     # builds and shows the toast (one at a time)
│   ├── SpottedToast.java        # advancement-styled Toast with a custom header
│   ├── MobLoot.java             # curated EntityType → representative-drop map
│   └── ItemSpotOverlay.java     # item card (dark slot + distance) at the top
└── mixin/EntityGlowMixin.java   # forces the client-side glow (see below)
```

## 26.1.2 technical notes

26.1 is the **first unobfuscated version**: it uses Mojang's official mappings and rewrote the
HUD render pipeline. The code was verified against the real JAR, and these names **changed**
from 1.21.x (handy if you extend the mod):

| Before (1.21.x) | Now (26.1.2) |
|---|---|
| `net.minecraft.resources.ResourceLocation` | **`net.minecraft.resources.Identifier`** |
| `GuiGraphics` | **`GuiGraphicsExtractor`** |
| `guiGraphics.drawString(font, …)` | **`gge.text(font, str, x, y, color)`** |
| `guiGraphics.renderItem(stack, …)` | **`gge.item(stack, x, y)`** |
| `player.displayClientMessage(c, true)` | **`player.sendOverlayMessage(c)`** (action bar) |
| `KeyBindingHelper.registerKeyBinding` | **`KeyMappingHelper.registerKeyMapping`** (`keymapping.v1`) |
| HUD `HudRenderCallback` | **`HudElementRegistry.attachElementBefore`** (`rendering.v1.hud`) |
| `Toast.render(GuiGraphics, …)` | **`extractRenderState(GuiGraphicsExtractor, Font, long)`** |

### The toast: advancement look, custom header, one at a time

Vanilla's `AdvancementToast` hard-codes its header from the advancement type
("Advancement Made!", "Challenge Complete!", …) and can't be changed without affecting real
advancements. So `SpottedToast` implements `Toast` directly and draws the same thing vanilla
does (same `minecraft:toast/advancement` sprite, same layout), but with the header/title/sound
coming from `PingConfig`. The smooth slide in/out is free: the `ToastManager` drives it via
`getWantedVisibility()`.

To keep **only one toast at a time**, `SpottedToast` uses a shared `TOKEN`: on each ping,
`FakeAdvancement` looks up the existing toast with `ToastManager#getToast` and, if found,
**reuses** that object (swaps content + restarts the timer + replays the sound) instead of
queueing a new one.

### The glow needs a mixin (not `setGlowingTag`)

`Entity.setGlowingTag(true)` **doesn't work on the client**. From the bytecode:

```java
// isCurrentlyGlowing(): on the client this reads the SERVER-synced shared flag
return level().isClientSide() ? getSharedFlag(6) : hasGlowingTag;
// setGlowingTag(v): circular on the client, leaves flag 6 false
this.hasGlowingTag = v;
this.setSharedFlag(6, this.isCurrentlyGlowing());
```

So `EntityGlowMixin` injects into `isCurrentlyGlowing()` and returns `true` for entities tracked
by `PingTracker`. `Minecraft.shouldEntityAppearGlowing(...)` consults that method, so vanilla's
outline pass renders the glow. (Client-side mixin, no refmap since 26.1 isn't obfuscated.)

### Mob icon = representative drop (fallback chain)

`MobLoot` maps each mob to its most recognizable drop (zombie → rotten flesh, creeper →
gunpowder, enderman → pearl, wither → Nether star, ender dragon → dragon egg, …), including the
**new 26.1 mobs** (copper golem → copper ingot, creaking → resin, happy ghast → ghast tear,
nautilus → nautilus shell). So **no mob is left without an icon**:

1. **Curated drop** from the map.
2. If absent → its **spawn egg**.
3. If it has no spawn egg either → a **name tag**.

Tweaking a drop or adding a mob is a one-liner in `MobLoot.java`.

## License

MIT — see [LICENSE](LICENSE).
