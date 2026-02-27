package org.fuzhou.fragmentsofsound.entity;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

public class ModEntities {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Fragmentsofsound.MODID);
    
    public static final RegistryObject<EntityType<PortalEntity>> PORTAL = ENTITIES.register("portal",
        () -> EntityType.Builder.<PortalEntity>of(PortalEntity::new, MobCategory.MISC)
            .sized(2.0f, 4.0f)
            .clientTrackingRange(256)
            .updateInterval(1)
            .build(Fragmentsofsound.MODID + ":portal"));
    
    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
