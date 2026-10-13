package dev.instinct.sharpscale;

import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

/** Fine GUI scale: steps of 0.25, rendered straight to the framebuffer (no resampling = no blur). */
public class SharpScale implements ClientModInitializer {
	public static final int MAX_STEPS = 32; // x8.00
	public static final int MIN_ACTIVE_STEPS = 2; // x0.50
	/** 0 = off (vanilla option applies). Otherwise scale = steps / 4. */
	public static int steps = 0;
	/** Effective fractional scale currently applied, or 0 when vanilla integer scale is in use. */
	public static volatile double current = 0;
	private static OptionInstance<Integer> option;
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("sharpscale.txt");
	private static final Path OLD_FILE = FabricLoader.getInstance().getConfigDir().resolve("finegui.txt");

	public static boolean monoFont = true;
	private static final Path FONT_FILE = FabricLoader.getInstance().getConfigDir().resolve("sharpscale-font.txt");
	private static long lastMod = 0;

	private static net.minecraft.client.KeyMapping keyUp;
	private static net.minecraft.client.KeyMapping keyDown;
	public static final java.util.concurrent.atomic.AtomicLong glyphHits = new java.util.concurrent.atomic.AtomicLong();

	private static void setSteps(int v) {
		steps = Math.max(0, Math.min(MAX_STEPS, v));
		save();
		Minecraft mc = Minecraft.getInstance();
		if (option != null) {
			option.set(steps);
		}
		mc.resizeGui();
		if (mc.player != null) {
			mc.player.sendSystemMessage(Component.literal("GUI scale: " + (steps == 0 ? "vanilla" : "x" + String.format(java.util.Locale.ROOT, "%.2f", Math.max(steps, MIN_ACTIVE_STEPS) / 4.0))));
		}
	}

	private static void tickKeys(Minecraft mc) {
		while (keyUp.consumeClick()) {
			setSteps(steps == 0 ? 9 : steps + 1);
		}
		while (keyDown.consumeClick()) {
			setSteps(steps == 0 ? 7 : steps - 1);
		}
	}

	@Override
	public void onInitializeClient() {
		load();
		loadFont();
		keyUp = net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(
			new net.minecraft.client.KeyMapping("key.sharpscale.up", com.mojang.blaze3d.platform.InputConstants.KEY_RBRACKET, net.minecraft.client.KeyMapping.Category.MISC));
		keyDown = net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(
			new net.minecraft.client.KeyMapping("key.sharpscale.down", com.mojang.blaze3d.platform.InputConstants.KEY_LBRACKET, net.minecraft.client.KeyMapping.Category.MISC));
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(SharpScale::tickKeys);
		// live reload when the config file is edited by hand (handy for testing)
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			try {
				if (mc.level != null && mc.gui != null && Files.exists(FILE) && (mc.gameRenderer != null)) {
					long m = Files.getLastModifiedTime(FILE).toMillis();
					if (lastMod == 0) {
						lastMod = m;
					} else if (m != lastMod) {
						lastMod = m;
						int old = steps;
						load();
						if (old != steps) {
							mc.resizeGui();
						}
					}
				}
			} catch (Exception ignored) {
			}
		});
	}

    private static void loadFont() {
        try { if (Files.exists(FONT_FILE)) monoFont = !Files.readString(FONT_FILE).trim().equals("minecraft"); } catch (Exception ignored) {}
    }
    public static void toggleFont() {
        boolean next = !monoFont;
        try { Files.createDirectories(FONT_FILE.getParent()); Files.writeString(FONT_FILE, next ? "jetbrains_mono" : "minecraft"); monoFont = next; } catch (Exception e) { org.slf4j.LoggerFactory.getLogger("SharpScale").error("Cannot save font choice", e); return; }
        Minecraft.getInstance().resizeGui();
    }
	public static boolean pureTranslation(org.joml.Matrix4fc m) {
		return Math.abs(m.m00() - 1) < 1e-4 && Math.abs(m.m11() - 1) < 1e-4 && Math.abs(m.m01()) < 1e-4 && Math.abs(m.m10()) < 1e-4;
	}

	private static void load() {
		try {
			if (!Files.exists(FILE) && Files.exists(OLD_FILE)) {
				Files.copy(OLD_FILE, FILE);
			}
			if (Files.exists(FILE)) {
				steps = Math.max(0, Math.min(MAX_STEPS, Integer.parseInt(Files.readString(FILE).trim())));
			}
		} catch (Exception ignored) {
		}
	}

	private static void save() {
		try {
			Files.writeString(FILE, Integer.toString(steps));
		} catch (Exception ignored) {
		}
	}

	public static OptionInstance<Integer> option() {
		if (option == null) {
			load();
			option = new OptionInstance<>(
				"options.guiScale",
				OptionInstance.noTooltip(),
				(caption, v) -> v == 0
					? Component.literal("GUI Scale: Vanilla")
					: Component.literal("GUI Scale: x" + String.format(java.util.Locale.ROOT, "%.2f", Math.max(v, MIN_ACTIVE_STEPS) / 4.0)),
				new OptionInstance.IntRange(0, MAX_STEPS),
				steps,
				v -> {
					steps = v;
					save();
					Minecraft.getInstance().resizeGui();
				}
			);
		}
		return option;
	}

	/** Returns the effective fractional scale for a framebuffer, or 0 if the vanilla scale should be used. */
	public static double effective(int fbW, int fbH) {
		if (steps <= 0) {
			return 0;
		}
		double s = Math.max(steps, MIN_ACTIVE_STEPS) / 4.0;
		// keep at least 240x180 scaled px so screens stay usable
		s = Math.min(s, Math.min(fbW / 240.0, fbH / 180.0));
		return Math.max(0.5, s);
	}
}
