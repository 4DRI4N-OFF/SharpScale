package dev.instinct.sharpscale.mixin;
import dev.instinct.sharpscale.SharpScale;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
/** Use the same source for measuring/rendering. Keep icon/player/custom fonts. */
@Mixin(Font.class)
public abstract class FontMixin {
 @Unique private static final FontDescription sharpscale$mono = new FontDescription.Resource(Identifier.fromNamespaceAndPath("sharpscale","jetbrains_mono"));
 @ModifyVariable(method="getGlyphSource",at=@At("HEAD"),argsOnly=true,require=1)
 private FontDescription sharpscale$font(FontDescription font){
  if (SharpScale.monoFont && font instanceof FontDescription.Resource r && r.id().getNamespace().equals("minecraft") && (r.id().getPath().equals("default") || r.id().getPath().equals("uniform"))) return sharpscale$mono;
  return font;
 }
}
