package net.tympanic.loots;

import net.fabricmc.api.ModInitializer;
import net.tympanic.loots.block.LootPile;
import net.tympanic.loots.block.ModBlocks;
import net.tympanic.loots.util.TickHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String ModId = "loots";
	public static final Logger LOGGER = LoggerFactory.getLogger(ModId);


	@Override
	public void onInitialize() {
		LOGGER.info("Initializing simple loot piles");
		TickHandler.init();
		ModBlocks.registerModBlocks();
		LootPile.registerTiers();
	}
}
