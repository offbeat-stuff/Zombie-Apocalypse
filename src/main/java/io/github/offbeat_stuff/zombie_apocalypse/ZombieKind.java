package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public enum ZombieKind {
  Simple,
  Frost,
  Flame,
  Potion;

  public void attack(ZombieEntity zombie, Entity target) {
    switch (this) {
    case Frost:
      target.setFrozenTicks(target.getMinFreezeDamageTicks() + 40);
      break;
    case Flame:
      target.setOnFire(true);
    case Potion:
      if (target instanceof LivingEntity living) {
        potionAttack(zombie, living);
      }
    default:
      break;
    }
  }

  private void potionAttack(ZombieEntity zombie, LivingEntity target) {
    Vec3d vel = target.getVelocity();
    Vec3d diff =
        target.getEyePos().subtract(zombie.getPos()).add(vel.x, -1.1, vel.z);
    double dist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
    Potion potion = Potions.STRENGTH;

    PotionEntity potionEntity = new PotionEntity(zombie.getWorld(), zombie);
    potionEntity.setItem(
        PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
    potionEntity.setPitch(potionEntity.getPitch() - -20.0F);
    potionEntity.setVelocity(diff.x, diff.y + dist * 0.2, diff.z, 0.75F, 8.0F);
    if (!zombie.isSilent()) {
      zombie.getWorld().playSound(
          (PlayerEntity)null, zombie.getX(), zombie.getY(), zombie.getZ(),
          SoundEvents.ENTITY_WITCH_THROW, zombie.getSoundCategory(), 1.0F,
          0.8F + XRANDOM.nextFloat() * 0.4F);
    }

    zombie.getWorld().spawnEntity(potionEntity);
  }

  public int toInt() {
    int i = 0;
    for (var v : values()) {
      if (this.equals(v)) {
        return i;
      }
      i++;
    }
    return 0;
  }

  public static ZombieKind fromIndex(int i) {
    return values()[Math.max(0, i % values().length)];
  }
}