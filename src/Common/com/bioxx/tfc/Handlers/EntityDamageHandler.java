package com.bioxx.tfc.Handlers;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

import com.bioxx.tfc.Core.Player.FoodStatsTFC;
import com.bioxx.tfc.Core.Player.PlayerInfo;
import com.bioxx.tfc.Core.Player.PlayerManagerTFC;
import cpw.mods.fml.common.eventhandler.EventPriority;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.common.ISpecialArmor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_MobData;
import com.bioxx.tfc.Entities.EntityJavelin;
import com.bioxx.tfc.Items.ItemTFCArmor;
import com.bioxx.tfc.api.Enums.EnumDamageType;
import com.bioxx.tfc.api.Events.EntityArmorCalcEvent;
import com.bioxx.tfc.api.Interfaces.ICausesDamage;
import com.bioxx.tfc.api.Interfaces.IInnateArmor;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EntityDamageHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityHurt(LivingHurtEvent event)
	{
		EntityLivingBase entity = event.entityLiving;

		/*
		if (entity instanceof EntityPlayer) {
			float curMaxHealth = (float)((EntityPlayer) entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
			float newMaxHealth = FoodStatsTFC.getMaxHealth((EntityPlayer) entity);
			float h = ((EntityPlayer) entity).getHealth();
			if(newMaxHealth != curMaxHealth)
				((EntityPlayer) entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newMaxHealth);
			if(newMaxHealth < h)
				((EntityPlayer) entity).setHealth(newMaxHealth);

			//initialDamage = ISpecialArmor.ArmorProperties.ApplyArmor(event.entityLiving, ((EntityPlayer) event.entityLiving).inventory.armorInventory, event.source, initialDamage);
		}
		*/

		event.source.setDamageBypassesArmor();

		boolean tfcDamage = false;

		String[] parts = event.source.damageType.split("\\|");
		if (parts.length > 1 && parts[1].equals("tfc")) {
			tfcDamage = true;
			event.source.damageType = parts[0];
		}

		float newDamage = event.ammount;
		float initialDamage = event.ammount;

		if (!tfcDamage) {

			if(event.source == DamageSource.onFire)
			{
				newDamage = 50;
			}
			else if (event.source == DamageSource.inFire)
			{
				newDamage = 50;
			}
			else if (event.source == DamageSource.fall)
			{
				float healthMod = TFC_Core.getEntityMaxHealth(entity) / 1000f;
				newDamage *= 80 * healthMod;
			}
			else if (event.source == DamageSource.drown)
			{
				newDamage = 50;
			}
			else if (event.source == DamageSource.lava)
			{
				newDamage = 100;
			}
			else if (event.source == DamageSource.inWall)
			{
				if (entity instanceof EntityPlayer) {
					//newDamage = 50;
				} else {
					newDamage = 5;
				}
			}
			else if (event.source == DamageSource.fallingBlock)
			{
				if (entity instanceof EntityPlayer) {
					newDamage = 50;
				} else {
					newDamage = 5;
				}
			}
			else if (event.source == DamageSource.outOfWorld)
			{
				System.out.println("[TerraFirmaCraft] (WARN ) Out of world entity " + entity.getCommandSenderName() + " COORDS: " + entity.posX + " " + entity.posY + " " + entity.posZ);
				if (entity instanceof EntityPlayer) {
					// Prevent bed > y=127 bug
					if (entity.posY > 0) {
						entity.setAbsorptionAmount(event.ammount);
					}
				} else {
					// Prevent CustomNPCs falling
					if (entity.getClass().getName().toLowerCase().contains("custom")) {
						entity.setPositionAndUpdate(entity.posX, 255, entity.posZ);
					}
					newDamage = 100;
				}
			}
			else if ("thorns".equals(event.source.damageType))
			{
				// do nothing
			}
			else if ("cactus".equals(event.source.damageType))
			{
				// do nothing
			}
			else if ("indirectMagic".equals(event.source.damageType))
			{
				newDamage *= 10;
			}
			else if (event.source.isExplosion())
			{
				newDamage *= 30;
			}
			else if (event.source == DamageSource.magic && entity.getHealth() > 25)
			{
				newDamage = 25;
			}
			else if ("player".equals(event.source.damageType) || "mob".equals(event.source.damageType) || "arrow".equals(event.source.damageType) || "thrown".equals(event.source.damageType))
			{
				// Blocking
				if (entity instanceof EntityPlayer) {
					if (((EntityPlayer) entity).isBlocking()) {
						newDamage = newDamage / 2;
					}
				}

				newDamage = applyArmorCalculations(entity, event.source, newDamage);

				if ("arrow".equals(event.source.damageType) || "thrown".equals(event.source.damageType))
				{
					Entity e = ((EntityDamageSourceIndirect) event.source).getSourceOfDamage();
					if (e instanceof EntityJavelin) {
						((EntityJavelin)e).setDamageTaken((short) (((EntityJavelin) e).damageTaken + 10));
						if (((EntityJavelin) e).damageTaken >= ((EntityJavelin) e).pickupItem.getMaxDamage()) {
							e.setDead();
						}
					} else if (e instanceof EntityArrow) {
						e.setDead();
					}
				}
			} else {
				System.out.println("[TerraFirmaCraft] (WARN) Damage type is not registered: " + event.source.damageType);
			}

			if ((newDamage != initialDamage) && (entity.getHealth() > 0) && !entity.isDead) {

				// prevent 2 deaths
				boolean preventVanillaDamage = true;
				if (newDamage >= entity.getHealth()) {
					preventVanillaDamage = false;
					if (initialDamage < entity.getHealth()) {
						newDamage = entity.getHealth() - initialDamage;
					} else {
						newDamage = 0;
					}
				}

				entity.hurtResistantTime = 0;

				DamageSource damageSource = event.source;
				damageSource.damageType = damageSource.damageType + "|tfc";
				entity.attackEntityFrom(damageSource, newDamage);
				if (preventVanillaDamage) {
					entity.setAbsorptionAmount(initialDamage);
				}
			} else {

				// debug
				if (entity instanceof EntityPlayer) {
					if (TFC_Core.isPlayerInDebugMode((EntityPlayer) entity)) {
						TFC_Core.sendInfoMessage(
								(EntityPlayer) entity,
								new ChatComponentTranslation(
										"Source: %s, damage: %s (vanilla)",
										event.source.damageType + (Objects.nonNull(event.source.getEntity()) ? " (" + event.source.getEntity().getCommandSenderName() + ")" : ""),
										initialDamage
								)
						);
					}
				}
			}

		} else {
			// do nothing

			// debug
			if (entity instanceof EntityPlayer) {
				if (TFC_Core.isPlayerInDebugMode((EntityPlayer) entity)) {
					TFC_Core.sendInfoMessage(
							(EntityPlayer) entity,
							new ChatComponentTranslation(
									"Source: %s, damage: %s (tfc)",
									event.source.damageType + (Objects.nonNull(event.source.getEntity()) ? " (" + event.source.getEntity().getCommandSenderName() + ")" : ""),
									initialDamage
							)
					);
				}
			}
		}


		/*
		ItemStack[] armor = entity.getLastActiveItems();
		if (Objects.nonNull(armor) && armor.length > 0) {
			for (ItemStack armorPart : armor) {
				if (Objects.nonNull(armorPart)) {
					//armorPart.setItemDamage(0);
					armorPart.
					armorPart.setItemDamage(armorPart.getItemDamage() - ((int) initialDamage) + 1);
				}
			}
		}
		*/
	}


	protected float applyArmorCalculations(EntityLivingBase entity, DamageSource source, float originalDamage)
	{
		/*
		if(entity instanceof EntityPlayer)
        	{
            		EntityPlayer player = (EntityPlayer) entity;
           		originalDamage = ISpecialArmor.ArmorProperties.ApplyArmor(player, player.inventory.armorInventory, source, originalDamage * 0.048F) / 0.048F;
        	}
        */
		ItemStack[] armor = entity.getLastActiveItems();
		int pierceRating = 0;
		int slashRating = 0;
		int crushRating = 0;

		EntityArmorCalcEvent eventPre = new EntityArmorCalcEvent(entity, originalDamage, EntityArmorCalcEvent.EventType.PRE);
		MinecraftForge.EVENT_BUS.post(eventPre);
		float damage = eventPre.incomingDamage;

		//if (!source.isUnblockable() && armor != null)
		if (armor != null)
		{
			//1. Get Random Hit Location
			int location = getRandomSlot(entity.getRNG());

			//2. Get Armor Rating for armor in hit Location
			if(armor[location] != null && armor[location].getItem() instanceof ItemTFCArmor)
			{
				pierceRating = ((ItemTFCArmor)armor[location].getItem()).armorTypeTFC.getPiercingAR();
				slashRating = ((ItemTFCArmor)armor[location].getItem()).armorTypeTFC.getSlashingAR();
				crushRating = ((ItemTFCArmor)armor[location].getItem()).armorTypeTFC.getCrushingAR();
				if(entity instanceof IInnateArmor)
				{
					pierceRating += ((IInnateArmor)entity).getPierceArmor();
					slashRating += ((IInnateArmor)entity).getSlashArmor();
					crushRating += ((IInnateArmor) entity).getCrushArmor();
				}

				//3. Convert the armor rating to % damage reduction
				float pierceMult = getDamageReduction(pierceRating);
				float slashMult = getDamageReduction(slashRating);
				float crushMult = getDamageReduction(crushRating);

				//4. Reduce incoming damage
				damage = processDamageSource(source, damage, pierceMult,
						slashMult, crushMult);

				// debug
				if (entity instanceof EntityPlayer) {
					if (TFC_Core.isPlayerInDebugMode((EntityPlayer) entity)) {
						TFC_Core.sendInfoMessage(
								(EntityPlayer) entity,
								new ChatComponentTranslation(
										"Damage armor: %s, %s",
										armor[location].getDisplayName(),
										(int) processArmorDamage(armor[location], damage)
								)
						);
						TFC_Core.sendInfoMessage(
								(EntityPlayer) entity,
								new ChatComponentTranslation(
										"pierceMult: %s, slashMult: %s, crushMult: %s",
										pierceMult,
										slashMult,
										crushMult
								)
						);
					}
				}

				//5. Damage the armor that was hit
				armor[location].damageItem((int) processArmorDamage(armor[location], damage), entity);
			}
			else if (armor[location] == null || armor[location] != null && !(armor[location].getItem() instanceof ItemTFCArmor))
			{
				if(entity instanceof IInnateArmor)
				{
					pierceRating += ((IInnateArmor)entity).getPierceArmor();
					slashRating += ((IInnateArmor)entity).getSlashArmor();
					crushRating += ((IInnateArmor) entity).getCrushArmor();
				}
				//1. Convert the armor rating to % damage reduction
				float pierceMult = getDamageReduction(pierceRating);
				float slashMult = getDamageReduction(slashRating);
				float crushMult = getDamageReduction(crushRating);
				//4. Reduce incoming damage
				damage = processDamageSource(source, damage, pierceMult, slashMult, crushMult);

				//a. If the attack hits an unprotected head, it does 75% more damage
				//b. If the attack hits unprotected feet, it applies a slow to the player
				if (location == 3) {
					if (entity instanceof EntityPlayer) {
						TFC_Core.sendInfoMessage((EntityPlayer) entity, new ChatComponentTranslation("hit.head"));
					}
					damage *= 1.75f;

				} else if (location == 0) {
					if (entity instanceof EntityPlayer) {
						TFC_Core.sendInfoMessage((EntityPlayer) entity, new ChatComponentTranslation("hit.legs"));
					}
					entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 100, 1));
				}
			}
			//6. Apply the damage to the player

			EntityArmorCalcEvent eventPost = new EntityArmorCalcEvent(entity, damage, EntityArmorCalcEvent.EventType.POST);
			MinecraftForge.EVENT_BUS.post(eventPost);

			float hasHealth = entity.getHealth();

			entity.func_110142_aN().func_94547_a(source, hasHealth, eventPost.incomingDamage);

			return damage;
		}
		return 0;
	}

	private float processDamageSource(DamageSource source, float damage,
									  float pierceMult, float slashMult, float crushMult)
	{
		EnumDamageType damageType = getDamageType(source);
		//4.2 Reduce the damage based upon the incoming Damage Type
		if(damageType == EnumDamageType.PIERCING)
		{
			damage *= pierceMult;
		}
		else if(damageType == EnumDamageType.SLASHING)
		{
			damage *= slashMult;
		}
		else if(damageType == EnumDamageType.CRUSHING)
		{
			damage *= crushMult;
		}
		else if(damageType == EnumDamageType.GENERIC)
		{
			damage *= (crushMult + slashMult + pierceMult) / 3 - 0.25;
		}
		return Math.max(0, damage);
	}

	private EnumDamageType getDamageType(DamageSource source)
	{
		//4.1 Determine the source of the damage and get the appropriate Damage Type
		if(source.getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)source.getSourceOfDamage();
			if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ICausesDamage)
			{
				return ((ICausesDamage)player.getCurrentEquippedItem().getItem()).getDamageType();
			}
		}

		if (source.getSourceOfDamage() instanceof EntityLiving)
		{
			EntityLiving el = (EntityLiving)source.getSourceOfDamage();
			if(el.getHeldItem() != null && el.getHeldItem().getItem() instanceof ICausesDamage)
			{
				return ((ICausesDamage)el.getHeldItem().getItem()).getDamageType();
			}
		}

		if(source.getSourceOfDamage() instanceof ICausesDamage)
		{
			return ((ICausesDamage)source.getSourceOfDamage()).getDamageType();
		}

		return EnumDamageType.GENERIC;
	}

	private int getRandomSlot(Random rand)
	{
		int chance = rand.nextInt(100);

		if (chance < 10) {
			return 3; //Helm
		} else if (chance < 20) {
			return 0; //Feet
		} else if (chance < 80) {
			return 2; //Chest
		} else {
			return 1; //Legs
		}
	}

	private float processArmorDamage(ItemStack armor, float baseDamage)
	{
		if(armor.hasTagCompound())
		{
			NBTTagCompound nbt = armor.getTagCompound();
			if(nbt.hasKey("armorReductionBuff"))
			{
				float reductBuff = nbt.getByte("armorReductionBuff")/100f;
				return baseDamage - (baseDamage * reductBuff);
			}
		}
		return baseDamage;
	}

	/**
	 * @param armorRating Armor Rating supplied by the armor
	 * @return Multiplier for damage reduction e.g. damage * multiplier = final damage
	 */
	protected float getDamageReduction(int armorRating)
	{
		if(armorRating == -1000)
			armorRating=-999;
		return 1000f / (1000f + armorRating);
	}

	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event)
	{
		if(event.entityLiving.worldObj.isRemote)
			return;

		EntityLivingBase attacker = event.entityLiving;
		EntityPlayer player = event.entityPlayer;
		Entity target = event.target;
		ItemStack stack = attacker.getEquipmentInSlot(0);
		if (stack != null && stack.getItem().onLeftClickEntity(stack, player, target))
			return;

		if (target.canAttackWithItem())
		{
			if (!target.hitByEntity(target))
			{
				float damageAmount = TFC_MobData.STEVE_DAMAGE;
				if(stack != null)
				{
					damageAmount = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
					//player.addChatMessage("Damage: " + i);
					if(damageAmount == 1.0f)
					{
						damageAmount = TFC_MobData.STEVE_DAMAGE;
						//i = player.inventory.getCurrentItem().getItem().getDamageVsEntity(target, player.inventory.getCurrentItem());
					}
				}

				if (player.isPotionActive(Potion.damageBoost))
					damageAmount += 3 << player.getActivePotionEffect(Potion.damageBoost).getAmplifier();

				if (player.isPotionActive(Potion.weakness))
					damageAmount -= 2 << player.getActivePotionEffect(Potion.weakness).getAmplifier();

				int knockback = 0;
				float enchantmentDamage = 0;

				if (target instanceof EntityLiving)
				{
					enchantmentDamage = EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLiving) target);
					knockback += EnchantmentHelper.getKnockbackModifier(player, (EntityLiving) target);
				}

				if (player.isSprinting())
					++knockback;

				if (damageAmount > 0 || enchantmentDamage > 0)
				{
					boolean criticalHit = player.fallDistance > 0.0F && !player.onGround &&
							!player.isOnLadder() && !player.isInWater() &&
							!player.isPotionActive(Potion.blindness) && player.ridingEntity == null &&
							target instanceof EntityLiving;

					if (criticalHit && damageAmount > 0)
						damageAmount += event.entity.worldObj.rand.nextInt((int) (damageAmount / 2 + 2));

					damageAmount += enchantmentDamage;
					boolean onFire = false;
					int fireAspect = EnchantmentHelper.getFireAspectModifier(player);

					if (target instanceof EntityLiving && fireAspect > 0 && !target.isBurning())
					{
						onFire = true;
						target.setFire(1);
					}

					//need for check Towny no-pvp flag
					boolean entityAttacked = target.attackEntityFrom(DamageSource.causePlayerDamage(player), 0);

					if (entityAttacked)
					{
						target.attackEntityFrom(DamageSource.causePlayerDamage(player), damageAmount);
						if (knockback > 0)
						{
							target.addVelocity(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F, 0.1D,
									MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F);
							player.motionX *= 0.6D;
							player.motionZ *= 0.6D;
							player.setSprinting(false);
						}

						if (criticalHit)
							player.onCriticalHit(target);

						if (enchantmentDamage > 0)
							player.onEnchantmentCritical(target);

						if (damageAmount >= 18)
							player.triggerAchievement(AchievementList.overkill);

						player.setLastAttacker(target);

						if (target instanceof EntityLiving)
							target.attackEntityFrom(DamageSource.causeThornsDamage(attacker), damageAmount);
					}

					ItemStack itemstack = player.getCurrentEquippedItem();
					Object object = target;

					if (target instanceof EntityDragonPart)
					{
						IEntityMultiPart ientitymultipart = ((EntityDragonPart)target).entityDragonObj;
						if (ientitymultipart instanceof EntityLiving)
							object = ientitymultipart;
					}

					if (itemstack != null && object instanceof EntityLiving)
					{
						itemstack.hitEntity((EntityLiving) object, player);
						if (itemstack.stackSize <= 0) {
							player.destroyCurrentEquippedItem();
						}
					}

					if (target instanceof EntityLivingBase)
					{
						player.addStat(StatList.damageDealtStat,Math.round(damageAmount * 10.0f));
						if (fireAspect > 0 && entityAttacked)
							target.setFire(fireAspect * 4);
						else if (onFire)
							target.extinguish();
					}

					player.addExhaustion(0.3F);
				}
			}
		}
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;

			//player.worldObj.setBlock((int)player.posX, (int)player.posY, (int)player.posZ, TFCBlocks.chest);
			//TEChest te = (TEChest) player.worldObj.getTileEntity((int)player.posX, (int)player.posY, (int)player.posZ);
			//if (Math.random() <= 0.2) {
			//	te.setInventorySlotContents(0, new ItemStack(TFCItems.bronzeJavelin));
			//}

			PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
			pi.restoreExp = player.experienceTotal;
			pi.deathX = (int) player.posX;
			pi.deathY = (int) player.posY;
			pi.deathZ = (int) player.posZ;

			if (player.getEntityData().hasKey("craftingTable")) {
				pi.hasWorkbench = true;
			} else {
				pi.hasWorkbench = false;
			}
		}
	}
}
