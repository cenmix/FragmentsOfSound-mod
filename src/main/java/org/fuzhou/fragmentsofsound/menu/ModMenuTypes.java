package org.fuzhou.fragmentsofsound.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, Fragmentsofsound.MODID);

    public static final RegistryObject<MenuType<ChiselStoneForgingTableMenu>> CHISEL_STONE_FORGING_TABLE_MENU =
        MENUS.register("chisel_stone_forging_table_menu", () ->
            IForgeMenuType.create(ChiselStoneForgingTableMenu::new));
}
