package de.cfp.createcards.screen;

import de.cfp.createcards.CreateCards;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.InkSacItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;

import java.util.UUID;

public class CardInscriberScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public CardInscriberScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(4));
    }

    public CardInscriberScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(CreateCards.CARD_INSCRIBER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 4);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        //This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        int m;
        int l;
        //Our inventory
//        for (m = 0; m < 3; ++m) {
//            for (l = 0; l < 3; ++l) {
//                this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
//            }
//        }
        // Empty ID
        this.addSlot(new Slot(inventory, 0, 14, 20) {
            @Override
            public boolean canInsert(ItemStack stack) {
                CreateCards.IDType idtype = CreateCards.getIDType(stack.getItem());
                if(idtype == CreateCards.IDType.NONE) return false;
                return CreateCards.isIDEmpty(idtype);
            }
        });
        // Ink Sac
        this.addSlot(new Slot(inventory, 1, 14+18, 20) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof InkSacItem;
            }
        });
        // Name tag
        this.addSlot(new Slot(inventory, 2, 14+18+18+9, 20) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof NameTagItem;
            }
        });
        // Output
        this.addSlot(new Slot(inventory, 3, 120, 49) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
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

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        CreateCards.LOGGER.info("CLICK");
        if(!(inventory.getStack(1).getItem() instanceof InkSacItem)) {
            return false;
        }
        if(!(inventory.getStack(2).getItem() instanceof NameTagItem)) {
            return false;
        }
        CreateCards.IDType idtype = CreateCards.getIDType(inventory.getStack(0).getItem());
        if(!(CreateCards.isIDEmpty(idtype))) {
            return false;
        }
        String content = inventory.getStack(2).getName().getString();
        if(!inventory.getStack(2).hasCustomName()) {
            content = "";
        }
        ItemStack card = new ItemStack(idtype == CreateCards.IDType.EMPTY_CARD ? CreateCards.CARD : CreateCards.TICKET);
        card.getOrCreateNbt().putUuid("owner", player.getUuid());
        card.getOrCreateNbt().putUuid("id", UUID.randomUUID());
        card.getOrCreateNbt().putString("content", content);
        if(content != "") {
            card.setCustomName(Text.literal(content));
        }
        inventory.removeStack(0);
        inventory.removeStack(1, 1);
        inventory.setStack(3, card);
        player.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1, 1);
        return true;
    }
}
