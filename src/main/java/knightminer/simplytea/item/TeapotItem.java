package knightminer.simplytea.item;

import knightminer.simplytea.core.Config;
import knightminer.simplytea.core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public class TeapotItem extends TooltipItem {

	public TeapotItem(Properties props) {
		super(props);
	}

	// using onItemUseFirst() instead of use() because many fluid tanks will consume the interaction before it gets to use()
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
		Level world = context.getLevel();
		if (world.isClientSide) {
        	return InteractionResult.PASS;
		}

		Player player = context.getPlayer();
		// Even though the context already has a blockhitresult, it does not include fluid blocks
		BlockHitResult rayTrace = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
		if (rayTrace.getType() == Type.BLOCK) {
			BlockPos pos = rayTrace.getBlockPos();
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			Direction side = rayTrace.getDirection();

			// unable to modify the block
			if (!world.mayInteract(player, pos) || !player.mayUseItemAt(pos.relative(side), side, stack)) {
				return InteractionResult.PASS;
			}

			// cauldron disabled in config
			if (block instanceof AbstractCauldronBlock && !Config.SERVER.teapot.fillFromCauldron()) {
				return InteractionResult.PASS;
			}

			Optional<Item> filledItem = Optional.empty();
			Optional<SoundEvent> pickupSound = Optional.empty();
			FluidState fluidState = state.getFluidState();

			if (fluidState.isSource()) {
				Fluid fluid = fluidState.getType();
				// comparing with WATER fluid instead of WATER fluid tag, because many/most modded fluids are tagged as WATER
				// to get water-like interactions even if the fluid is not water
				if (fluid.isSame(Fluids.WATER)) {
					filledItem = Optional.of(Registration.teapot_water);
					pickupSound = ((BucketPickup)block).getPickupSound(state);
					if (!Config.SERVER.teapot.infiniteWater()) {
						((BucketPickup)block).pickupBlock(world, pos, state);
					}
				} else if (fluid.is(Tags.Fluids.MILK)) {
					filledItem = Optional.of(Registration.teapot_milk);
					pickupSound = ((BucketPickup)block).getPickupSound(state);
					((BucketPickup)block).pickupBlock(world, pos, state);
				}
			} else if (Config.SERVER.teapot.fillFromFluidTank()) {
				BlockEntity entity = world.getBlockEntity(pos);
				Optional<IFluidHandler> fluidHandler = Optional.empty();
				if (entity != null) {
					 fluidHandler = entity.getCapability(ForgeCapabilities.FLUID_HANDLER, side).resolve();
				}
				if (fluidHandler.isPresent()) {
					FluidActionResult fillResult = FluidUtil.tryFillContainer(new ItemStack(Items.BUCKET), fluidHandler.get(), FluidType.BUCKET_VOLUME, player, false);
					FluidStack fluidStack = fillResult.success ? FluidUtil.getFluidContained(fillResult.getResult()).orElseGet(() -> FluidStack.EMPTY) : FluidStack.EMPTY;

					if (fluidStack.isEmpty() || fluidStack.getAmount() != FluidType.BUCKET_VOLUME) {
						return InteractionResult.PASS;
					}
						
					// comparing with WATER fluid instead of WATER fluid tag, because many/most modded fluids are tagged as WATER
					// to get water-like interactions even if the fluid is not water
					if (fluidStack.getFluid().isSame(Fluids.WATER)) {
						filledItem = Optional.of(Registration.teapot_water);
					} else if (fluidStack.getFluid().is(Tags.Fluids.MILK)) {
						filledItem = Optional.of(Registration.teapot_milk);
					}

					if (filledItem.isPresent()) {
						FluidUtil.tryFillContainer(new ItemStack(Items.BUCKET), fluidHandler.get(), FluidType.BUCKET_VOLUME, player, true);
					}

				}
			}

			if (filledItem.isPresent()) {
				if (pickupSound.isPresent()) {
					player.playSound(pickupSound.get(), 1.0f, 1.0f);
				}
				player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack.copy(), player, new ItemStack(filledItem.get())));
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		// only work if the teapot is empty and right clicking a cow
		if(Config.SERVER.teapot.canMilkCows() && target instanceof Cow) {
			// sound
			player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);

			// fill with milk
			player.setItemInHand(hand, ItemUtils.createFilledResult(stack.copy(), player, new ItemStack(Registration.teapot_milk)));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}