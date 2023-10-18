package tf.ssf.sfort.yttr.init;

import tf.ssf.sfort.yttr.BlockCriterion;
import tf.ssf.sfort.yttr.Yttr;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;

public class YCriteria {

	public static final BlockCriterion CLEAVE_BLOCK = new BlockCriterion("yttr:cleave_block");

	public static void init() {
		Yttr.autoreg.autoRegister(Criteria::register, YCriteria.class, Criterion.class);
	}
	
}
