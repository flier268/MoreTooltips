package com.flier268.more_tooltips;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = MoreTooltips.MOD_ID)
public class ModConfig implements ConfigData {

    public boolean isEnable = true;
    public boolean debug = false;

    public Options BurnTime = Options.show;
    public Options Durability = Options.show;
    public Options Food = Options.show;
    public Options NBT = Options.onShiftAndDebug;
    public Options ID = Options.onShift;
    public Options MaxStackSize = Options.show;
    public Options TranslationKey = Options.onDebug;
    public Options RepairCost = Options.onShift;
    public Options Enchantability = Options.onShift;
    public Options MiningLevel = Options.show;

    public enum Options {
        hide,
        show,
        onDebug,
        onShift,
        onShiftAndDebug;

        public boolean isShown(boolean isShiftKeyDown, boolean isDebug) {
            switch (this) {
                case show:
                    return true;
                case onDebug:
                    return isDebug;
                case onShift:
                    return isShiftKeyDown;
                case onShiftAndDebug:
                    return isDebug && isShiftKeyDown;
                case hide:
                default:
                    return false;
            }
        }
    }
}
