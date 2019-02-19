package com.bioxx.tfc.Render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Reference;

public class RenderBoar extends RenderLiving {

    private static final ResourceLocation BOAR_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/mob/boar.png");

    public RenderBoar(ModelBase par1ModelBase, float par3) {
        super(par1ModelBase, par3);
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.shadowSize = 0.55f;
        this.doRender((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
        float scale = 1.2f;
        GL11.glScalef(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return BOAR_TEXTURE;
    }
}
