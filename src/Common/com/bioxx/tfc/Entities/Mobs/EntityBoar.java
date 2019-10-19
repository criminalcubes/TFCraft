package com.bioxx.tfc.Entities.Mobs;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Entities.AI.EntityAILightPanic;
import com.bioxx.tfc.Entities.AI.EntityAIPanicTFC;
import com.bioxx.tfc.api.TFCItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.bioxx.tfc.Core.TFC_MobData;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.Enums.EnumDamageType;
import com.bioxx.tfc.api.Interfaces.ICausesDamage;

public class EntityBoar extends EntityMob implements ICausesDamage {

    private float moveSpeed = 0.4F;

    public EntityBoar(World par1World) {
        super(par1World);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, moveSpeed, false));
        this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, moveSpeed));
        this.tasks.addTask(3, new EntityAIWander(this, moveSpeed));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        this.setSize(0.9F, 0.9F);
        this.setCanPickUpLoot(false);
        this.experienceValue = 6;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(TFC_MobData.BOAR_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(TFC_MobData.BOAR_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(TFC_MobData.BOAR_SPEED);
    }

    @Override
    public EnumDamageType getDamageType() {
        return EnumDamageType.PIERCING;
    }

    @Override
    public boolean getCanSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        Block b = this.worldObj.getBlock(x, y, z);

        if (b == TFCBlocks.leaves || b == TFCBlocks.leaves2 || b == TFCBlocks.thatch)
            return false;

        return super.getCanSpawnHere();
    }

    @Override
    protected Entity findPlayerToAttack() {
        double d0 = 16.0D;
        return this.worldObj.getClosestVulnerablePlayerToEntity(this, d0);
    }

    @Override
    protected void attackEntity(Entity p_70785_1_, float p_70785_2_) {
        if (this.rand.nextInt(100) == 0) {
            this.entityToAttack = null;
        } else {
            if (p_70785_2_ > 2.0F && p_70785_2_ < 6.0F && this.rand.nextInt(10) == 0) {
                if (this.onGround) {
                    double d0 = p_70785_1_.posX - this.posX;
                    double d1 = p_70785_1_.posZ - this.posZ;
                    float f2 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
                    this.motionX = d0 / (double) f2 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                    this.motionZ = d1 / (double) f2 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                    this.motionY = 0.4000000059604645D;
                }
            } else {
                super.attackEntity(p_70785_1_, p_70785_2_);
            }
        }
    }

    @Override
    protected String getLivingSound() {
        return "mob.pig.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.pig.say";
    }

    @Override
    protected String getDeathSound() {
        return "mob.pig.death";
    }

    @Override
    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
        this.playSound("mob.pig.step", 0.15F, 1.0F);
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        if (Math.random() <= 0.25) {
            this.entityDropItem(new ItemStack(TFCItems.hide, 1, rand.nextInt(3)), 0);
        }

        if (Math.random() <= 0.25) {
            this.dropItem(Items.bone, rand.nextInt(2));
        }

        TFC_Core.animalDropMeat(this, TFCItems.porkchopRaw, (float) Math.random() * 5000 / 2);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
    }

    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
    }

    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public float getAIMoveSpeed()
    {
        return this.isAIEnabled() ? moveSpeed : 0.4F;
    }
}
