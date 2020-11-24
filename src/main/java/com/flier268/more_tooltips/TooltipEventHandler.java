package com.flier268.more_tooltips;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.text.DecimalFormat;

public class TooltipEventHandler {
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
            // Tooltip - Burn Time
            if (config.BurnTime.isShown(isShiftDown, config.debug)) {
                if (ItemTags.getTagGroup().getTags().size() > 0) {
                    int burnTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(itemStack.getItem(), 0);
                    if (burnTime > 0) {
                        list.add(new TranslatableText("tooltip.more_tooltips.burnTime")
                                .append(new LiteralText(" " + decimalFormat.format(burnTime) + " "))
                                .append(new TranslatableText("tooltip.more_tooltips.burnTime.suffix"))
                                .fillStyle(DARK_GRAY));
                    }
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
                    FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
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
                CompoundTag nbtData = itemStack.getTag();
                if (nbtData != null) {
                    list.add(new TranslatableText("tooltip.more_tooltips.nbtTagData")
                            .append(new LiteralText(" " + nbtData))
                            .fillStyle(DARK_GRAY));
                }
            }

            // Tooltip - Registry Name
            if (config.ID.isShown(isShiftDown, config.debug)) {
                list.add(new TranslatableText("tooltip.more_tooltips.registryName")
                        .append(new LiteralText(" " + Registry.ITEM.getId(itemStack.getItem()).toString()))
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
                            .append(new LiteralText(" " + itemStack.getItem().getEnchantability()))
                            .fillStyle(DARK_GRAY));
                }
            }
            if(isShiftDown && config.debug)
                list.add(new LiteralText("Powered by flier268").fillStyle(AQUA));
        });
    }
}
