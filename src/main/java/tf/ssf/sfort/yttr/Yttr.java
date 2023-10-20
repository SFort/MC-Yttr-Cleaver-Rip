package tf.ssf.sfort.yttr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import tf.ssf.sfort.yttr.init.YBlockEntities;
import tf.ssf.sfort.yttr.init.YBlocks;
import tf.ssf.sfort.yttr.init.YCriteria;
import tf.ssf.sfort.yttr.init.YItems;
import tf.ssf.sfort.yttr.init.YSounds;
import tf.ssf.sfort.yttr.init.YTags;
import tf.ssf.sfort.yttr.mixinsupport.DirectClickItem;
import tf.ssf.sfort.yttr.util.AutoRegistry;

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
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "direct_attack"), (server, player, handler, buf, sender) -> {
			server.execute(() -> {
				if (player.getMainHandStack().getItem() instanceof DirectClickItem dci) {
					if (!player.getItemCooldownManager().isCoolingDown(player.getMainHandStack().getItem())) {
						if (dci.onDirectAttack(player, Hand.MAIN_HAND).shouldSwingHand()) {
							player.swingHand(Hand.MAIN_HAND, true);
						}
					}
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "direct_use"), (server, player, handler, buf, sender) -> {
			server.execute(() -> {
				if (player.getMainHandStack().getItem() instanceof DirectClickItem dci) {
					if (!player.getItemCooldownManager().isCoolingDown(player.getMainHandStack().getItem())) {
						if (dci.onDirectUse(player, Hand.MAIN_HAND).shouldSwingHand()) {
							player.swingHand(Hand.MAIN_HAND, true);
						}
					}
				}
			});
		});
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
