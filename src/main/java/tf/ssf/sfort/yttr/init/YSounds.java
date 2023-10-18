package tf.ssf.sfort.yttr.init;

import tf.ssf.sfort.yttr.Yttr;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;

public class YSounds {

	public static final SoundEvent CLEAVER = create("cleaver");
	

	public static void init() {
		Yttr.autoreg.autoRegister(Registries.SOUND_EVENT, YSounds.class, SoundEvent.class);
	}

	private static SoundEvent create(String path) {
		return SoundEvent.createVariableRangeEvent(Yttr.id(path));
	}
	
}
