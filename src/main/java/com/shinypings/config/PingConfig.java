package com.shinypings.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shinypings.ShinyPingsClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// JSON config at .minecraft/config/shiny-pings.json. Durations are in ticks (20 ticks = 1 second).
public class PingConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH =
			FabricLoader.getInstance().getConfigDir().resolve("shiny-pings.json");

	public String toastHeader = "Spotted!";
	public String toastTitle = "You spotted a {mob}";
	public String toastSound = "minecraft:block.amethyst_block.fall";

	public boolean expandEnabled = true;
	public double expandRadius = 10.0;
	public int expandDelayTicks = 4;
	public String expandSound = "minecraft:block.amethyst_block.resonate";

	public int entityGlowDurationTicks = 100;
	public int itemSpotDurationTicks = 200;
	public String itemSpotText = "Spotted at {dist}m";
	public double pingRange = 64.0;

	private static PingConfig instance = new PingConfig();

	public static PingConfig get() {
		return instance;
	}

	public static void load() {
		try {
			if (Files.exists(PATH)) {
				PingConfig loaded = GSON.fromJson(Files.readString(PATH), PingConfig.class);
				instance = (loaded != null) ? loaded : new PingConfig();
			} else {
				save();
			}
		} catch (IOException | RuntimeException e) {
			ShinyPingsClient.LOGGER.warn("Could not read config, using defaults", e);
			instance = new PingConfig();
		}
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Files.writeString(PATH, GSON.toJson(instance));
		} catch (IOException e) {
			ShinyPingsClient.LOGGER.warn("Could not save config", e);
		}
	}
}
