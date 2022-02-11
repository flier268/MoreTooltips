package com.flier268.more_tooltips;

import java.text.DecimalFormat;
import java.util.Map;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TooltipEventHandler {
    private static Map<Item, Integer> FuelTimeMap = null;

    public static void addMoreTooltip() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (!config.isEnable)
                return;

            boolean isShiftDown = Screen.hasShiftDown();

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setGroupingSize(3);
            Style DARK_GRAY = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.DARK_GRAY));
            Style AQUA = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.AQUA));
            // Retrieve the ItemStack and Item

            // If item stack empty do nothing
            if (itemStack.isEmpty()) {
                return;
            }
            Item item = itemStack.getItem();
            Identifier itemId = Registry.ITEM.getKey(item).get().getValue();

            // Tooltip - Burn Time
            if (config.BurnTime.isShown(isShiftDown, config.debug)) {
                if (ItemTags.getTagGroup().getTags().size() > 0) {
                    if (TooltipEventHandler.FuelTimeMap == null)
                        TooltipEventHandler.FuelTimeMap = AbstractFurnaceBlockEntity.createFuelTimeMap();
                    int burnTime = TooltipEventHandler.FuelTimeMap.getOrDefault(item, 0);
                    if (burnTime > 0) {
                        list.add(new TranslatableText("tooltip.more_tooltips.burnTime")
                                .append(new LiteralText(" " + decimalFormat.format(burnTime) + " "))
                                .append(new TranslatableText("tooltip.more_tooltips.burnTime.suffix"))
                                .fillStyle(DARK_GRAY));
                    }
                }
            }

            // Tooltip - MiningLevel
            if (config.MiningLevel.isShown(isShiftDown, config.debug)) {
                if (item instanceof ToolItem) {
                    int miningLevel = ((ToolItem) item).getMaterial().getMiningLevel();
                    list.add(1, new TranslatableText("tooltip.more_tooltips.MiningLevel")
                            .append(new LiteralText(" " + miningLevel)));
                }
            }

            // Tooltip - Durability
            if (config.Durability.isShown(isShiftDown, config.debug)) {
                int maxDamage = itemStack.getMaxDamage();
                int currentDamage = maxDamage - itemStack.getDamage();
                if (maxDamage > 0) {
                    list.add(1, new TranslatableText("tooltip.more_tooltips.durability")
                            .append(new LiteralText(" " + currentDamage + "/" + maxDamage)));
                }
            }

            // Tooltip - Hunger / Saturation
            if (config.Food.isShown(isShiftDown, config.debug)) {
                if (itemStack.isFood()) {
                    FoodComponent foodComponent = item.getFoodComponent();
                    int healVal = foodComponent.getHunger();
                    float satVal = healVal * (foodComponent.getSaturationModifier()) * 2;
                    list.add(new TranslatableText("tooltip.more_tooltips.hunger")
                            .append(new LiteralText(" " + healVal + " "))
                            .append(new TranslatableText("tooltip.more_tooltips.saturation"))
                            .append(new LiteralText(" " + decimalFormat.format(satVal)))
                            .fillStyle(DARK_GRAY));

                }
            }

            // Tooltip - NBT Data
            if (config.NBT.isShown(isShiftDown, config.debug)) {
                NbtCompound nbtData = itemStack.getNbt();
                if (nbtData != null) {
                    list.add(new TranslatableText("tooltip.more_tooltips.nbtTagData")
                            .append(new LiteralText(" " + nbtData))
                            .fillStyle(DARK_GRAY));
                }
            }

            // Tooltip - Registry Name
            if (config.ID.isShown(isShiftDown, config.debug)) {
                list.add(new TranslatableText("tooltip.more_tooltips.registryName")
                        .append(new LiteralText(" " + Registry.ITEM.getId(item).toString()))
                        .fillStyle(DARK_GRAY));
            }

            // Tooltip - Max Stack Size
            if (config.MaxStackSize.isShown(isShiftDown, config.debug)) {
                if (itemStack.isStackable()) {
                    list.add(new TranslatableText("tooltip.more_tooltips.maxStackSize")
                            .append(new LiteralText(" " + itemStack.getMaxCount()))
                            .fillStyle(DARK_GRAY));
                }
            }

            // Tooltip - Translation Key
            if (config.TranslationKey.isShown(isShiftDown, config.debug)) {
                list.add(new TranslatableText("tooltip.more_tooltips.translationKey")
                        .append(new LiteralText(" " + itemStack.getTranslationKey()))
                        .fillStyle(DARK_GRAY));
            }

            // Tooltip - Repair Cost
            if (config.RepairCost.isShown(isShiftDown, config.debug)) {
                if (itemStack.isDamageable()) {
                    list.add(new TranslatableText("tooltip.more_tooltips.RepairCost")
                            .append(new LiteralText(" " + itemStack.getRepairCost()))
                            .fillStyle(DARK_GRAY));
                }
            }
            // Tooltip - Enchantability
            if (config.Enchantability.isShown(isShiftDown, config.debug)) {
                if (itemStack.isEnchantable()) {
                    list.add(new TranslatableText("tooltip.more_tooltips.Enchantability")
                            .append(new LiteralText(" " + item.getEnchantability()))
                            .fillStyle(DARK_GRAY));
                }
            }

            // Tooltip - Light level
            if (config.LightLevel.isShown(isShiftDown, config.debug)) {
                int luminance = Registry.BLOCK.get(itemId).getDefaultState().getLuminance();
                if (luminance > 0) {
                    list.add(new TranslatableText("tooltip.more_tooltips.LightLevel")
                            .append(new LiteralText(" " + luminance))
                            .fillStyle(DARK_GRAY));
                }
            }

            // Tooltip - Composting chance
            if (config.CompostingChance.isShown(isShiftDown, config.debug)) {
                float chance = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item);
                if (chance > 0.0) {
                    list.add(new TranslatableText("tooltip.more_tooltips.CompostingChance")
                            .append(new LiteralText(" " + String.format("%.0f%%", chance * 100)))
                            .fillStyle(DARK_GRAY));
                }
            }

            if (isShiftDown && config.debug)
                list.add(new LiteralText("Powered by flier268").fillStyle(AQUA));
        });
    }
}
