package com.flier268.more_tooltips;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TooltipEventHandler {
    private static Map<Item, Integer> FuelTimeMap = null;

    private static List<Text> splitToolTip(TextRenderer renderer, String text, int maxWidth) {
        return splitToolTip(renderer, text, maxWidth, null);
    }

    private static List<Text> splitToolTip(TextRenderer renderer, String text, int maxWidth, Style style) {
        List<Text> output = new ArrayList<Text>();
        int width = renderer.getWidth(text);
        if (width > maxWidth) {
            int skipEnd = 0;
            int added = 0;
            while (true) {
                int lastSpaceIndex = text.lastIndexOf(" ", text.length() - skipEnd - 1);
                if (added <= lastSpaceIndex) {
                    String textPart = text.substring(added, lastSpaceIndex);
                    int textPartWidth = renderer.getWidth(textPart);
                    if (textPartWidth <= maxWidth || textPart.indexOf(" ") == -1) {
                        output.add(TrySetStyle(new LiteralText(textPart), style));
                        added += textPart.length() + 1;
                        skipEnd = 0;
                    } else {
                        skipEnd = text.length() - lastSpaceIndex;
                    }
                } else {
                    output.add(TrySetStyle(new LiteralText(text.substring(added, text.length())), style));
                    break;
                }
            }
        } else {
            output.add(TrySetStyle(new LiteralText(text), style));
        }
        return output;
    }

    private static Text TrySetStyle(BaseText text, Style style) {
        if (style != null)
            return text.setStyle(style);
        return text;
    }

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

            var clientInstance = MinecraftClient.getInstance();
            int threshold = clientInstance.getWindow().getScaledWidth() / 2;

            // Tooltip - Burn Time
            if (config.BurnTime.isShown(isShiftDown, config.debug)) {
                if (ItemTags.getTagGroup().getTags().size() > 0) {
                    if (TooltipEventHandler.FuelTimeMap == null)
                        TooltipEventHandler.FuelTimeMap = AbstractFurnaceBlockEntity.createFuelTimeMap();
                    int burnTime = TooltipEventHandler.FuelTimeMap.getOrDefault(item, 0);
                    if (burnTime > 0) {
                        String string = new TranslatableText("tooltip.more_tooltips.burnTime", burnTime).getString();
                        list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                    }
                }
            }

            // Tooltip - MiningLevel
            if (config.MiningLevel.isShown(isShiftDown, config.debug)) {
                if (item instanceof ToolItem) {
                    int miningLevel = ((ToolItem) item).getMaterial().getMiningLevel();
                    String string = new TranslatableText("tooltip.more_tooltips.MiningLevel", miningLevel).getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold));
                }
            }

            // Tooltip - Durability
            if (config.Durability.isShown(isShiftDown, config.debug)) {
                int maxDamage = itemStack.getMaxDamage();
                int currentDamage = maxDamage - itemStack.getDamage();
                if (maxDamage > 0) {
                    String string = new TranslatableText("tooltip.more_tooltips.durability", currentDamage, maxDamage)
                            .getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold));
                }
            }

            // Tooltip - Hunger / Saturation
            if (config.Food.isShown(isShiftDown, config.debug)) {
                if (itemStack.isFood()) {
                    FoodComponent foodComponent = item.getFoodComponent();
                    int healVal = foodComponent.getHunger();
                    float satVal = healVal * (foodComponent.getSaturationModifier()) * 2;
                    String string = new TranslatableText("tooltip.more_tooltips.hunger", healVal,
                            new DecimalFormat("###.#").format(satVal)).getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }

            // Tooltip - NBT Data
            if (config.NBT.isShown(isShiftDown, config.debug)) {
                NbtCompound nbtData = itemStack.getNbt();
                if (nbtData != null) {
                    String string = new TranslatableText("tooltip.more_tooltips.nbtTagData", nbtData.asString())
                            .getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }

            // Tooltip - Registry Name
            if (config.ID.isShown(isShiftDown, config.debug)) {
                String string = new TranslatableText("tooltip.more_tooltips.registryName",
                        Registry.ITEM.getId(item).toString()).getString();
                list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
            }

            // Tooltip - Max Stack Size
            if (config.MaxStackSize.isShown(isShiftDown, config.debug)) {
                if (itemStack.isStackable()) {
                    String string = new TranslatableText("tooltip.more_tooltips.maxStackSize", itemStack.getMaxCount())
                            .getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }

            // Tooltip - Translation Key
            if (config.TranslationKey.isShown(isShiftDown, config.debug)) {
                String string = new TranslatableText("tooltip.more_tooltips.translationKey",
                        itemStack.getTranslationKey()).getString();
                list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
            }

            // Tooltip - Repair Cost
            if (config.RepairCost.isShown(isShiftDown, config.debug)) {
                if (itemStack.isDamageable()) {
                    String string = new TranslatableText("tooltip.more_tooltips.RepairCost", itemStack.getRepairCost())
                            .getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }
            // Tooltip - Enchantability
            if (config.Enchantability.isShown(isShiftDown, config.debug)) {
                if (itemStack.isEnchantable()) {
                    String string = new TranslatableText("tooltip.more_tooltips.Enchantability",
                            item.getEnchantability()).getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }

            // Tooltip - Light level
            if (config.LightLevel.isShown(isShiftDown, config.debug)) {
                int luminance = Registry.BLOCK.get(itemId).getDefaultState().getLuminance();
                if (luminance > 0) {
                    String string = new TranslatableText("tooltip.more_tooltips.LightLevel", luminance).getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }

            // Tooltip - Composting chance
            if (config.CompostingChance.isShown(isShiftDown, config.debug)) {
                float chance = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item);

                if (chance > 0.0) {
                    String string = new TranslatableText("tooltip.more_tooltips.CompostingChance",
                            new DecimalFormat("###.#").format(chance * 100)).getString();
                    list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, DARK_GRAY));
                }
            }

            if (isShiftDown && config.debug) {
                String string = new LiteralText("Powered by flier268").getString();
                list.addAll(splitToolTip(clientInstance.textRenderer, string, threshold, AQUA));
            }
        });
    }
}
