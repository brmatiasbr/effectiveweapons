package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.entity.custom.DekajaEffectEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class DekajaTomeItem extends Item {
    public DekajaTomeItem(Settings settings) {
        super(settings);
    }
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        System.out.println("onStoppedUsing, remainingUseTicks: " + remainingUseTicks);
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = getPullProgress(i);

            if (world instanceof ServerWorld serverWorld) {
                DekajaEffectEntity dekajaEffect = new DekajaEffectEntity(EffectiveWeaponsEntities.DEKAJA_EFFECT_ENTITY_TYPE,
                        world, user, user.getX(), user.getEyeY() - 0.4, user.getZ());
                dekajaEffect.addVelocity(this.getVector(user, 0.75 * f));
                serverWorld.spawnEntity(dekajaEffect);
            }

            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                    1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
            );

            playerEntity.getItemCooldownManager().set(this, 200);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(user.getItemCooldownManager().isCoolingDown(this)){
            return TypedActionResult.fail(stack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        if (f > 3.0F) {
            f = 3.0F;
        }

        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    protected Vec3d getVector(Entity entity, double velocity){
        double vectorX, vectorY, vectorZ, horizontalComponents;
        double pitchRadian = Math.toRadians(entity.getPitch()), yawRadian = Math.toRadians(entity.getYaw());
        vectorY = velocity * -Math.sin(pitchRadian);
        horizontalComponents = velocity * Math.cos(pitchRadian);
        vectorX = horizontalComponents * -Math.sin(yawRadian);
        vectorZ = horizontalComponents * Math.cos(yawRadian);

        return new Vec3d(vectorX, vectorY, vectorZ);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean onCooldown = false;
        float cooldown = 0;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        boolean controlHeld = Screen.hasControlDown();

        if(player != null){
            onCooldown = player.getItemCooldownManager().isCoolingDown(this);
            cooldown = player.getItemCooldownManager().getCooldownProgress(this, 0);
        }

        NumberFormat formatter = new DecimalFormat("#0");

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.dekaja_tome").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.dekaja_tome_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.dekaja_tome_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.dekaja_tome_cooldown").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.dekaja_tome_cooldown_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.dekaja_tome_summary").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        if(onCooldown) tooltip.add(Text.literal("Remaining cooldown: " + formatter.format(cooldown * 100) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.timeUntilRegen = 0;
        return super.postHit(stack, target, attacker);
    }
}
