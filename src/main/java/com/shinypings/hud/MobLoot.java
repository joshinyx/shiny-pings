package com.shinypings.hud;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

// Curated representative-drop icon per mob; null for unlisted types (FakeAdvancement falls back to the spawn egg).
public final class MobLoot {

	private MobLoot() {
	}

	private static final Map<EntityType<?>, Item> DROPS = Map.ofEntries(
			// Undead
			Map.entry(EntityType.ZOMBIE, Items.ROTTEN_FLESH),
			Map.entry(EntityType.HUSK, Items.ROTTEN_FLESH),
			Map.entry(EntityType.DROWNED, Items.ROTTEN_FLESH),
			Map.entry(EntityType.ZOMBIE_VILLAGER, Items.ROTTEN_FLESH),
			Map.entry(EntityType.ZOMBIFIED_PIGLIN, Items.GOLD_NUGGET),
			Map.entry(EntityType.ZOGLIN, Items.ROTTEN_FLESH),
			Map.entry(EntityType.SKELETON, Items.BONE),
			Map.entry(EntityType.STRAY, Items.BONE),
			Map.entry(EntityType.BOGGED, Items.BONE),
			Map.entry(EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL),
			// Hostiles
			Map.entry(EntityType.CREEPER, Items.GUNPOWDER),
			Map.entry(EntityType.SPIDER, Items.STRING),
			Map.entry(EntityType.CAVE_SPIDER, Items.STRING),
			Map.entry(EntityType.ENDERMAN, Items.ENDER_PEARL),
			Map.entry(EntityType.SLIME, Items.SLIME_BALL),
			Map.entry(EntityType.MAGMA_CUBE, Items.MAGMA_CREAM),
			Map.entry(EntityType.BLAZE, Items.BLAZE_ROD),
			Map.entry(EntityType.BREEZE, Items.BREEZE_ROD),
			Map.entry(EntityType.GHAST, Items.GHAST_TEAR),
			Map.entry(EntityType.WITCH, Items.REDSTONE),
			Map.entry(EntityType.GUARDIAN, Items.PRISMARINE_SHARD),
			Map.entry(EntityType.ELDER_GUARDIAN, Items.PRISMARINE_SHARD),
			Map.entry(EntityType.SHULKER, Items.SHULKER_SHELL),
			Map.entry(EntityType.PHANTOM, Items.PHANTOM_MEMBRANE),
			Map.entry(EntityType.PIGLIN, Items.GOLD_INGOT),
			Map.entry(EntityType.PIGLIN_BRUTE, Items.GOLD_INGOT),
			Map.entry(EntityType.HOGLIN, Items.PORKCHOP),
			Map.entry(EntityType.WARDEN, Items.SCULK_CATALYST),
			// Illagers / raids
			Map.entry(EntityType.EVOKER, Items.TOTEM_OF_UNDYING),
			Map.entry(EntityType.VINDICATOR, Items.EMERALD),
			Map.entry(EntityType.PILLAGER, Items.ARROW),
			Map.entry(EntityType.RAVAGER, Items.SADDLE),
			// Bosses
			Map.entry(EntityType.WITHER, Items.NETHER_STAR),
			Map.entry(EntityType.ENDER_DRAGON, Items.DRAGON_EGG),
			// Passive / neutral
			Map.entry(EntityType.COW, Items.LEATHER),
			Map.entry(EntityType.MOOSHROOM, Items.LEATHER),
			Map.entry(EntityType.PIG, Items.PORKCHOP),
			Map.entry(EntityType.CHICKEN, Items.FEATHER),
			Map.entry(EntityType.SHEEP, Items.WHITE_WOOL),
			Map.entry(EntityType.RABBIT, Items.RABBIT_HIDE),
			Map.entry(EntityType.COD, Items.COD),
			Map.entry(EntityType.SALMON, Items.SALMON),
			Map.entry(EntityType.PUFFERFISH, Items.PUFFERFISH),
			Map.entry(EntityType.TROPICAL_FISH, Items.TROPICAL_FISH),
			Map.entry(EntityType.SQUID, Items.INK_SAC),
			Map.entry(EntityType.GLOW_SQUID, Items.GLOW_INK_SAC),
			Map.entry(EntityType.DOLPHIN, Items.COD),
			Map.entry(EntityType.POLAR_BEAR, Items.COD),
			Map.entry(EntityType.HORSE, Items.LEATHER),
			Map.entry(EntityType.DONKEY, Items.LEATHER),
			Map.entry(EntityType.MULE, Items.LEATHER),
			Map.entry(EntityType.LLAMA, Items.LEATHER),
			Map.entry(EntityType.TRADER_LLAMA, Items.LEATHER),
			Map.entry(EntityType.CAT, Items.STRING),
			Map.entry(EntityType.PANDA, Items.BAMBOO),
			Map.entry(EntityType.PARROT, Items.FEATHER),
			Map.entry(EntityType.STRIDER, Items.STRING),
			Map.entry(EntityType.TURTLE, Items.TURTLE_SCUTE),
			Map.entry(EntityType.ARMADILLO, Items.ARMADILLO_SCUTE),
			// Villagers / golems
			Map.entry(EntityType.VILLAGER, Items.EMERALD),
			Map.entry(EntityType.WANDERING_TRADER, Items.EMERALD),
			Map.entry(EntityType.IRON_GOLEM, Items.IRON_INGOT),
			Map.entry(EntityType.SNOW_GOLEM, Items.SNOWBALL),
			// Other passive / neutral (thematic where they drop nothing)
			Map.entry(EntityType.ALLAY, Items.AMETHYST_SHARD),
			Map.entry(EntityType.BEE, Items.HONEYCOMB),
			Map.entry(EntityType.FOX, Items.SWEET_BERRIES),
			Map.entry(EntityType.WOLF, Items.BONE),
			Map.entry(EntityType.OCELOT, Items.COD),
			Map.entry(EntityType.AXOLOTL, Items.TROPICAL_FISH),
			Map.entry(EntityType.GOAT, Items.GOAT_HORN),
			Map.entry(EntityType.FROG, Items.FROGSPAWN),
			Map.entry(EntityType.TADPOLE, Items.FROGSPAWN),
			Map.entry(EntityType.SNIFFER, Items.TORCHFLOWER_SEEDS),
			Map.entry(EntityType.SKELETON_HORSE, Items.BONE),
			Map.entry(EntityType.ZOMBIE_HORSE, Items.ROTTEN_FLESH),
			Map.entry(EntityType.GIANT, Items.ROTTEN_FLESH),
			Map.entry(EntityType.ILLUSIONER, Items.BOW),
			Map.entry(EntityType.CAMEL_HUSK, Items.ROTTEN_FLESH),
			// New in 26.1
			Map.entry(EntityType.COPPER_GOLEM, Items.COPPER_INGOT),
			Map.entry(EntityType.CREAKING, Items.RESIN_CLUMP),
			Map.entry(EntityType.HAPPY_GHAST, Items.GHAST_TEAR),
			Map.entry(EntityType.NAUTILUS, Items.NAUTILUS_SHELL),
			Map.entry(EntityType.ZOMBIE_NAUTILUS, Items.NAUTILUS_SHELL));

	public static Item iconFor(EntityType<?> type) {
		return DROPS.get(type);
	}
}
