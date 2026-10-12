package dev.instinct.sharpscale.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.instinct.sharpscale.SharpScale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Shadow private int framebufferWidth;
	@Shadow private int framebufferHeight;
	@Shadow private int guiScaledWidth;
	@Shadow private int guiScaledHeight;
	@Shadow private int guiScale;

	@Inject(method = "setGuiScale", at = @At("HEAD"), cancellable = true)
	private void sharpscale$setGuiScale(int vanillaScale, CallbackInfo ci) {
		double s = SharpScale.effective(this.framebufferWidth, this.framebufferHeight);
		SharpScale.current = s;
		if (s <= 0) {
			return;
		}
		// integer field stays >= the real scale (used for item atlas / PiP texture sizes)
		this.guiScale = Math.max(1, (int) Math.ceil(s));
		this.guiScaledWidth = (int) Math.ceil(this.framebufferWidth / s);
		this.guiScaledHeight = (int) Math.ceil(this.framebufferHeight / s);
		ci.cancel();
	}
}
