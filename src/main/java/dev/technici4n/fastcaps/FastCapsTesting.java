package dev.technici4n.fastcaps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dev.technici4n.fastcaps.FastCaps.CAP;
import static dev.technici4n.fastcaps.FastCaps.MODID;

public class FastCapsTesting {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	private static final RegistryObject<Item> CAP_ITEM = ITEMS.register("cap_item", CapItem::new);
	private static int attachCapabilitiesCount;

	static void registerTesting() {
		FastCaps.LOGGER.info("FastCaps testing enabled - if you didn't enable this, it's probably a bug!");
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(modBus);
		MinecraftForge.EVENT_BUS.addListener(FastCapsTesting::onServerStarted);
		MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, FastCapsTesting::onItemStackAttachCaps);
	}

	private static void onServerStarted(FMLServerStartedEvent event) {
		testItemStackCapabilities();
	}

	private static void onItemStackAttachCaps(AttachCapabilitiesEvent<ItemStack> event) {
		attachCapabilitiesCount++;
	}

	private static void testItemStackCapabilities() {
		attachCapabilitiesCount = 0;

		ItemStack stackOne = new ItemStack(CAP_ITEM.get());
		// get capability dispatches event
		stackOne.getCapability(CAP).ifPresent(handler -> handler.insertItem(0, new ItemStack(Items.DIAMOND), false));
		assertEquals(1, attachCapabilitiesCount);
		// copy doesn't dispatch an event
		ItemStack stackTwo = stackOne.copy();
		// get capability does dispatch an event
		stackTwo.getCapability(CAP).ifPresent(handler -> handler.insertItem(0, new ItemStack(Items.DIAMOND), false));
		assertEquals(2, attachCapabilitiesCount);

		// copies should handle comparisons just fine, without dispatching an attach capabilities event
		ItemStack stackOneCopy = stackOne.copy();
		ItemStack stackTwoCopy = stackTwo.copy();

		// check that copies preserve equality
		assertCompatible(stackOne, stackOneCopy);
		assertCompatible(stackTwo, stackTwoCopy);
		assertNotCompatible(stackOne, stackTwo);
		assertNotCompatible(stackOne, stackTwoCopy);
		assertNotCompatible(stackOneCopy, stackTwo);
		assertNotCompatible(stackOneCopy, stackTwoCopy);

		// only two dispatches should have happened (for the first two stacks)
		assertEquals(2, attachCapabilitiesCount);

		FastCaps.LOGGER.info("FastCaps test ok!");
	}

	private static void assertEquals(int expected, int actual) {
		if (expected != actual)
			throw new AssertionError("Expected integer " + actual + " to be equal to " + expected + " , but it's not.");
	}

	private static void assertCompatible(ItemStack a, ItemStack b) {
		if (!a.areCapsCompatible(b)) throw new AssertionError("Expected capabilities to be compatible.");
	}

	private static void assertNotCompatible(ItemStack a, ItemStack b) {
		if (a.equals(b)) throw new AssertionError("Expected capabilities NOT to be compatible.");
	}

	private static class CapItem extends Item {
		private CapItem() {
			super(new Properties());
		}

		@Nullable
		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
			return new TestCapProvider();
		}
	}

	private static class TestCapProvider implements ICapabilityProvider, INBTSerializable<INBT> {
		private final IItemHandler handler = CAP.getDefaultInstance();

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			if (cap == CAP) return LazyOptional.of(() -> handler).cast();
			else return LazyOptional.empty();
		}

		@Override
		public INBT serializeNBT() {
			return CAP.getStorage().writeNBT(CAP, handler, null);
		}

		@Override
		public void deserializeNBT(INBT nbt) {
			CAP.getStorage().readNBT(CAP, handler, null, nbt);
		}
	}
}
