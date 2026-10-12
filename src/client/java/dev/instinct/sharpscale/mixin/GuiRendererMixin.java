package dev.instinct.sharpscale.mixin;

import com.mojang.renderpearl.api.commands.RenderPass;
import dev.instinct.sharpscale.SharpScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.WindowRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
	// Projection size in GUI units: framebuffer / fractional scale (vanilla divides by the int scale).
	@ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Projection;setupOrtho(FFFFZ)V"), index = 2)
	private float sharpscale$orthoW(float w) {
		WindowRenderState ws = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState;
		return SharpScale.current > 0 ? (float) (ws.width / SharpScale.current) : w;
	}

	@ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Projection;setupOrtho(FFFFZ)V"), index = 3)
	private float sharpscale$orthoH(float h) {
		WindowRenderState ws = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState;
		return SharpScale.current > 0 ? (float) (ws.height / SharpScale.current) : h;
	}

	@Inject(method = "enableScissor", at = @At("HEAD"), cancellable = true)
	private void sharpscale$scissor(ScreenRectangle rectangle, RenderPass renderPass, CallbackInfo ci) {
		double s = SharpScale.current;
		if (s <= 0) {
			return;
		}
		WindowRenderState window = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState;
		double left = Math.floor(rectangle.left() * s);
		double top = Math.floor(rectangle.top() * s);
		double right = Math.min(Math.ceil(rectangle.right() * s), window.width);
		double bottom = Math.min(Math.ceil(rectangle.bottom() * s), window.height);
		renderPass.enableScissor((int) left, window.height - (int) bottom, Math.max(0, (int) (right - left)), Math.max(0, (int) (bottom - top)));
		ci.cancel();
	}
}
