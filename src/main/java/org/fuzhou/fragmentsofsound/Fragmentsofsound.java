package org.fuzhou.fragmentsofsound;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.fuzhou.fragmentsofsound.block.MonsterOutpostBlock;
import org.fuzhou.fragmentsofsound.block.ChiselStoneForgingTableBlock;
import org.fuzhou.fragmentsofsound.block.entity.BlockEntityTypes;
import org.fuzhou.fragmentsofsound.block.ChiselStoneOreBlock;
import org.fuzhou.fragmentsofsound.block.CorruptBlock;
import org.fuzhou.fragmentsofsound.effect.ModEffects;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;
import org.fuzhou.fragmentsofsound.item.PhysicalRuneItem;
import org.fuzhou.fragmentsofsound.item.ArmorBreakRuneItem;
import org.fuzhou.fragmentsofsound.item.PierceRuneItem;
import org.fuzhou.fragmentsofsound.menu.ModMenuTypes;
import org.fuzhou.fragmentsofsound.client.screen.ChiselStoneForgingTableScreen;
import org.fuzhou.fragmentsofsound.client.renderer.ChiselStoneForgingTableRenderer;
import org.slf4j.Logger;

@Mod(Fragmentsofsound.MODID)
public class Fragmentsofsound {

    public static final String MODID = "fragmentsofsound";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_1 = BLOCKS.register("monster_outpost_1", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f).requiresCorrectToolForDrops(), 1));
    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_2 = BLOCKS.register("monster_outpost_2", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.5f).requiresCorrectToolForDrops(), 2));
    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_3 = BLOCKS.register("monster_outpost_3", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4.0f).requiresCorrectToolForDrops(), 3));
    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_4 = BLOCKS.register("monster_outpost_4", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4.5f).requiresCorrectToolForDrops(), 4));
    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_5 = BLOCKS.register("monster_outpost_5", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0f).requiresCorrectToolForDrops(), 5));
    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_6 = BLOCKS.register("monster_outpost_6", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.5f).requiresCorrectToolForDrops(), 6));
    public static final RegistryObject<MonsterOutpostBlock> MONSTER_OUTPOST_7 = BLOCKS.register("monster_outpost_7", () -> new MonsterOutpostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(6.0f).requiresCorrectToolForDrops(), 7));

    public static final RegistryObject<CorruptBlock> CORRUPT_BLOCK = BLOCKS.register("corrupt_block", CorruptBlock::new);

    public static final RegistryObject<ChiselStoneForgingTableBlock> CHISEL_STONE_FORGING_TABLE = BLOCKS.register("chisel_stone_forging_table", () -> new ChiselStoneForgingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4.0f).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_1 = BLOCKS.register("chisel_stone_ore_1", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f).requiresCorrectToolForDrops(), 1));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_2 = BLOCKS.register("chisel_stone_ore_2", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.5f).requiresCorrectToolForDrops(), 2));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_3 = BLOCKS.register("chisel_stone_ore_3", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4.0f).requiresCorrectToolForDrops(), 3));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_4 = BLOCKS.register("chisel_stone_ore_4", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4.5f).requiresCorrectToolForDrops(), 4));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_5 = BLOCKS.register("chisel_stone_ore_5", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0f).requiresCorrectToolForDrops(), 5));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_6 = BLOCKS.register("chisel_stone_ore_6", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.5f).requiresCorrectToolForDrops(), 6));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_7 = BLOCKS.register("chisel_stone_ore_7", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(6.0f).requiresCorrectToolForDrops(), 7));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_8 = BLOCKS.register("chisel_stone_ore_8", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(6.5f).requiresCorrectToolForDrops(), 8));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_9 = BLOCKS.register("chisel_stone_ore_9", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(7.0f).requiresCorrectToolForDrops(), 9));
    public static final RegistryObject<ChiselStoneOreBlock> CHISEL_STONE_ORE_10 = BLOCKS.register("chisel_stone_ore_10", () -> new ChiselStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(7.5f).requiresCorrectToolForDrops(), 10));

    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_1_ITEM = ITEMS.register("monster_outpost_1", () -> new BlockItem(MONSTER_OUTPOST_1.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_2_ITEM = ITEMS.register("monster_outpost_2", () -> new BlockItem(MONSTER_OUTPOST_2.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_3_ITEM = ITEMS.register("monster_outpost_3", () -> new BlockItem(MONSTER_OUTPOST_3.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_4_ITEM = ITEMS.register("monster_outpost_4", () -> new BlockItem(MONSTER_OUTPOST_4.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_5_ITEM = ITEMS.register("monster_outpost_5", () -> new BlockItem(MONSTER_OUTPOST_5.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_6_ITEM = ITEMS.register("monster_outpost_6", () -> new BlockItem(MONSTER_OUTPOST_6.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> MONSTER_OUTPOST_7_ITEM = ITEMS.register("monster_outpost_7", () -> new BlockItem(MONSTER_OUTPOST_7.get(), new net.minecraft.world.item.Item.Properties()));

    public static final RegistryObject<net.minecraft.world.item.Item> CORRUPT_BLOCK_ITEM = ITEMS.register("corrupt_block", () -> new BlockItem(CORRUPT_BLOCK.get(), new net.minecraft.world.item.Item.Properties()));

    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_FORGING_TABLE_ITEM = ITEMS.register("chisel_stone_forging_table", () -> new BlockItem(CHISEL_STONE_FORGING_TABLE.get(), new net.minecraft.world.item.Item.Properties()));

    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_1_ITEM = ITEMS.register("chisel_stone_ore_1", () -> new BlockItem(CHISEL_STONE_ORE_1.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_2_ITEM = ITEMS.register("chisel_stone_ore_2", () -> new BlockItem(CHISEL_STONE_ORE_2.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_3_ITEM = ITEMS.register("chisel_stone_ore_3", () -> new BlockItem(CHISEL_STONE_ORE_3.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_4_ITEM = ITEMS.register("chisel_stone_ore_4", () -> new BlockItem(CHISEL_STONE_ORE_4.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_5_ITEM = ITEMS.register("chisel_stone_ore_5", () -> new BlockItem(CHISEL_STONE_ORE_5.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_6_ITEM = ITEMS.register("chisel_stone_ore_6", () -> new BlockItem(CHISEL_STONE_ORE_6.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_7_ITEM = ITEMS.register("chisel_stone_ore_7", () -> new BlockItem(CHISEL_STONE_ORE_7.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_8_ITEM = ITEMS.register("chisel_stone_ore_8", () -> new BlockItem(CHISEL_STONE_ORE_8.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_9_ITEM = ITEMS.register("chisel_stone_ore_9", () -> new BlockItem(CHISEL_STONE_ORE_9.get(), new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_ORE_10_ITEM = ITEMS.register("chisel_stone_ore_10", () -> new BlockItem(CHISEL_STONE_ORE_10.get(), new net.minecraft.world.item.Item.Properties()));

    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_1 = ITEMS.register("chisel_stone_1", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 1));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_2 = ITEMS.register("chisel_stone_2", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 2));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_3 = ITEMS.register("chisel_stone_3", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 3));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_4 = ITEMS.register("chisel_stone_4", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 4));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_5 = ITEMS.register("chisel_stone_5", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 5));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_6 = ITEMS.register("chisel_stone_6", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 6));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_7 = ITEMS.register("chisel_stone_7", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 7));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_8 = ITEMS.register("chisel_stone_8", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 8));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_9 = ITEMS.register("chisel_stone_9", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 9));
    public static final RegistryObject<net.minecraft.world.item.Item> CHISEL_STONE_10 = ITEMS.register("chisel_stone_10", () -> new ChiselStoneItem(new net.minecraft.world.item.Item.Properties(), 10));

    public static final RegistryObject<net.minecraft.world.item.Item> PHYSICAL_RUNE = ITEMS.register("physical_rune", () -> new PhysicalRuneItem(new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> ARMOR_BREAK_RUNE = ITEMS.register("armor_break_rune", () -> new ArmorBreakRuneItem(new net.minecraft.world.item.Item.Properties()));
    public static final RegistryObject<net.minecraft.world.item.Item> PIERCE_RUNE = ITEMS.register("pierce_rune", () -> new PierceRuneItem(new net.minecraft.world.item.Item.Properties()));

    public static final RegistryObject<CreativeModeTab> FRAGMENTS_TAB = CREATIVE_MODE_TABS.register("fragments_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.fragmentsofsound.fragments"))
        .icon(() -> MONSTER_OUTPOST_1_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(CHISEL_STONE_ORE_1_ITEM.get());
            output.accept(CHISEL_STONE_ORE_2_ITEM.get());
            output.accept(CHISEL_STONE_ORE_3_ITEM.get());
            output.accept(CHISEL_STONE_ORE_4_ITEM.get());
            output.accept(CHISEL_STONE_ORE_5_ITEM.get());
            output.accept(CHISEL_STONE_ORE_6_ITEM.get());
            output.accept(CHISEL_STONE_ORE_7_ITEM.get());
            output.accept(CHISEL_STONE_ORE_8_ITEM.get());
            output.accept(CHISEL_STONE_ORE_9_ITEM.get());
            output.accept(CHISEL_STONE_ORE_10_ITEM.get());
            output.accept(CHISEL_STONE_1.get());
            output.accept(CHISEL_STONE_2.get());
            output.accept(CHISEL_STONE_3.get());
            output.accept(CHISEL_STONE_4.get());
            output.accept(CHISEL_STONE_5.get());
            output.accept(CHISEL_STONE_6.get());
            output.accept(CHISEL_STONE_7.get());
            output.accept(CHISEL_STONE_8.get());
            output.accept(CHISEL_STONE_9.get());
            output.accept(CHISEL_STONE_10.get());
            output.accept(MONSTER_OUTPOST_1_ITEM.get());
            output.accept(MONSTER_OUTPOST_2_ITEM.get());
            output.accept(MONSTER_OUTPOST_3_ITEM.get());
            output.accept(MONSTER_OUTPOST_4_ITEM.get());
            output.accept(MONSTER_OUTPOST_5_ITEM.get());
            output.accept(MONSTER_OUTPOST_6_ITEM.get());
            output.accept(MONSTER_OUTPOST_7_ITEM.get());
            output.accept(CORRUPT_BLOCK_ITEM.get());
            output.accept(CHISEL_STONE_FORGING_TABLE_ITEM.get());
            output.accept(PHYSICAL_RUNE.get());
            output.accept(ARMOR_BREAK_RUNE.get());
            output.accept(PIERCE_RUNE.get());
        })
        .build());

    public Fragmentsofsound() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BlockEntityTypes.BLOCK_ENTITIES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        org.fuzhou.fragmentsofsound.entity.ModEntities.ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            org.fuzhou.fragmentsofsound.network.NetworkHandler.register();
        });
        LOGGER.info("破碎余声模组已加载");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("服务器启动中...");
    }
    
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        org.fuzhou.fragmentsofsound.command.CinematicCommand.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("客户端设置完成");
            LOGGER.info("玩家名称 >> {}", Minecraft.getInstance().getUser().getName());
            
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.CHISEL_STONE_FORGING_TABLE_MENU.get(), ChiselStoneForgingTableScreen::new);
                ItemBlockRenderTypes.setRenderLayer(CHISEL_STONE_FORGING_TABLE.get(), RenderType.cutout());
            });
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BlockEntityTypes.CHISEL_STONE_FORGING_TABLE.get(), 
                ChiselStoneForgingTableRenderer::new);
            event.registerEntityRenderer(org.fuzhou.fragmentsofsound.entity.ModEntities.PORTAL.get(), 
                org.fuzhou.fragmentsofsound.client.renderer.PortalRenderer::new);
        }
    }
}
