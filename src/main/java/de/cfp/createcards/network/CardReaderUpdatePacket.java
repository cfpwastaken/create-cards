package de.cfp.createcards.network;

import de.cfp.createcards.CreateCards;
import de.cfp.createcards.block.CardReaderBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CardReaderUpdatePacket {

    public static void handler() {
        ServerPlayNetworking.registerGlobalReceiver(CreateCards.UPDATE_CARD_READER_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            NbtCompound nbt = buf.readNbt();
            if(pos == null || nbt == null || !nbt.contains("delay") || !nbt.contains("delayUnit")) {
                return;
            }

            server.execute(() -> {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
                assert blockEntity != null;
                if(blockEntity instanceof CardReaderBlockEntity cardReaderBlockEntity) {
                    if(cardReaderBlockEntity.owner != player.getUuid()) {
                        return;
                    }
                    cardReaderBlockEntity.delay = nbt.getInt("delay");
                    cardReaderBlockEntity.delayUnit = nbt.getInt("delayUnit");
                } else {
                    CreateCards.LOGGER.error("Block entity is of type " + blockEntity.getClass().getName());
                }
            });
        });
    }

}
