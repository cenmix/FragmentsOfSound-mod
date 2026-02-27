package org.fuzhou.fragmentsofsound.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.block.MonsterOutpostBlock;

public class BlockEntityTypes {
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Fragmentsofsound.MODID);
    
    public static final RegistryObject<BlockEntityType<MonsterOutpostBlockEntity>> MONSTER_OUTPOST = 
        BLOCK_ENTITIES.register("monster_outpost", () -> 
            BlockEntityType.Builder.of(MonsterOutpostBlockEntity::new, 
                Fragmentsofsound.MONSTER_OUTPOST_1.get(),
                Fragmentsofsound.MONSTER_OUTPOST_2.get(),
                Fragmentsofsound.MONSTER_OUTPOST_3.get(),
                Fragmentsofsound.MONSTER_OUTPOST_4.get(),
                Fragmentsofsound.MONSTER_OUTPOST_5.get(),
                Fragmentsofsound.MONSTER_OUTPOST_6.get(),
                Fragmentsofsound.MONSTER_OUTPOST_7.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChiselStoneForgingTableBlockEntity>> CHISEL_STONE_FORGING_TABLE =
        BLOCK_ENTITIES.register("chisel_stone_forging_table", () ->
            BlockEntityType.Builder.of(ChiselStoneForgingTableBlockEntity::new,
                Fragmentsofsound.CHISEL_STONE_FORGING_TABLE.get()).build(null));
}
