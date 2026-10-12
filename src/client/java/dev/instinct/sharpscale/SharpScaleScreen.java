package dev.instinct.sharpscale;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Standalone settings screen (opened from Mod Menu), works even when Sodium replaces the video options. */
public class SharpScaleScreen extends Screen {
	private final Screen parent;

	public SharpScaleScreen(Screen parent) {
		super(Component.literal("SharpScale"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int w = 200;
		int x = this.width / 2 - w / 2;
		this.addRenderableWidget(SharpScale.option().createButton(this.minecraft.options, x, this.height / 2 - 24, w));
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(x, this.height / 2 + 4, w, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.text(this.font, this.title.getString(), this.width / 2 - this.font.width(this.title.getString()) / 2, this.height / 2 - 52, 0xFFFFFFFF);
		String hint = "0 = vanilla. Keys [ and ] change it in game.";
		graphics.text(this.font, hint, this.width / 2 - this.font.width(hint) / 2, this.height / 2 - 40, 0xFFA0A0A0);
	}

	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(this.parent);
	}
}
