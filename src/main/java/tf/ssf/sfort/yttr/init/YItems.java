package tf.ssf.sfort.yttr.init;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import tf.ssf.sfort.yttr.Yttr;
import tf.ssf.sfort.yttr.content.item.CleaverItem;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class YItems {

	public static final Item CLEAVER = new CleaverItem(new Item.Settings()
			.maxDamage(1562));
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.ITEM, YItems.class, Item.class);
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface BuiltinRenderer {
		String value();
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface ColorProvider {
		String value();
	}

}
