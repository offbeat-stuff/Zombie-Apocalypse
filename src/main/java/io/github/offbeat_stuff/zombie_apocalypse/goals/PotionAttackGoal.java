package io.github.offbeat_stuff.zombie_apocalypse.goals;

import io.github.offbeat_stuff.zombie_apocalypse.ZombieEntityInterface;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class PotionAttackGoal extends Goal {

  private final ZombieEntity owner;
  @Nullable private LivingEntity target;
  private int countdown;
  private final double mobSpeed;
  private int lastSeenTarget;
  private final int minInterval;
  private final int maxInterval;
  private final float shootRange;
  private final float sqShootRange;

  public PotionAttackGoal(ZombieEntity zombie, double mobSpeed,
                          int minIntervalTicks, int maxIntervalTicks,
                          float maxShootRange) {
    this.countdown = -1;
    this.owner = zombie;
    this.mobSpeed = mobSpeed;
    this.minInterval = minIntervalTicks;
    this.maxInterval = maxIntervalTicks;
    this.shootRange = maxShootRange;
    this.sqShootRange = maxShootRange * maxShootRange;
    this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  @Override
  public boolean canStart() {
    var target = this.owner.getTarget();
    if (target == null || !target.isAlive()) {
      return false;
    }
    this.target = target;
    return true;
  }

  public boolean shouldContinue() {
    return this.canStart() ||
        this.target.isAlive() && !this.owner.getNavigation().isIdle();
  }

  public void stop() {
    this.target = null;
    this.lastSeenTarget = 0;
    this.countdown = -1;
  }

  public boolean shouldRunEveryTick() { return true; }

  public void tick() {
    var dist = this.owner.squaredDistanceTo(this.target);
    var canSee = this.owner.getVisibilityCache().canSee(this.target);
    if (canSee) {
      ++this.lastSeenTarget;
    } else {
      this.lastSeenTarget = 0;
    }

    if (dist <= (double)this.sqShootRange && this.lastSeenTarget >= 5) {
      this.owner.getNavigation().stop();
    } else {
      this.owner.getNavigation().startMovingTo(this.target, this.mobSpeed);
    }

    this.owner.getLookControl().lookAt(this.target, 30.0F, 30.0F);
    if (--this.countdown == 0) {
      if (!canSee) {
        return;
      }

      float f = (float)Math.sqrt(dist) / this.shootRange;
      ((ZombieEntityInterface)this.owner)
          .getKind()
          .attack(this.owner, this.target);
      this.countdown =
          MathHelper.floor(f * (float)(this.maxInterval - this.minInterval) +
                           (float)this.minInterval);
    } else if (this.countdown < 0) {
      this.countdown = MathHelper.floor(
          MathHelper.lerp(Math.sqrt(dist) / (double)this.shootRange,
                          (double)this.minInterval, (double)this.maxInterval));
    }
  }
}
