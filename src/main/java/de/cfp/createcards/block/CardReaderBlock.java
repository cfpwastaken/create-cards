package de.cfp.createcards.block;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import de.cfp.createcards.CreateCards;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CardReaderBlock extends HorizontalFacingBlock implements IWrenchable, BlockEntityProvider {
    public static final BooleanProperty POWERING = BooleanProperty.of("powering");

    public CardReaderBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(POWERING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
        builder.add(POWERING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if((boolean)state.get(POWERING)) {
            return 15;
        } else {
            return 0;
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            CardReaderBlockEntity blockEntity = (CardReaderBlockEntity) world.getBlockEntity(pos);
            if(blockEntity == null) {
                return ActionResult.FAIL;
            }
            if(player.getMainHandStack().getItem().getName().equals(Text.translatable("item.create.wrench"))) {
                CreateCards.IDType idtype = CreateCards.getIDType(player.getOffHandStack().getItem());
                if (!(CreateCards.isIDValid(idtype))) {
                    return ActionResult.FAIL;
                }
                UUID id = player.getOffHandStack().getOrCreateNbt().getUuid("id");
                if(blockEntity.cards.contains(id.toString())) {
                    blockEntity.cards.remove(id.toString());
                    player.sendMessage(Text.translatable("block.create_cards.card_reader.removed_id"));
                    return ActionResult.SUCCESS;
                }
                blockEntity.cards.add(id.toString());
                player.sendMessage(Text.translatable("block.create_cards.card_reader.added_id"));
                return ActionResult.SUCCESS;
            }
            CreateCards.IDType idtype = CreateCards.getIDType(player.getMainHandStack().getItem());
            if (!(CreateCards.isIDValid(idtype))) {
                return ActionResult.FAIL;
            }
            if (state.get(POWERING).equals(true)) {
                return ActionResult.FAIL;
            }
            UUID uuid = player.getMainHandStack().getOrCreateNbt().getUuid("owner");
            UUID id = player.getMainHandStack().getOrCreateNbt().getUuid("id");
            String content = player.getMainHandStack().getOrCreateNbt().getString("content");

            //if (player.getUuid().equals(uuid)) {
            if(blockEntity.cards.contains(id.toString())) {
                if(idtype.equals(CreateCards.IDType.TICKET)) {
                    player.getMainHandStack().setCount(0);
                }
                world.setBlockState(pos, state.with(POWERING, true));
                world.scheduleBlockTick(pos, this, 15);
                AllSoundEvents.CONFIRM.playOnServer(world, pos);
            } else {
                AllSoundEvents.DENY.playOnServer(world, pos);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Boolean)state.get(POWERING)) {
            Direction direction = (Direction)state.get(FACING);
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            double e = (double)pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            float g = -5.0F;

            g /= 16.0F;
            double h = (double)(g * (float)direction.getOffsetX());
            double i = (double)(g * (float)direction.getOffsetZ());
            world.addParticle(DustParticleEffect.DEFAULT, d + h, e, f + i, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERING, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CardReaderBlockEntity(pos, state);
    }
}
