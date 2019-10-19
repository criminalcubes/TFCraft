package com.bioxx.tfc.Render.Models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelVepr extends ModelBase {
    
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightear;
    private final ModelRenderer leftear;
    private final ModelRenderer upperjaw;
    private final ModelRenderer bottomjaw;
    private final ModelRenderer uphead;
    private final ModelRenderer downhead;
    private final ModelRenderer lefttusk;
    private final ModelRenderer bone16;
    private final ModelRenderer righttusk;
    private final ModelRenderer bone17;
    private final ModelRenderer upcrest;
    private final ModelRenderer downcrest;
    private final ModelRenderer topforpaw;
    private final ModelRenderer hindpaw;
    private final ModelRenderer tail;
    private final ModelRenderer bone15;
    private final ModelRenderer ass;
    private final ModelRenderer rightforepaw;
    private final ModelRenderer leftforepaw;
    private final ModelRenderer righthidleg;
    private final ModelRenderer lefthindleg;

    public ModelVepr() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(-4.5F, 11.0F, 0.5F);
        body.cubeList.add(new ModelBox(body, 0, 0, -7.5F, -7.0F, -10.5F, 15, 12, 21, 0.0F));

        head = new ModelRenderer(this);
        head.setRotationPoint(-4.5F, 9.5F, -14.5F);
        head.cubeList.add(new ModelBox(head, 72, 0, -5.5F, -5.5F, -4.5F, 11, 10, 9, 0.0F));

        rightear = new ModelRenderer(this);
        rightear.setRotationPoint(-4.9F, -5.5F, 1.4F);
        setRotationAngle(rightear, -0.4538F, 0.0F, -0.6981F);
        head.addChild(rightear);
        rightear.cubeList.add(new ModelBox(rightear, 0, 104, -2.0F, -3.0F, -2.0F, 4, 6, 4, 0.0F));

        leftear = new ModelRenderer(this);
        leftear.setRotationPoint(5.1F, -5.5F, 1.4F);
        setRotationAngle(leftear, -0.4538F, 0.0F, 0.6981F);
        head.addChild(leftear);
        leftear.cubeList.add(new ModelBox(leftear, 16, 104, -2.0F, -3.0F, -2.0F, 4, 6, 4, 0.0F));

        upperjaw = new ModelRenderer(this);
        upperjaw.setRotationPoint(0.0F, 1.0F, -6.9F);
        setRotationAngle(upperjaw, 0.1745F, 0.0F, 0.0F);
        head.addChild(upperjaw);
        upperjaw.cubeList.add(new ModelBox(upperjaw, 80, 51, -3.5F, -2.0F, -2.5F, 7, 5, 7, 0.0F));

        bottomjaw = new ModelRenderer(this);
        bottomjaw.setRotationPoint(0.0F, 2.5F, -4.4F);
        setRotationAngle(bottomjaw, 0.1745F, 0.0F, 0.0F);
        head.addChild(bottomjaw);
        bottomjaw.cubeList.add(new ModelBox(bottomjaw, 54, 51, -2.5F, -3.6F, -4.0F, 5, 6, 8, 0.0F));

        uphead = new ModelRenderer(this);
        uphead.setRotationPoint(0.0F, -0.5F, -4.9F);
        setRotationAngle(uphead, 1.2741F, 0.0F, 0.0F);
        head.addChild(uphead);
        uphead.cubeList.add(new ModelBox(uphead, 64, 86, -2.5F, -0.5F, -2.5F, 5, 5, 6, 0.0F));

        downhead = new ModelRenderer(this);
        downhead.setRotationPoint(4.5F, 14.5F, 14.5F);
        setRotationAngle(downhead, -0.1222F, 0.0F, 0.0F);
        head.addChild(downhead);
        downhead.cubeList.add(new ModelBox(downhead, 38, 33, -8.0F, -16.7F, -19.0F, 7, 9, 9, 0.0F));

        lefttusk = new ModelRenderer(this);
        lefttusk.setRotationPoint(-4.6106F, 2.5005F, -5.9F);
        setRotationAngle(lefttusk, 0.0F, 0.0F, -0.1745F);
        head.addChild(lefttusk);
        lefttusk.cubeList.add(new ModelBox(lefttusk, 68, 104, -1.1658F, -2.2475F, -1.0F, 2, 3, 2, 0.0F));

        righttusk = new ModelRenderer(this);
        righttusk.setRotationPoint(4.8223F, 1.5106F, -5.9F);
        setRotationAngle(righttusk, 0.0F, 0.0F, 0.1745F);
        head.addChild(righttusk);
        righttusk.cubeList.add(new ModelBox(righttusk, 60, 109, -1.0638F, -1.2857F, -1.0F, 2, 3, 2, 0.0F));

        bone17 = new ModelRenderer(this);
        bone17.setRotationPoint(-0.7368F, 1.5688F, -1.0F);
        setRotationAngle(bone17, 0.0F, 0.0F, 0.9076F);
        righttusk.addChild(bone17);
        bone17.cubeList.add(new ModelBox(bone17, 44, 104, -0.9675F, -1.0273F, -0.0F, 2, 4, 2, 0.0F));

        upcrest = new ModelRenderer(this);
        upcrest.setRotationPoint(-4.5F, 3.0F, -11.0F);
        setRotationAngle(upcrest, 0.1745F, 0.0F, 0.0F);
        upcrest.cubeList.add(new ModelBox(upcrest, 40, 86, -2.0F, -0.5F, -6.0F, 4, 3, 8, 0.0F));

        downcrest = new ModelRenderer(this);
        downcrest.setRotationPoint(-4.5F, 3.0F, -3.5F);
        setRotationAngle(downcrest, -0.1047F, 0.0F, 0.0F);
        downcrest.cubeList.add(new ModelBox(downcrest, 0, 33, -2.0F, -0.2F, -5.9F, 4, 2, 15, 0.0F));

        topforpaw = new ModelRenderer(this);
        topforpaw.setRotationPoint(-4.5F, 12.0F, 0.875F);
        setRotationAngle(topforpaw, -0.2618F, 0.0F, 0.0F);
        topforpaw.cubeList.add(new ModelBox(topforpaw, 24, 68, 4.0F, -3.5F, -10.125F, 5, 8, 7, 0.0F));
        topforpaw.cubeList.add(new ModelBox(topforpaw, 0, 68, -9.0F, -3.5F, -10.125F, 5, 8, 7, 0.0F));

        hindpaw = new ModelRenderer(this);
        hindpaw.setRotationPoint(-4.5F, 11.5F, 8.0F);
        setRotationAngle(hindpaw, 0.2618F, 0.0F, 0.0F);
        hindpaw.cubeList.add(new ModelBox(hindpaw, 70, 33, -8.0F, -5.0F, -3.25F, 6, 10, 8, 0.0F));
        hindpaw.cubeList.add(new ModelBox(hindpaw, 28, 51, 2.0F, -5.0F, -2.25F, 6, 10, 7, 0.0F));

        tail = new ModelRenderer(this);
        tail.setRotationPoint(-4.6F, 6.6F, 12.7F);
        tail.cubeList.add(new ModelBox(tail, 0, 114, -0.4F, -0.6F, -1.7F, 1, 1, 3, 0.0F));

        bone15 = new ModelRenderer(this);
        bone15.setRotationPoint(-0.3F, 2.3F, 2.0F);
        setRotationAngle(bone15, -1.1345F, 0.0F, 0.0F);
        tail.addChild(bone15);
        bone15.cubeList.add(new ModelBox(bone15, 32, 104, -0.1F, -0.5962F, -2.8895F, 1, 1, 5, 0.0F));

        bone16 = new ModelRenderer(this);
        bone16.setRotationPoint(0.3F, 0.85F, 3.3541F);
        setRotationAngle(bone16, 1.117F, 0.0F, 0.0F);
        bone15.addChild(bone16);
        bone16.cubeList.add(new ModelBox(bone16, 60, 104, -0.9F, -1.7262F, -0.821F, 2, 3, 2, 0.0F));

        ass = new ModelRenderer(this);
        ass.setRotationPoint(-4.5F, 11.5F, 8.0F);
        setRotationAngle(ass, 0.2618F, 0.0F, 0.0F);
        ass.cubeList.add(new ModelBox(ass, 0, 51, -3.0F, -6.4F, -3.25F, 6, 9, 8, 0.0F));

        rightforepaw = new ModelRenderer(this);
        rightforepaw.setRotationPoint(0.0F, 24.0F, 0.0F);
        rightforepaw.cubeList.add(new ModelBox(rightforepaw, 0, 86, -12.6F, -13.0F, -8.9F, 5, 13, 5, 0.0F));

        leftforepaw = new ModelRenderer(this);
        leftforepaw.setRotationPoint(0.0F, 24.0F, 0.0F);
        leftforepaw.cubeList.add(new ModelBox(leftforepaw, 20, 86, -1.4F, -13.0F, -8.8F, 5, 13, 5, 0.0F));

        righthidleg = new ModelRenderer(this);
        righthidleg.setRotationPoint(0.0F, 24.0F, 0.0F);
        righthidleg.cubeList.add(new ModelBox(righthidleg, 48, 68, -11.9F, -13.0F, 7.4F, 5, 13, 5, 0.0F));

        lefthindleg = new ModelRenderer(this);
        lefthindleg.setRotationPoint(0.0F, 24.0F, 0.0F);
        lefthindleg.cubeList.add(new ModelBox(lefthindleg, 68, 68, -2.1F, -13.0F, 7.4F, 5, 13, 5, 0.0F));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        body.render(f5);
        head.render(f5);
        upcrest.render(f5);
        downcrest.render(f5);
        topforpaw.render(f5);
        hindpaw.render(f5);
        tail.render(f5);
        ass.render(f5);
        rightforepaw.render(f5);
        leftforepaw.render(f5);
        righthidleg.render(f5);
        lefthindleg.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
    {
        float f6 = (180F / (float)Math.PI);
        this.head.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
        this.head.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
        this.body.rotateAngleX = ((float)Math.PI / 2F);
        this.rightforepaw.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
        this.leftforepaw.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
        this.righthidleg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
        this.lefthindleg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
    }
}