package dev.technici4n.fastcaps.mixin;

import dev.technici4n.fastcaps.CapabilityProviderExt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(
			method = "<init>(Lnet/minecraft/util/IItemProvider;ILnet/minecraft/nbt/CompoundNBT;)V",
			at = @At("TAIL")
	)
	public void fastcaps_onConstruct(IItemProvider provider, int count, CompoundNBT nbt, CallbackInfo ci) {
		((CapabilityProviderExt) this).fastcaps_setDelayComparisonDispatch();
	}
}
