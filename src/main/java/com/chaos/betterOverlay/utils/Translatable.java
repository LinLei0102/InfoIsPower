package com.chaos.betterOverlay.utils;

import com.chaos.betterOverlay.BetterOverlay;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TranslationTextComponent;

public class Translatable extends TranslationTextComponent {
	/**
	 * Lazy translate, default type is OVERLAY.
	 * 
	 * @param translationKey
	 * @param type
	 * @param args
	 */
	public Translatable(String translationKey, TranslateType type, Object... args) {
		super(BetterOverlay.MODID + (type == null ? TranslateType.OVERLAY.getName() : type.getName()) + translationKey, args);
	}

	public enum TranslateType implements IStringSerializable {
		OVERLAY(".overlay."), COMMAND(".command."), MISC(".misc."), CONFIG(".config.");

		private String s;

		TranslateType(String s) {
			this.s = s;
		}

		@Override
		public String getName() {
			return s;
		}
	}
}
