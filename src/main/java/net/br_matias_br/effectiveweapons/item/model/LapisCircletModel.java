// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17+ for Yarn

package net.br_matias_br.effectiveweapons.item.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class LapisCircletModel extends BipedEntityModel<LivingEntity> {
	private final ModelPart Head;
	private final ModelPart body;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	public final ModelPart hat;
	public LapisCircletModel(ModelPart root) {
		super(root);
		this.Head = root.getChild("head");
		this.body = root.getChild("body");
		this.right_arm = root.getChild("right_arm");
		this.left_arm = root.getChild("left_arm");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
		this.hat = root.getChild("hat");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData circlet = head.addChild("circlet", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, -8.0F, 4.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-4.0F, -8.0F, 4.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 2).cuboid(-4.0F, -8.0F, -5.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 2).cuboid(1.0F, -8.0F, -5.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 4).cuboid(-1.0F, -8.5F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(4.0F, -8.0F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
				.uv(10, 0).cuboid(-4.0F, -8.0F, -4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(10, 0).cuboid(3.0F, -8.0F, -4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(10, 0).cuboid(3.0F, -8.0F, 3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(10, 0).cuboid(-4.0F, -8.0F, 3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

		ModelPartData rightSide_r1 = circlet.addChild("rightSide_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-4.5F, -7.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

		ModelPartData circletHeldR = right_arm.addChild("circletHeldR", ModelPartBuilder.create().uv(16, 0).cuboid(1.0F, -0.5F, 2.2273F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 0).cuboid(-2.0F, -0.5F, 2.2273F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(17, 2).cuboid(-2.0F, -0.5F, -2.7727F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 2).cuboid(1.0F, -0.5F, -2.7727F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 4).cuboid(-1.0F, -1.0F, -2.7727F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(22, 0).cuboid(-3.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(22, 0).cuboid(2.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(20, 0).cuboid(-2.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(1.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(1.0F, -0.5F, 1.2273F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(-2.0F, -0.5F, 1.2273F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.pivot(-1.0F, 7.5F, -0.2273F));

		ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

		ModelPartData circletHeldL = left_arm.addChild("circletHeldL", ModelPartBuilder.create().uv(16, 0).cuboid(1.0F, -0.5F, 2.2273F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 0).cuboid(-2.0F, -0.5F, 2.2273F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(17, 2).cuboid(-2.0F, -0.5F, -2.7727F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 2).cuboid(1.0F, -0.5F, -2.7727F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 4).cuboid(-1.0F, -1.0F, -2.7727F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(22, 0).cuboid(-3.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(22, 0).cuboid(2.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(20, 0).cuboid(-2.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(1.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(1.0F, -0.5F, 1.2273F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(-2.0F, -0.5F, 1.2273F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.pivot(1.0F, 7.5F, -0.2273F));

		ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

		ModelPartData circletWornR = right_leg.addChild("circletWornR", ModelPartBuilder.create().uv(16, 0).cuboid(1.0F, -0.5F, 2.2273F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 0).cuboid(-2.0F, -0.5F, 2.2273F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(17, 2).cuboid(-2.0F, -0.5F, -2.7727F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 2).cuboid(1.0F, -0.5F, -2.7727F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 4).cuboid(-1.0F, -1.0F, -2.7727F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(22, 0).cuboid(-3.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(22, 0).cuboid(2.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(20, 0).cuboid(-2.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(1.0F, -0.5F, -1.7727F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(1.0F, -0.5F, 1.2273F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F))
				.uv(20, 0).cuboid(-2.0F, -0.5F, 1.2273F, 1.0F, 1.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.pivot(-0.1F, 9.5F, -0.2273F));

		ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(1.9F, 12.0F, 0.0F));

		ModelPartData hat = modelPartData.addChild("hat", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		head.render(matrices, vertices, light, overlay, color);
		rightLeg.render(matrices, vertices, light, overlay, color);
	}
}