package com.flier268.more_tooltips;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)

public class MoreTooltips implements ClientModInitializer {
	public static Logger logger = LogManager.getLogger();
	;
	public static final String MOD_ID = "more_tooltips";

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		TooltipEventHandler.addMoreTooltip();

		//hotkey of config
		KeyBinding key_config = new KeyBinding("key.config", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");
		KeyBindingHelper.registerKeyBinding(key_config);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (key_config.wasPressed()) {
				MinecraftClient.getInstance().openScreen(AutoConfig.getConfigScreen(ModConfig.class, MinecraftClient.getInstance().currentScreen).get());
			}
		});
	}
}
