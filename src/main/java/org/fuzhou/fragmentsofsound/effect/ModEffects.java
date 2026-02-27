package org.fuzhou.fragmentsofsound.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Fragmentsofsound.MODID);
    
    public static final RegistryObject<MobEffect> CORROSION = MOB_EFFECTS.register("corrosion", CorrosionEffect::new);
}
