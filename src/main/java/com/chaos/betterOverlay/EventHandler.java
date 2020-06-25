package com.chaos.betterOverlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chaos.betterOverlay.utils.GUIElementLocator;
import com.chaos.betterOverlay.utils.GUIElementLocator.LocatorGapTypes;
import com.chaos.betterOverlay.utils.GUIElementLocator.LocatorTypes;
import com.chaos.betterOverlay.utils.Translatable;
import com.chaos.betterOverlay.utils.Translatable.TranslateType;
import com.google.common.collect.Lists;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SpectralArrowItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mod.EventBusSubscriber(modid = BetterOverlay.MODID, value = Dist.CLIENT)
public class EventHandler {
	private static final int color = 16777215;

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Text event) {
		Minecraft mc = Minecraft.getInstance();
		World world = mc.getConnection().getWorld();
		FontRenderer font = mc.fontRenderer;
		GUIElementLocator locator = GUIElementLocator.getInstance();
		DecimalFormat df = new DecimalFormat("0.00");
		int fps = 0;
		try {
			fps = (int) ObfuscationReflectionHelper.findField(Minecraft.class, "field_71470_ab").getInt(mc);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		PlayerEntity player = mc.player;
		BlockPos pos = player.getPosition();
		List<NetworkPlayerInfo> infos = new ArrayList<NetworkPlayerInfo>(mc.getConnection().getPlayerInfoMap());
		if (!mc.gameSettings.showDebugInfo) {
			// ============================BASE MESSAGES PART============================//
			simpleStringDraw(font, new Translatable("fps", null, fps).getFormattedText(), 2, locator.begin(LocatorTypes.LEFT_UP, mc).getCurrent(), color);
			simpleStringDraw(font, new Translatable("biome", null, world.getBiome(pos).getDisplayName().getFormattedText()).getFormattedText(), 2, locator
					.getNextLocation(LocatorGapTypes.TEXT), color);
			if (!player.isCreative()) {
				simpleStringDraw(font, new Translatable("health", null, df.format(player.getHealth()))
						.applyTextStyle((player.getHealth() > 10.0 ? TextFormatting.GREEN : (player.getHealth() > 5.0 ? TextFormatting.YELLOW : TextFormatting.RED)))
						.getFormattedText(), 2, locator.getNextLocation(LocatorGapTypes.TEXT), color);
				simpleStringDraw(font, new Translatable("hunger", null, player.getFoodStats().getFoodLevel(), df.format(player.getFoodStats().getSaturationLevel())).applyTextStyle(TextFormatting.GOLD)
						.getFormattedText(), 2, locator.getNextLocation(LocatorGapTypes.TEXT), color);
				simpleStringDraw(font, new Translatable("armor", null, player.getTotalArmorValue()).applyTextStyle(TextFormatting.GRAY).getFormattedText(), 2, locator
						.getNextLocation(LocatorGapTypes.TEXT), color);
			}
			// ============================POS & PLAYERS PART============================//
			simpleStringDraw(font, new Translatable("pos", null, pos.getX(), pos.getY(), pos.getZ()).getFormattedText(), 2, locator.getNextLocation(LocatorGapTypes.TEXT), color);
			if (!mc.isSingleplayer()) {
				simpleStringDraw(font, new Translatable("playerList", null).getFormattedText(), 2, locator.getNextLocation(LocatorGapTypes.TEXT), color);
				int counts = 0;
				for (NetworkPlayerInfo s : infos) {
					if (counts == 5)
						break;
					simpleStringDraw(font, (s.getGameProfile().getName().equals(player.getName().getString()) ? TextFormatting.GOLD : TextFormatting.GRAY) + s.getGameProfile().getName(), 2, locator
							.getNextLocation(LocatorGapTypes.TEXT), color);
					counts++;
				}
			}
			// ============================HELD ITEM PART============================//
			if (!mc.ingameGUI.getChatGUI().getChatOpen()) {
				locator.begin(LocatorTypes.LEFT_CENTER, mc);
				EquipmentSlotType[] types = EquipmentSlotType.values();
				Iterator<EquipmentSlotType> handType = Arrays.asList(new EquipmentSlotType[] { types[0], types[1] }).iterator();
				Iterator<EquipmentSlotType> armorType = Arrays.asList(new EquipmentSlotType[] { types[5], types[4], types[3], types[2] }).iterator();
				switch (Config.CLIENT.mode.get()) {
				case 1:
					(Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
						getAllInfos(mc, stack, armorType.next(), locator, true);
					});
					locator.getNextLocation(LocatorGapTypes.ITEM);
					player.getHeldEquipment().forEach(stack -> {
						getAllInfos(mc, stack, handType.next(), locator, false);
						locator.getNextLocation(LocatorGapTypes.ITEM);
					});
					break;
				case 2:
					(Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
						getAllInfos(mc, stack, armorType.next(), locator, false);
						locator.getNextLocation(LocatorGapTypes.TEXT);
					});
					player.getHeldEquipment().forEach(stack -> {
						getAllInfos(mc, stack, handType.next(), locator, true);
					});
					break;
				case 3:
					break;
				default:
					break;
				}
			}
			locator.end();
		}
	}

	private static void simpleStringDraw(FontRenderer font, String text, int x, int y, int color) {
		font.drawStringWithShadow(text, x, y, color);
	}

	private static void simpleItemDraw(FontRenderer font, ItemRenderer renderer, ItemStack stack, int x, int y) {
		renderer.renderItemAndEffectIntoGUI(stack, x, y);
		renderer.renderItemOverlayIntoGUI(font, stack, x, y, null);
	}

	private static void getAllInfos(Minecraft mc, ItemStack stack, EquipmentSlotType slot, GUIElementLocator locator, boolean simple) {
		ItemRenderer renderer = mc.getItemRenderer();
		FontRenderer font = mc.fontRenderer;
		String s = stack.getTextComponent().getFormattedText() + " | ";
		List<ITextComponent> texts = new ArrayList<ITextComponent>();
		Item item = stack.getItem();
		if (stack.isEmpty())
			return;
		if (simple) {
			simpleItemDraw(font, renderer, stack, 2, locator.getNextLocation(LocatorGapTypes.ITEM));
			simpleStringDraw(font, s + new Translatable(slot.getName(), TranslateType.MISC).getFormattedText(), 20, locator.getCurrent(), color);
			return;
		}
		int times = 0, enchtimes = 0;
		if (stack.hasTag()) {
			CompoundNBT tag = stack.getTag();
			Block blk = Block.getBlockFromItem(item);
			if (stack.getEnchantmentTagList().size() > 0) {
				Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
				for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
					texts.add(ench.getKey().getDisplayName(ench.getValue()).applyTextStyle(ench.getKey().getMaxLevel() == ench.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN));
					enchtimes++;
				}
			} else if (blk instanceof BeehiveBlock) {
				int infos[] = { tag.getCompound("BlockStateTag").getInt("honey_level"), tag.getCompound("BlockEntityTag").getList("Bees", 10).size() };
				texts.add(new Translatable("honeyLevel", null, infos[0]).applyTextStyle(infos[0] == 5 ? TextFormatting.GOLD : TextFormatting.GREEN));
				texts.add(new Translatable("honeyLevel", null, infos[0]).applyTextStyle(infos[0] == 5 ? TextFormatting.GOLD : TextFormatting.GREEN));
			} else if (blk instanceof ShulkerBoxBlock) {
				ListNBT list = tag.getCompound("BlockEntityTag").getList("Items", 10);
				for (int i = 0, p = locator.getCurrent() + 20; i < list.size(); i++) {
					if (i % 9 == 0 && i != 0) {
						p += 20;
						times++;
					}
					simpleItemDraw(font, renderer, ItemStack.read((CompoundNBT) list.get(i)), 2 + (i % 9) * 20, p - 3);
				}
			} else if (item instanceof FilledMapItem) {
				int type = -1, target = -1;
				ListNBT list = tag.getList("Decorations", 10);
				for (int i = 0; i < list.size(); i++) {
					byte testType = list.getCompound(i).getByte("type");
					if (testType == 8 || testType == 9 || testType == 26) {
						type = testType;
						target = i;
					}
				}
				if (type == -1) {
					texts.add(new Translatable("normalTotal", null, list.size()).applyTextStyle(list.size() > 1 ? TextFormatting.GREEN : TextFormatting.GRAY));
				} else {
					CompoundNBT nbt = list.getCompound(target);
					texts.add(new Translatable("explorerPos", null, nbt.getInt("x"), nbt.getInt("z"))
							.applyTextStyle(type == 8 ? TextFormatting.GREEN : (type == 9 ? TextFormatting.AQUA : TextFormatting.GOLD)));
				}
			} else if (item instanceof EnchantedBookItem) {
				Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
				for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
					texts.add(ench.getKey().getDisplayName(ench.getValue()).applyTextStyle(ench.getKey().getMaxLevel() == ench.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN));
				}
			} else if (item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem || item instanceof SpectralArrowItem || item instanceof TippedArrowItem) {
				List<ITextComponent> tooltip = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
				tooltip.remove(2);
				tooltip.remove(1);
				for (ITextComponent text : tooltip) {
					texts.add(text);
				}
			}
		}
		if (stack.isDamageable()) {
			texts.add(new Translatable("durability", null, (stack.getMaxDamage() - stack.getDamage()), stack.getMaxDamage()));
			if (item instanceof ArmorItem || item instanceof ToolItem || item instanceof TieredItem) {
				List<ITextComponent> tooltip = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
				texts.add(tooltip.get(enchtimes + 3));
				texts.add(tooltip.get(enchtimes + 4));
			}
		}
		s += new Translatable(slot.getName(), TranslateType.MISC).getFormattedText();
		simpleItemDraw(font, renderer, stack, 2, locator.getCurrent());
		simpleStringDraw(font, s, 20, locator.getCurrent(), color);
		locator.getNextLocation(LocatorGapTypes.TEXT);
		for (ITextComponent t : texts) {
			simpleStringDraw(font, t.getFormattedText(), 2, locator.getNextLocation(LocatorGapTypes.TEXT), color);
		}
		if (times == 0)
			return;
		for (int j = 0; j < times + 1; j++) {
			locator.getNextLocation(LocatorGapTypes.ITEM);
		}
	}
}
