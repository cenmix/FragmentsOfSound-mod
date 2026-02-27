package org.fuzhou.fragmentsofsound.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Fragmentsofsound.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
            .add(Fragmentsofsound.MONSTER_OUTPOST_1.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_2.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_3.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_4.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_5.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_6.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_7.get());

        tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
            .add(Fragmentsofsound.MONSTER_OUTPOST_1.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_2.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_3.get());

        tag(net.minecraft.tags.BlockTags.NEEDS_DIAMOND_TOOL)
            .add(Fragmentsofsound.MONSTER_OUTPOST_4.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_5.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_6.get())
            .add(Fragmentsofsound.MONSTER_OUTPOST_7.get());
    }
}
