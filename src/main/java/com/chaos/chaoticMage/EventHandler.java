package com.chaos.chaoticMage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chaos.chaoticMage.utils.GUIElementLocator;
import com.chaos.chaoticMage.utils.GUIElementLocator.LocatorTypes;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(value = Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "better_overlay", value = Dist.CLIENT)
public class EventHandler {
	private static Minecraft mc = Minecraft.getInstance();
	private final int color = 16777215;
	private GUIElementLocator locator = GUIElementLocator.getInstance();

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		PlayerEntity player = mc.player;
		if (event.getType() == ElementType.TEXT && !mc.gameSettings.showDebugInfo) {
			// ============================BASE MESSAGES PART============================//
			simpleStringDraw("玩家 : " + player.getName().getString(), 2,
					locator.begin(LocatorTypes.LEFT_UP).getCurrent(), color);
			// ============================NON CREATIVE MODE
			// PART============================//
			if (!player.isCreative()) {
				simpleStringDraw((player.getHealth() > 10.0 ? TextFormatting.GREEN
						: (player.getHealth() > 5.0 ? TextFormatting.YELLOW : TextFormatting.RED)) + "血量 : "
						+ (player.getHealth()), 2, locator.getNextLocation(LocatorTypes.LEFT_UP), color);
				simpleStringDraw(TextFormatting.GOLD + "飽食度 : " + player.getFoodStats().getFoodLevel(), 2,
						locator.getNextLocation(LocatorTypes.LEFT_UP), color);
				simpleStringDraw("護甲值 : " + player.getTotalArmorValue(), 2,
						locator.getNextLocation(LocatorTypes.LEFT_UP), color);
			}
			// ============================POS & PLAYERS PART============================//
			BlockPos pos = player.getPosition();
			simpleStringDraw("位置 : " + pos.getX() + "," + pos.getY() + "," + pos.getZ(), 2,
					locator.getNextLocation(LocatorTypes.LEFT_UP), color);
			simpleStringDraw("玩家列表 : ", 2, locator.getNextLocation(LocatorTypes.LEFT_UP), color);
			List<NetworkPlayerInfo> infos = new ArrayList<NetworkPlayerInfo>(mc.getConnection().getPlayerInfoMap());
			for (NetworkPlayerInfo s : infos) {
				simpleStringDraw(
						(s.getGameProfile().getName().equals(player.getName().getString()) ? TextFormatting.GOLD
								: TextFormatting.GRAY) + s.getGameProfile().getName(),
						2, locator.getNextLocation(LocatorTypes.LEFT_UP), color);
			}
			/***
			 * World world = !mc.isSingleplayer() ?
			 * player.getServer().getWorld(player.dimension) : mc.player.world;
			 * RayTracingHelper.INSTANCE.fire(); RayTraceResult mop =
			 * RayTracingHelper.INSTANCE.getTarget(); if (mop instanceof BlockRayTraceResult
			 * && mop.getType() == Type.BLOCK) { BlockRayTraceResult bmop =
			 * (BlockRayTraceResult) mop; BlockPos pos2 = bmop.getPos(); BlockState state =
			 * world.getBlockState(pos2); Block block = state.getBlock(); }
			 ***/
			// ============================HELD ITEM PART============================//
			if (!mc.ingameGUI.getChatGUI().getChatOpen()) {
				locator.begin(LocatorTypes.LEFT_CENTER);
				ItemRenderer renderer = mc.getItemRenderer();
				ItemStack main = player.getHeldItemMainhand();
				ItemStack off = player.getHeldItemOffhand();

				renderer.renderItemAndEffectIntoGUI(off, 2, locator.getNextLocation(LocatorTypes.LEFT_CENTER));
				renderer.renderItemOverlayIntoGUI(mc.fontRenderer, off, 2, locator.getCurrent(), null);
				simpleStringDraw(off.getItem() == Items.AIR ? null : off.getTextComponent().getFormattedText(), 22,
						locator.getCurrent(), color);
				if (off.hasTag() && !off.isEmpty()) {
					simpleStringDraw(getAllInfos(off, Hand.OFF_HAND), 22, locator.getCurrent(), color);
				} else if (!off.isEmpty()) {
					simpleStringDraw(off.getTextComponent().getFormattedText(), 22, locator.getCurrent(), color);
				}
				renderer.renderItemAndEffectIntoGUI(main, 2, locator.getNextLocation(LocatorTypes.LEFT_CENTER) - 11);
				renderer.renderItemOverlayIntoGUI(mc.fontRenderer, main, 2,
						locator.getNextLocation(LocatorTypes.LEFT_CENTER), null);
				if (main.hasTag() && !main.isEmpty()) {
					simpleStringDraw(getAllInfos(main, Hand.MAIN_HAND), 22, locator.getCurrent(), color);
				} else if (!main.isEmpty()) {
					simpleStringDraw(main.getTextComponent().getFormattedText(), 22, locator.getCurrent(), color);
				}
				simpleStringDraw(main.getItem() == Items.AIR ? null : main.getTextComponent().getFormattedText(), 22,
						locator.getCurrent(), color);
			}

			locator.end();
		}
	}

	private static void simpleStringDraw(String text, int x, int y, int color) {
		mc.fontRenderer.drawStringWithShadow(text, x, y, color);
	}

	private static String getAllInfos(ItemStack stack, Hand hand) {
		GUIElementLocator locator = GUIElementLocator.getInstance();
		String s = stack.getTextComponent().getFormattedText();
		int textLength = mc.fontRenderer.getStringWidth(stack.getTextComponent().getString()) + 30;
		if (stack.hasTag()) {
			CompoundNBT tag = stack.getTag();
			Block blk = Block.getBlockFromItem(stack.getItem());
			s += " | ";
			if (stack.getEnchantmentTagList().size() > 0) {
				Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
				int count = 0;
				for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
					count++;
					s += (ench.getKey().getMaxLevel() == ench.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN)
							+ ench.getKey().getDisplayName(ench.getValue()).getString() + TextFormatting.RESET
							+ (count < enchs.size() || stack.isDamageable() ? " / " : "");
				}
			} else if (blk instanceof BeehiveBlock) {
				int infos[] = { tag.getCompound("BlockStateTag").getInt("honey_level"),
						tag.getCompound("BlockEntityTag").getList("Bees", 10).size() };
				s += (infos[0] == 5 ? TextFormatting.GOLD : TextFormatting.GREEN) + "蜂蜜等級 : " + infos[0] + "/5";
				s += TextFormatting.RESET + " / " + (infos[1] == 3 ? TextFormatting.GOLD : TextFormatting.GREEN)
						+ "蜂巢內蜜蜂 : " + infos[1] + "/3";
			} else if (blk instanceof ShulkerBoxBlock) {
				ListNBT list = tag.getCompound("BlockEntityTag").getList("Items", 10);
				if (hand == Hand.MAIN_HAND) {
					for (int i = 0, p = list.size() > 9 ? locator.getCurrent() - 20 * ((int) (list.size() - 1) / 9)
							: locator.getCurrent(); i < list.size(); i++) {
						if (i % 9 == 0 && i != 0) {
							p += 20;
						}
						mc.getItemRenderer().renderItemAndEffectIntoGUI(ItemStack.read((CompoundNBT) list.get(i)),
								textLength + (i % 9) * 20, p - 3);
						mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer,
								ItemStack.read((CompoundNBT) list.get(i)), textLength + (i % 9) * 20, p - 3, null);
					}
				} else if (hand == Hand.OFF_HAND) {
					for (int i = 0, p = locator.getCurrent(); i < list.size(); i++) {
						if (i % 9 == 0 && i != 0) {
							p += 20;
						}
						mc.getItemRenderer().renderItemAndEffectIntoGUI(ItemStack.read((CompoundNBT) list.get(i)),
								textLength + (i % 9) * 20, p - 3);
						mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer,
								ItemStack.read((CompoundNBT) list.get(i)), textLength + (i % 9) * 20, p - 3, null);
					}
				}
			} else if (stack.getItem() == Items.FILLED_MAP) {
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
					return s + (list.size() > 1 ? TextFormatting.GREEN : TextFormatting.GRAY) + "共有 : " + list.size()
							+ "個標誌";
				}
				CompoundNBT nbt = list.getCompound(target);
				int pos[] = { nbt.getInt("x"), nbt.getInt("z") };
				s += (type == 8 ? TextFormatting.GREEN : (type == 9 ? TextFormatting.AQUA : TextFormatting.GOLD))
						+ "位置 : " + pos[0] + "," + pos[1];
			} else if (stack.getItem() == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
				int count = 0;
				for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
					count++;
					s += (ench.getKey().getMaxLevel() == ench.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN)
							+ ench.getKey().getDisplayName(ench.getValue()).getString() + TextFormatting.RESET
							+ (count < enchs.size() || stack.isDamageable() ? " / " : "");
				}
			} else if (stack.getItem() == Items.POTION || stack.getItem() == Items.LINGERING_POTION
					|| stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.SPECTRAL_ARROW
					|| stack.getItem() == Items.TIPPED_ARROW) {
				List<ITextComponent> texts = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
				texts.remove(0);
				int count = 0;
				for (ITextComponent text : texts) {
					s += text.getFormattedText();
					if (count == 1) {
						s += " / ";
					} else if (count == 2) {
						s += " ";
					}
					count++;
				}
			}
		}
		if (stack.isDamageable())

		{
			s += "耐久 : " + (stack.getMaxDamage() - stack.getDamage()) + "/" + stack.getMaxDamage();
		}
		return s;
	}
}
