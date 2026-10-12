package dev.instinct.sharpscale.mixin;

import dev.instinct.sharpscale.SharpScale;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsMixin {
	@Redirect(method = "displayOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;guiScale()Lnet/minecraft/client/OptionInstance;"))
	private static OptionInstance<Integer> sharpscale$option(Options options) {
		return SharpScale.option();
	}
}
