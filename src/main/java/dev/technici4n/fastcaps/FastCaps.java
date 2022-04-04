package dev.technici4n.fastcaps;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FastCaps.MODID)
public final class FastCaps {
	public static final String MODID = "fastcaps";

	public static final Logger LOGGER = LogManager.getLogger();

	// Used by testmod
	@CapabilityInject(IItemHandler.class)
	public static Capability<IItemHandler> CAP;

	public FastCaps() {
		if ("true".equals(System.getProperty("fastcaps_test"))) {
			FastCapsTesting.registerTesting();
		}
	}
}
