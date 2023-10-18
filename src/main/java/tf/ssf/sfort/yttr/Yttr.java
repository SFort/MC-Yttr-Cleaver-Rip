package tf.ssf.sfort.yttr;

import com.unascribed.lib39.core.api.AutoRegistry;
import com.unascribed.lib39.dessicant.api.DessicantControl;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import tf.ssf.sfort.yttr.init.YBlockEntities;
import tf.ssf.sfort.yttr.init.YBlocks;
import tf.ssf.sfort.yttr.init.YCriteria;
import tf.ssf.sfort.yttr.init.YItems;
import tf.ssf.sfort.yttr.init.YSounds;
import tf.ssf.sfort.yttr.init.YTags;

public class Yttr implements ModInitializer {
	

	public static final AutoRegistry autoreg = AutoRegistry.of("yttr");

	@Override
	public void onInitialize() {
		YBlocks.init();
		YBlockEntities.init();
		YItems.init();
		YSounds.init();

		YTags.init();

		YCriteria.init();


		DessicantControl.optIn("yttr");

	}
	
	public static Identifier id(String path) {
		return new Identifier("yttr", path);
	}

	public static void sync(BlockEntity be) {
		if (!be.hasWorld()) return;
		if (be.getWorld().isClient) return;
		be.getWorld().updateListeners(be.getPos(), Blocks.AIR.getDefaultState(), be.getCachedState(), 3);
	}
	
}
