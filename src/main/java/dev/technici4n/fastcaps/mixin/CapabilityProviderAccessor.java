package dev.technici4n.fastcaps.mixin;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(CapabilityProvider.class)
public interface CapabilityProviderAccessor {
	@Accessor
	boolean getIsLazy();
	@Accessor
	boolean getInitialized();
	@Accessor
	@Nullable
	CompoundNBT getLazyData();

	@Invoker
	@Nullable CompoundNBT callSerializeCaps();
}
