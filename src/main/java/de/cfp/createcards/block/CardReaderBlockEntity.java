package de.cfp.createcards.block;

import de.cfp.createcards.CreateCards;
import de.cfp.createcards.inventory.ImplementedInventory;
import de.cfp.createcards.screen.CardReaderScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class CardReaderBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {

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
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public int delay = 15;
    public int delayUnit = 0;

    private final PropertyDelegate delegates = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cards.size();
                case 1 -> delay;
                case 2 -> delayUnit;
                default -> -1;
            };
        }

        @Override
        public void set(int index, int value) {
            CreateCards.LOGGER.info("set delegate");
            if(index == 0) return; // Can't write that
            if(index == 1) { // Delay
                delay = value;
                if(delay > 60) {
                    delay = 0;
                }
                if(delay < 1) {
                    delay = 60;
                }
            }
            if(index == 2) { // Delay Unit
                if(value < 0 || value > 1) {
                    return;
                }
                delayUnit = value;
            }
            serializeNBT();
        }

        @Override
        public int size() {
            return 3;
        }
    };
    public final PropertyDelegate posDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            if(index == 0) {
                return getPos().getX();
            }
            if(index == 1) {
                return getPos().getY();
            }
            if(index == 2) {
                return getPos().getZ();
            }
            return -1;
        }

        @Override
        public void set(int index, int value) {
            // no
        }

        @Override
        public int size() {
            return 3;
        }
    };

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
        nbt.putInt("delay", delay);
        nbt.putInt("delayUnit", delayUnit);

        Inventories.writeNbt(nbt, this.inventory);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        owner = nbt.getUuid("owner");
        delay = nbt.getInt("delay");
        delayUnit = nbt.getInt("delayUnit");

        NbtList cardlist = nbt.getList("cards", 10);
        cards = new ArrayList<>();

        for(int i = 0; i < cardlist.size(); i++) {
            NbtCompound tag = cardlist.getCompound(i);
            String cardstring = tag.getString("" + i);
            String[] splitter = cardstring.split(";", 2);
            cards.add(new Card(UUID.fromString(splitter[0]), splitter[1].replace("#", "")));
        }

        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.create_cards.card_reader");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CardReaderScreenHandler(syncId, playerInventory, this, delegates, posDelegate);
    }

}
