package dev.instinct.sharpscale;

import java.io.File;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

/** Dev-only: runs when run/sharpscale_selftest.txt exists. Cycles scales and takes screenshots. */
final class SharpScaleSelfTest {
	private static final int[] STEPS = {0, 10, 11};
	private static int idx = -1;
	private static int wait = 0;

	static void tick(Minecraft mc) {
		File marker = new File(mc.gameDirectory, "sharpscale_selftest.txt");
		if (!marker.exists() || mc.level == null) {
			return;
		}
		if (idx == -1) {
			if (++wait < 200) {
				return;
			}
			idx = 0;
			wait = 0;
			apply(mc);
			return;
		}
		if (++wait == 30) {
			net.minecraft.client.Screenshot.grab(mc.gameDirectory, mc.gameRenderer.mainRenderTarget(), c -> {});
		}
		if (wait >= 50) {
			wait = 0;
			idx++;
			if (idx >= STEPS.length * 3) {
				try {
					Files.writeString(new File(mc.gameDirectory, "sharpscale_selftest_result.txt").toPath(),
						"done glyphHits=" + SharpScale.glyphHits.get() + " current=" + SharpScale.current + "\n");
				} catch (Exception ignored) {
				}
				marker.delete();
				mc.stop();
				return;
			}
			apply(mc);
		}
	}

	private static void apply(Minecraft mc) {
		int step = idx / 3;
		int kind = idx % 3;
		SharpScale.steps = STEPS[step];
		if (idx == 0) {
			try {
				mc.options.debugGuiScale().set(0);
			} catch (Throwable t) {
			}
			mc.debugEntries.toggleDebugOverlay();
		}
		try {
			mc.player.getFoodData().setFoodLevel(11);
			mc.player.getFoodData().setSaturation(5.0f);
			mc.player.getInventory().setItem(0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_BEEF, 4));
			mc.player.getInventory().setSelectedSlot(0);
		} catch (Throwable t) {
		}
		mc.resizeGui();
		if (kind == 0) {
			mc.gui.setScreen(null);
		} else if (kind == 1) {
			mc.gui.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
		} else {
			mc.gui.setScreen(new PauseScreen(true));
		}
	}
}
