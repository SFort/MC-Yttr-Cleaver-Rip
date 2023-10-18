package tf.ssf.sfort.yttr.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import tf.ssf.sfort.yttr.Yttr;
import tf.ssf.sfort.yttr.client.cache.CleavedBlockMeshes;
import tf.ssf.sfort.yttr.client.render.CleaverUI;
import tf.ssf.sfort.yttr.client.util.TextureColorThief;
import tf.ssf.sfort.yttr.init.YBlockEntities;
import tf.ssf.sfort.yttr.init.YItems;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

public class YttrClient extends IHasAClient implements ClientModInitializer {

	public static final VertexFormat POSITION_NORMAL = new VertexFormat(ImmutableMap.of(
			"Position", VertexFormats.POSITION_ELEMENT,
			"Normal", VertexFormats.NORMAL_ELEMENT));
	
	public static ShaderProgram positionNormalShader;

	@Override
	public void onInitializeClient() {
		doReflectionMagic();
		mc.send(() -> {
			ReloadableResourceManager rm = (ReloadableResourceManager)mc.getResourceManager();
			rm.registerReloader(reloader("yttr:clear_caches", (manager) -> {
				TextureColorThief.clearCache();
				CleavedBlockMeshes.clearCache();
			}));
		});

		ModelPredicateProviderRegistry.register(Yttr.id("has_block_entity"), (stack, world, entity, seed) -> {
			return stack.getSubNbt("BlockEntityTag") != null ? 1 : 0;
		});

		
		WorldRenderEvents.BLOCK_OUTLINE.register(CleaverUI::render);
		DynamicBlockModelProvider.init();
		
		CoreShaderRegistrationCallback.EVENT.register(ctx -> {
			ctx.register(Yttr.id("position_normal"), POSITION_NORMAL, it -> positionNormalShader = it);
		});

	}

	private SimpleSynchronousResourceReloadListener reloader(String idStr, Consumer<ResourceManager> cb) {
		Identifier id = new Identifier(idStr);
		return new SimpleSynchronousResourceReloadListener() {
			@Override
			public void reload(ResourceManager manager) {
				cb.accept(manager);
			}
			
			@Override
			public Identifier getFabricId() {
				return id;
			}
		};
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doReflectionMagic() {
		Map<String, RenderLayer> renderLayers = Maps.newHashMap();
		renderLayers.put("cutout", RenderLayer.getCutout());
		renderLayers.put("cutout_mipped", RenderLayer.getCutoutMipped());
		renderLayers.put("translucent", RenderLayer.getTranslucent());
		renderLayers.put("tripwire", RenderLayer.getTripwire());

		Yttr.autoreg.eachRegisterableField(YItems.class, Item.class, null, this::handleItemAutoreg);
		Yttr.autoreg.eachRegisterableField(YBlockEntities.class, BlockEntityType.class, YBlockEntities.Renderer.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					MethodHandle handle = MethodHandles.publicLookup().findConstructor(Class.forName("com.unascribed.yttr.client.render.block_entity."+ann.value()), MethodType.methodType(void.class));
					BlockEntityRendererFactories.register(type, berd -> {
						try {
							return (BlockEntityRenderer<?>)handle.invoke();
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
	}
	
	public void handleItemAutoreg(Field f, Item i, Annotation ann) {
		if (i instanceof ItemColorProvider) ColorProviderRegistry.ITEM.register((ItemColorProvider)i, i);
		YItems.ColorProvider colProvAnn = f.getAnnotation(YItems.ColorProvider.class);
		if (colProvAnn != null) {
			try {
				ColorProviderRegistry.ITEM.register((ItemColorProvider)Class.forName("com.unascribed.yttr.client."+colProvAnn.value()).getConstructor().newInstance(), i);
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
		YItems.BuiltinRenderer birAnn = f.getAnnotation(YItems.BuiltinRenderer.class);
		if (birAnn != null) {
			try {
				Class<?> rend = Class.forName("com.unascribed.yttr.client.render."+birAnn.value());
				MethodHandle renderHandle = MethodHandles.publicLookup().findStatic(rend, "render", MethodType.methodType(void.class, ItemStack.class, ModelTransformationMode.class, MatrixStack.class, VertexConsumerProvider.class, int.class, int.class));
				BuiltinItemRendererRegistry.INSTANCE.register(i, (is, mode, matrices, vcp, light, overlay) -> {
					try {
						renderHandle.invoke(is, mode, matrices, vcp, light, overlay);
					} catch (RuntimeException | Error e) {
						throw e;
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				});
				try {
					MethodHandle registerModelsHandle = MethodHandles.publicLookup().findStatic(rend, "registerModels", MethodType.methodType(void.class, Consumer.class));
					ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
						try {
							registerModelsHandle.invoke(out);
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
				} catch (NoSuchMethodException e) {
					// ignore
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void addLine(MatrixStack matrices, VertexConsumer vc,
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			float r1, float g1, float b1, float a1,
			float r2, float g2, float b2, float a2) {
		addLine(matrices, vc,
				(float)x1, (float)y1, (float)z1,
				(float)x2, (float)y2, (float)z2,
				r1, g1, b1, a1,
				r2, g2, b2, a2);
	}

	public static void addLine(MatrixStack matrices, VertexConsumer vc,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float r1, float g1, float b1, float a1,
			float r2, float g2, float b2, float a2) {
		float dX = x2 - x1;
		float dY = y2 - y1;
		float dZ = z2 - z2;
		float dist = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
		dX /= dist;
		dY /= dist;
		dZ /= dist;
		Matrix4f model = matrices.peek().getModel();
		Matrix3f normal = matrices.peek().getNormal();
		vc.vertex(model, x1, y1, z1).color(r1, g1, b1, a1).normal(normal, dX, dY, dZ).next();
		vc.vertex(model, x2, y2, z2).color(r2, g2, b2, a2).normal(normal, dX, dY, dZ).next();
	}
	
}
