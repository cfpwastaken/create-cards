package de.cfp.createcards.block;

import de.cfp.createcards.CreateCards;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class CardReaderBlockEntity extends BlockEntity {

    public static class Card {
        public final UUID owner;
        public final String content;

        public Card(UUID owner, String content) {
            this.owner = owner;
            this.content = content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Card card)) return false;
            return Objects.equals(owner, card.owner) && Objects.equals(content, card.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, content);
        }
    }

    public ArrayList<Card> cards = new ArrayList<>();
    public UUID owner = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public CardReaderBlockEntity(BlockPos pos, BlockState state) {
        super(CreateCards.CARD_READER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putUuid("owner", owner);

        NbtList cardlist = new NbtList();

        for(int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            NbtCompound tag = new NbtCompound();
            tag.putString("" + i, card.owner.toString() + ";#" + card.content);
            cardlist.add(tag);
        }

        nbt.put("cards", cardlist);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        owner = nbt.getUuid("owner");

        NbtList cardlist = nbt.getList("cards", 10);
        cards = new ArrayList<>();

        for(int i = 0; i < cardlist.size(); i++) {
            NbtCompound tag = cardlist.getCompound(i);
            String cardstring = tag.getString("" + i);
            String[] splitter = cardstring.split(";", 2);
            cards.add(new Card(UUID.fromString(splitter[0]), splitter[1].replace("#", "")));
        }
    }

}
