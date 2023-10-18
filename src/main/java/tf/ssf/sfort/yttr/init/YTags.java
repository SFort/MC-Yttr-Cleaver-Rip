package tf.ssf.sfort.yttr.init;

import tf.ssf.sfort.yttr.Yttr;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class YTags {
	
	public static final class Block {

		public static final TagKey<net.minecraft.block.Block> UNCLEAVABLE = TagKey.of(RegistryKeys.BLOCK, Yttr.id("uncleavable"));
		public static final TagKey<net.minecraft.block.Block> CLEAVE_PASSTHRU = TagKey.of(RegistryKeys.BLOCK, Yttr.id("cleave_passthru"));
		
		private static void init() {}
		
	}

	public static void init() {
		Block.init();
	}
	
}
