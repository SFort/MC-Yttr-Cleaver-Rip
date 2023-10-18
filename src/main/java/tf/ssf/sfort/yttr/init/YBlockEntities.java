package tf.ssf.sfort.yttr.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.registry.Registries;
import tf.ssf.sfort.yttr.Yttr;
import tf.ssf.sfort.yttr.content.block.decor.CleavedBlockEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class YBlockEntities {

	public static final BlockEntityType<CleavedBlockEntity> CLEAVED_BLOCK = create(CleavedBlockEntity::new, YBlocks.CLEAVED_BLOCK);

	private static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityFactory<T> cons, Block... acceptableBlocks) {
		return new BlockEntityType<>(cons, ImmutableSet.copyOf(acceptableBlocks), null);
	}

	public static void init() {
		Yttr.autoreg.autoRegister(Registries.BLOCK_ENTITY_TYPE, YBlockEntities.class, BlockEntityType.class);
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Renderer {
		String value();
	}
	
}
