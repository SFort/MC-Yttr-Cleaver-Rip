package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.UltrapureBonus;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {

	@Inject(at=@At("RETURN"), method="craft")
	public void craft(RecipeInputInventory inv, DynamicRegistryManager mgr, CallbackInfoReturnable<ItemStack> ci) {
		UltrapureBonus.handleCraft(inv, ci.getReturnValue());
	}


}