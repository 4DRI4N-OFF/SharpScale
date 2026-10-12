package dev.instinct.sharpscale.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.instinct.sharpscale.SharpScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Vanilla F3 "debug GUI scale" assumes one GUI unit = getGuiScale() pixels (an int). With a fractional scale
 * that is wrong, so the right column ended short of the screen edge. Rebase the factor on the real scale.
 */
@Mixin(DebugScreenOverlay.class)
public abstract class DebugOverlayMixin {
	@ModifyArg(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;"), index = 0, require = 0)
	private float sharpscale$scaleX(float factor) {
		return fix(factor);
	}

	@ModifyArg(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;"), index = 1, require = 0)
	private float sharpscale$scaleY(float factor) {
		return fix(factor);
	}

	private static float fix(float factor) {
		double s = SharpScale.current;
		if (s <= 0) {
			return factor;
		}
		Window w = Minecraft.getInstance().getWindow();
		return (float) (factor * w.getGuiScale() / s);
	}
}
