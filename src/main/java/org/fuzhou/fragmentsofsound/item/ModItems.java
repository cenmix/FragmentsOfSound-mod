package org.fuzhou.fragmentsofsound.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final TagKey<Item> WEAPONS = ItemTags.create(ResourceLocation.tryParse("fragmentsofsound:weapons"));
    public static final TagKey<Item> TOOLS = ItemTags.create(ResourceLocation.tryParse("fragmentsofsound:tools"));
}
