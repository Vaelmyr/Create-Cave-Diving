package com.vaelmyr.create_cave_diving.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class RespiratorArmorModel extends HumanoidModel<LivingEntity> {
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;
    private static final float MODEL_SCALE = 8.0F / 12.0F;

    public RespiratorArmorModel(ModelPart root) {
        super(root);

        ModelPart respirator = this.head.getChild("respirator");

        respirator.xScale = MODEL_SCALE;
        respirator.yScale = MODEL_SCALE;
        respirator.zScale = MODEL_SCALE;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition respirator = head.addOrReplaceChild("respirator", CubeListBuilder.create(), PartPose.ZERO);

        respirator.addOrReplaceChild("middle_filter",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, -3.5F, -2.0F, 7.0F, 7.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, -7.0F, 22.5F * DEG_TO_RAD, 0.0F, 0.0F));

        respirator.addOrReplaceChild("right_filter",
                CubeListBuilder.create().texOffs(22, 19).addBox(-2.5F, -2.5F, -1.5F, 5.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(-6.2F, -2.0F, -6.5F, 0.0F, 22.5F * DEG_TO_RAD, 0.0F));

        respirator.addOrReplaceChild("left_filter",
                CubeListBuilder.create().texOffs(22, 19).addBox(-2.5F, -2.5F, -1.5F, 5.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(6.2F, -2.0F, -6.5F, 0.0F, -22.5F * DEG_TO_RAD, 0.0F));

        PartDefinition band = respirator.addOrReplaceChild("band", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -4.9F, 0.3F, 22.5F * DEG_TO_RAD, 0.0F, 0.0F));

        band.addOrReplaceChild("back_band",
                CubeListBuilder.create().texOffs(22, 16).addBox(-6.0F, -1.1F, 6.5F, 13.0F, 2.0F, 1.0F), PartPose.ZERO);

        band.addOrReplaceChild("left_band",
                CubeListBuilder.create().texOffs(0, 0).addBox(6.0F, -1.1F, -7.5F, 1.0F, 2.0F, 14.0F), PartPose.ZERO);

        band.addOrReplaceChild("right_band",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -1.1F, -6.5F, 1.0F, 2.0F, 14.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
