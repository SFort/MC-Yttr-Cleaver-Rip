package tf.ssf.sfort.yttr.init;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import tf.ssf.sfort.yttr.Yttr;
import tf.ssf.sfort.yttr.content.block.decor.CleavedBlock;

public class YBlocks {

	public static final CleavedBlock CLEAVED_BLOCK = new CleavedBlock(FabricBlockSettings.create().dynamicBounds().nonOpaque());
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.BLOCK, YBlocks.class, Block.class);
	}

}
