package org.fuzhou.fragmentsofsound.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Fragmentsofsound.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModItems.WEAPONS)
            .addTags(net.minecraft.tags.ItemTags.SWORDS)
            .addTags(net.minecraft.tags.ItemTags.AXES)
            .addTags(net.minecraft.tags.ItemTags.PICKAXES)
            .addTags(net.minecraft.tags.ItemTags.SHOVELS)
            .addTags(net.minecraft.tags.ItemTags.HOES)
            .add(Items.BOW)
            .add(Items.CROSSBOW)
            .add(Items.TRIDENT)
            .add(Items.SHIELD);

        tag(ModItems.TOOLS)
            .addTags(net.minecraft.tags.ItemTags.PICKAXES)
            .addTags(net.minecraft.tags.ItemTags.AXES)
            .addTags(net.minecraft.tags.ItemTags.SHOVELS)
            .addTags(net.minecraft.tags.ItemTags.HOES);
    }
}
