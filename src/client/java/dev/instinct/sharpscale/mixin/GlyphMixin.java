package dev.instinct.sharpscale.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.instinct.sharpscale.SharpScale;
import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pixel-perfect text at fractional GUI scales. Each font texel is drawn as a whole number N of
 * screen pixels (N = floor(scale + 0.3)), glyph origins snap to the pixel grid, and shadow/bold
 * offsets are whole pixels. Only affects GUI text (flat == true); in-world text is untouched.
 */
@Mixin(BakedSheetGlyph.class)
public abstract class GlyphMixin {
	@Shadow @Final private float u0;
	@Shadow @Final private float u1;
	@Shadow @Final private float v0;
	@Shadow @Final private float v1;
	@Shadow @Final private float left;
	@Shadow @Final private float right;
	@Shadow @Final private float up;
	@Shadow @Final private float down;

	@Inject(method = "renderChar", at = @At("HEAD"), cancellable = true)
	private void sharpscale$renderChar(BakedSheetGlyph.GlyphInstance gi, Matrix4fc pose, VertexConsumer buffer, int light, boolean flat, CallbackInfo ci) {
		// TrueType glyphs use Minecraft's normal antialiased renderer. The bitmap-specific
        // integer texel scaling below would distort their spacing at fractional scale.
        if (SharpScale.monoFont && gi.style().getFont() instanceof net.minecraft.network.chat.FontDescription.Resource r
            && r.id().getNamespace().equals("minecraft")
            && (r.id().getPath().equals("default") || r.id().getPath().equals("uniform"))) return;
        double s = SharpScale.current;
		if (!flat || s < 1.0 || !SharpScale.pureTranslation(pose)) {
			return;
		}
		int n = Math.max(1, (int) Math.floor(s + 0.3));
		SharpScale.glyphHits.incrementAndGet();
		Style style = gi.style();
		boolean italic = style.isItalic();
		boolean bold = style.isBold();
		long ox = Math.round((pose.m30() + gi.x()) * s);
		long oy = Math.round((pose.m31() + gi.y()) * s);
		int color = gi.color();
		boolean shadow = gi.shadowColor() != 0;
		int shOff = shadow ? (int) Math.max(1, Math.round(gi.shadowOffset() * n)) : 0;
		int boldOff = bold ? (int) Math.max(1, Math.round(gi.boldOffset() * n)) : 0;
		float depth = 0.0F;
		if (shadow) {
			this.sharpscale$quad(ox + shOff, oy + shOff, n, 0.0F, pose, buffer, gi.shadowColor(), italic, light, s);
			if (bold) {
				this.sharpscale$quad(ox + shOff + boldOff, oy + shOff, n, 0.0F, pose, buffer, gi.shadowColor(), italic, light, s);
			}
			depth = 0.0F;
		}
		this.sharpscale$quad(ox, oy, n, depth, pose, buffer, color, italic, light, s);
		if (bold) {
			this.sharpscale$quad(ox + boldOff, oy, n, depth, pose, buffer, color, italic, light, s);
		}
		ci.cancel();
	}

	private void sharpscale$quad(long px, long py, int n, float z, Matrix4fc pose, VertexConsumer b, int color, boolean italic, int light, double s) {
		double x0 = px + Math.round(this.left * n);
		double x1 = px + Math.round(this.right * n);
		double y0 = py + Math.round(this.up * n);
		double y1 = py + Math.round(this.down * n);
		double shTop = italic ? Math.round((1.0F - 0.25F * this.up) * n) : 0;
		double shBot = italic ? Math.round((1.0F - 0.25F * this.down) * n) : 0;
		float ox = pose.m30();
		float oy = pose.m31();
		b.addVertex(pose, (float) ((x0 + shTop) / s - ox), (float) (y0 / s - oy), z).setColor(color).setUv(this.u0, this.v0).setLight(light);
		b.addVertex(pose, (float) ((x0 + shBot) / s - ox), (float) (y1 / s - oy), z).setColor(color).setUv(this.u0, this.v1).setLight(light);
		b.addVertex(pose, (float) ((x1 + shBot) / s - ox), (float) (y1 / s - oy), z).setColor(color).setUv(this.u1, this.v1).setLight(light);
		b.addVertex(pose, (float) ((x1 + shTop) / s - ox), (float) (y0 / s - oy), z).setColor(color).setUv(this.u1, this.v0).setLight(light);
	}
}
