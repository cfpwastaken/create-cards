package de.cfp.createcards.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import de.cfp.createcards.CreateCards;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CardInscriberBlock extends HorizontalFacingBlock implements IWrenchable, BlockEntityProvider {
    public CardInscriberBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
//            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);

            if(screenHandlerFactory == null) {
                CreateCards.LOGGER.error("screenHandlerFactory is null");
                return ActionResult.FAIL;
            }

            player.openHandledScreen(screenHandlerFactory);
//            CreateCards.IDType idtype = CreateCards.getIDType(player.getMainHandStack().getItem());
//            if(!(CreateCards.isIDEmpty(idtype))) {
//                return ActionResult.FAIL;
//            }
//            ItemStack card = new ItemStack(idtype == CreateCards.IDType.EMPTY_CARD ? CreateCards.CARD : CreateCards.TICKET);
//            card.getOrCreateNbt().putUuid("owner", player.getUuid());
//            card.getOrCreateNbt().putUuid("id", UUID.randomUUID());
//            card.getOrCreateNbt().putString("content", "CARD CONTENT");
//            player.setStackInHand(hand, card);
//            world.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1, 1);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CardInscriberBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof CardInscriberBlockEntity) {
                ItemScatterer.spawn(world, pos, (CardInscriberBlockEntity)entity);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
