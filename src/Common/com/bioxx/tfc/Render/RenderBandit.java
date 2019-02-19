package com.bioxx.tfc.Render;

import com.bioxx.tfc.Entities.Mobs.EntityBandit;
import com.bioxx.tfc.Reference;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBandit extends RenderBiped {

    private static final ResourceLocation BANDIT_TEXTURE1 = new ResourceLocation(Reference.MOD_ID, "textures/mob/bandit1.png");
    private static final ResourceLocation BANDIT_TEXTURE2 = new ResourceLocation(Reference.MOD_ID, "textures/mob/bandit2.png");

    public RenderBandit() {
        super(new ModelBiped(), 0.5F, 1.0F);
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        EntityBandit bandit = (EntityBandit) entity;
        ResourceLocation location = bandit.getTextureVariant() == 1 ? BANDIT_TEXTURE1 : BANDIT_TEXTURE2;
        return location;
    }
}
