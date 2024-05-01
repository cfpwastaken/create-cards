package de.cfp.createcards.block;

import de.cfp.createcards.CreateCards;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

public class CardReaderBlockEntity extends BlockEntity {

    public ArrayList<String> cards = new ArrayList<>();

    public CardReaderBlockEntity(BlockPos pos, BlockState state) {
        super(CreateCards.CARD_READER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList cardlist = new NbtList();

        for(int i = 0; i < cards.size(); i++) {
            String card = cards.get(i);
            NbtCompound tag = new NbtCompound();
            tag.putString("" + i, card);
            cardlist.add(tag);
        }

        nbt.put("cards", cardlist);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtList cardlist = nbt.getList("cards", 10);
        cards = new ArrayList<>();

        for(int i = 0; i < cardlist.size(); i++) {
            NbtCompound tag = cardlist.getCompound(i);
            String card = tag.getString("" + i);
            cards.add(card);
        }
    }

}
