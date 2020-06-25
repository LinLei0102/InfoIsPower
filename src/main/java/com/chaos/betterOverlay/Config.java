package com.chaos.betterOverlay;

import org.apache.commons.lang3.tuple.Pair;

import com.chaos.betterOverlay.utils.Translatable;
import com.chaos.betterOverlay.utils.Translatable.TranslateType;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = BetterOverlay.MODID, bus = Bus.MOD)
public class Config {
	public static class Client {
		public final IntValue mode;

		public Client(Builder builder) {
			builder.comment("Better Overlay Config").push("betterOverlay");

			mode = builder.comment("This sets the detail mode. 1 is held items only, 2 is armor only, 3 is nondetailed mode. Default value is 1.")
					.translation(new Translatable("mode", TranslateType.CONFIG).getKey()).defineInRange("Modes", 1, 1, 3);

			builder.pop();
		}
	}

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}
}
