package de.cfp.createcards.screen;

import com.simibubi.create.AllSoundEvents;
import de.cfp.createcards.CreateCards;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class CardReaderScreen extends HandledScreen<CardReaderScreenHandler> {

    CardReaderScreenHandler screenHandler;
    private static final Identifier TEXTURE = new Identifier("create_cards", "textures/gui/reader_gui.png");
    private static final Identifier ADD_BUTTON = new Identifier("create_cards", "textures/gui/reader_add.png");
    private static final Identifier ADD_ACTIVE_BUTTON = new Identifier("create_cards", "textures/gui/reader_add_active.png");
    private static final Identifier DELETE_BUTTON = new Identifier("create_cards", "textures/gui/reader_delete.png");
    private static final Identifier DELETE_ACTIVE_BUTTON = new Identifier("create_cards", "textures/gui/reader_delete_active.png");
    private ButtonWidget addButton;
    private ButtonWidget deleteButton;
    private final int BUTTON_WIDTH = 18;
    private final int BUTTON_HEIGHT = 18;
    private final int ADD_BUTTON_X = 50;
    private final int ADD_BUTTON_Y = 22;
    private final int DELETE_BUTTON_X = 148;
    private final int DELETE_BUTTON_Y = 22;

    public CardReaderScreen(CardReaderScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        screenHandler = handler;
    }

    public boolean isMouseWithinArea(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if(isMouseWithinArea((int)mouseX, (int)mouseY, x+30, y+44, 22, 18)) {
            screenHandler.setDelay(screenHandler.getDelay() + (int) amount);
            MinecraftClient.getInstance()
                    .getSoundManager()
                    .play(PositionedSoundInstance.master(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 1.5F + 0.1f * (screenHandler.getDelay() - 1) / (60 - 1)));
            screenHandler.syncData();
        }
        if(isMouseWithinArea((int)mouseX, (int)mouseY, x+55, y+44, 44, 18)) {
            screenHandler.setDelayUnit(screenHandler.getDelayUnit() + (int) amount);
            MinecraftClient.getInstance()
                    .getSoundManager()
                    .play(PositionedSoundInstance.master(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 1.5F + 0.1f * (screenHandler.getDelay() - 1) / (60 - 1)));
            screenHandler.syncData();
        }

        return true;
    }


//    @Override
//    public void mouseMoved(double mouseX, double mouseY) {
//        super.mouseMoved(mouseX, mouseY);
//        int x = (width - backgroundWidth) / 2;
//        int y = (height - backgroundHeight) / 2;
//        if(isMouseWithinArea((int)mouseX, (int)mouseY, x+55, y+44, 44, 18)) {
//            CreateCards.LOGGER.info(Math.random() + "");
//        }
//    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 176, 166);

        boolean isHoveringAddButton = isMouseWithinArea(mouseX, mouseY, x + ADD_BUTTON_X, y + ADD_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        if(!isHoveringAddButton) {
            context.drawTexture(ADD_BUTTON, x + ADD_BUTTON_X, y + ADD_BUTTON_Y, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, 18, 18);
        } else {
            context.drawTexture(ADD_ACTIVE_BUTTON, x + ADD_BUTTON_X, y + ADD_BUTTON_Y, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, 18, 18);
        }

        boolean isHoveringDeleteButton = isMouseWithinArea(mouseX, mouseY, x + DELETE_BUTTON_X, y + DELETE_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        if(!isHoveringDeleteButton) {
            context.drawTexture(DELETE_BUTTON, x + DELETE_BUTTON_X, y + DELETE_BUTTON_Y, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, 18, 18);
        } else {
            context.drawTexture(DELETE_ACTIVE_BUTTON, x + DELETE_BUTTON_X, y + DELETE_BUTTON_Y, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, 18, 18);
        }
        context.drawText(textRenderer, Text.literal(screenHandler.getCards() + " registered"), x + 73, y + 27, Colors.WHITE, true);
        context.drawText(textRenderer, Text.literal(screenHandler.getDelay() + ""), x + 33, y + 49, Colors.WHITE, true);
        context.drawText(textRenderer, Text.literal(delayUnitToString(screenHandler.getDelayUnit())), x + 58, y + 49, Colors.WHITE, true);
    }

    private String delayUnitToString(int unit) {
        return switch (unit) {
            case 0 -> "Ticks";
            case 1 -> "Seconds";
            default -> "";
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 6 - 2;
        playerInventoryTitleY = 72 + 2;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        addButton = ButtonWidget.builder(Text.literal(""), btn -> {
            if(this.client == null) return;
            assert this.client.interactionManager != null;
            this.client.interactionManager.clickButton(((CardReaderScreenHandler)this.handler).syncId, 0);
        }).dimensions(x + ADD_BUTTON_X, y + ADD_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT).tooltip(Tooltip.of(Text.literal("Add"))).build();

        deleteButton = ButtonWidget.builder(Text.literal(""), btn -> {
            if(this.client == null) return;
            assert this.client.interactionManager != null;
            this.client.interactionManager.clickButton(((CardReaderScreenHandler)this.handler).syncId, 1);
        }).dimensions(x + DELETE_BUTTON_X, y + DELETE_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT).tooltip(Tooltip.of(Text.literal("Add"))).build();

        addSelectableChild(addButton); // dont render
        addSelectableChild(deleteButton); // dont render
    }
}
