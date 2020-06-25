package com.chaos.betterOverlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaos.betterOverlay.commands.ModeCommand;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BetterOverlay.MODID)
@Mod.EventBusSubscriber(modid = BetterOverlay.MODID, bus = Bus.MOD)
public class BetterOverlay {
	public static final Logger LOGGER = LogManager.getLogger("better_overlay");
	public static final String MODID = "better_overlay";

	public BetterOverlay() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC, "better-overlay.toml");
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	private void setup(final FMLCommonSetupEvent event) {
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		ModeCommand.register(event.getCommandDispatcher());
	}
}
