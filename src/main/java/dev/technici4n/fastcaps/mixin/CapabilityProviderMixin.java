package dev.technici4n.fastcaps.mixin;

import dev.technici4n.fastcaps.CapabilityProviderExt;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(CapabilityProvider.class)
public abstract class CapabilityProviderMixin implements CapabilityProviderExt, CapabilityProviderAccessor {

	private boolean fastcaps_delayComparisonDispatch = false;

	@Override
	public void fastcaps_setDelayComparisonDispatch() {
		fastcaps_delayComparisonDispatch = true;
	}

	@Override
	public boolean fastcaps_comparisonsSkipDispatch() {
		return fastcaps_delayComparisonDispatch;
	}

	@Inject(
			method = "areCapsCompatible(Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	protected void fastcaps_injectAreCapsCompatible(CapabilityProvider<?> other_, CallbackInfoReturnable<Boolean> cir) {
		CapabilityProviderAccessor other = (CapabilityProviderAccessor) other_;
		boolean canThis = canCompareLazyData(this);
		boolean canOther = canCompareLazyData(other);
		if (canThis && canOther) {
			cir.setReturnValue(Objects.equals(this.getLazyData(), other.getLazyData()));
		} else if (canThis) {
			cir.setReturnValue(Objects.equals(this.getLazyData(), other.callSerializeCaps()));
		} else if (canOther) {
			cir.setReturnValue(Objects.equals(other.getLazyData(), this.callSerializeCaps()));
		}
	}

	@Unique
	private static boolean canCompareLazyData(CapabilityProviderAccessor provider) {
		return provider.getIsLazy() && !provider.getInitialized() && ((CapabilityProviderExt) provider).fastcaps_comparisonsSkipDispatch();
	}
}
