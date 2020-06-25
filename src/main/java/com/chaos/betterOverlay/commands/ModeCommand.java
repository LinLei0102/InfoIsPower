package com.chaos.betterOverlay.commands;

import java.util.ArrayList;
import java.util.Arrays;

import com.chaos.betterOverlay.Config;
import com.chaos.betterOverlay.utils.Translatable;
import com.chaos.betterOverlay.utils.Translatable.TranslateType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

public class ModeCommand {
	private static final SuggestionProvider<CommandSource> SUGGEST_NUMBERS = (source, builder) -> {
		return ISuggestionProvider.suggest(new ArrayList<String>(Arrays.asList(new String[] { "1", "2", "3" })), builder);
	};

	public static void register(CommandDispatcher<CommandSource> dis) {
		dis.register(Commands.literal("mode").then(Commands.argument("mode_number", IntegerArgumentType.integer(1, 3)).suggests(SUGGEST_NUMBERS).executes(src -> {
			return modeChange(src.getSource(), IntegerArgumentType.getInteger(src, "mode_number"));
		})));
	}

	private static int modeChange(CommandSource src, int mode) throws CommandSyntaxException {
		Config.CLIENT.mode.set(mode);
		String s = "";
		switch (mode) {
		case 1:
			s = "heldItemMode";
			break;
		case 2:
			s = "armorMode";
			break;
		case 3:
			s = "disableMode";
			break;
		default:
			break;
		}
		src.sendFeedback(new Translatable("success.mode", TranslateType.COMMAND, new Translatable(s, TranslateType.COMMAND).getFormattedText()), true);
		return 1;
	}
}
