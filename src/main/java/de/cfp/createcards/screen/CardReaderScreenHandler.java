package de.cfp.createcards.screen;

import com.simibubi.create.AllSoundEvents;
import de.cfp.createcards.CreateCards;
import de.cfp.createcards.block.CardReaderBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CardReaderScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    PropertyDelegate delegates;
    PropertyDelegate posDelegate;

    public CardReaderScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(1), new ArrayPropertyDelegate(3), new ArrayPropertyDelegate(3));
    }

    public CardReaderScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegates, PropertyDelegate posDelegate) {
        super(CreateCards.CARD_READER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        this.delegates = delegates;
        this.posDelegate = posDelegate;
        inventory.onOpen(playerInventory.player);

        this.addProperties(delegates);
        this.addProperties(posDelegate);

        int m;
        int l;

        // ID
        this.addSlot(new Slot(inventory, 0, 31, 23) {
            @Override
            public boolean canInsert(ItemStack stack) {
                CreateCards.IDType idtype = CreateCards.getIDType(stack.getItem());
                if(idtype == CreateCards.IDType.NONE) return false;
                return CreateCards.isIDValid(idtype);
            }
        });

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    public int getCards() {
        return delegates.get(0);
    }

    public int getDelay() {
        return delegates.get(1);
    }

    public int getDelayUnit() {
        return delegates.get(2);
    }

    public void setDelay(int delay) {
        if(delay > 60) {
            delay = 0;
        }
        if(delay < 1) {
            delay = 60;
        }
        delegates.set(1, delay);
    }

    public void setDelayUnit(int delayUnit) {
        if(delayUnit < 0 || delayUnit > 1) {
            return;
        }
        delegates.set(2, delayUnit);
    }

    public BlockPos getPos() {
        return new BlockPos(posDelegate.get(0), posDelegate.get(1), posDelegate.get(2));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.dropInventory(player, inventory);
    }

    public void syncData() {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeBlockPos(getPos());
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("delay", getDelay());
        nbt.putInt("delayUnit", getDelayUnit());
        packet.writeNbt(nbt);
        ClientPlayNetworking.send(CreateCards.UPDATE_CARD_READER_PACKET_ID, packet);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if(id == 0) {
            CreateCards.IDType idtype = CreateCards.getIDType(inventory.getStack(0).getItem());
            if (!(CreateCards.isIDValid(idtype))) {
                return false;
            }
            CardReaderBlockEntity blockEntity = (CardReaderBlockEntity)player.getWorld().getBlockEntity(getPos());
            NbtCompound nbt = inventory.getStack(0).getOrCreateNbt();
            CardReaderBlockEntity.Card card = new CardReaderBlockEntity.Card(nbt.getUuid("owner"), nbt.getString("content"));
            if(blockEntity.cards.contains(card)) {
                blockEntity.cards.remove(card);
                player.sendMessage(Text.translatable("block.create_cards.card_reader.removed_id"));
                blockEntity.serializeNBT();
                return true;
            }
            blockEntity.cards.add(card);
            player.sendMessage(Text.translatable("block.create_cards.card_reader.added_id"));
            blockEntity.serializeNBT();
            return true;
        }
        if(id == 1) {
            CardReaderBlockEntity blockEntity = (CardReaderBlockEntity)player.getWorld().getBlockEntity(getPos());
            for(int i = 0; i < blockEntity.cards.size(); i++) {
                blockEntity.cards.remove(i);
            }
            blockEntity.serializeNBT();
        }
        return true;
    }
}
