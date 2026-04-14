package ru.ke4e.armorweight.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.ke4e.armorweight.ArmorWeightCalculator;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@ModifyArg(
		method = "tickMovement",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"
		),
		index = 0
	)
	private Vec3d armorweight$applyArmorWeightToMovement(Vec3d movementInput) {
		if (!((Object)this instanceof PlayerEntity player)) {
			return movementInput;
		}

		float multiplier = ArmorWeightCalculator.getSpeedMultiplier(player);
		if (multiplier >= 1.0F) {
			return movementInput;
		}

		return new Vec3d(movementInput.x * multiplier, movementInput.y, movementInput.z * multiplier);
	}
}
