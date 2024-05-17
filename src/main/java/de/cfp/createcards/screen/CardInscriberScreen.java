package de.cfp.createcards.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import de.cfp.createcards.CreateCards;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IconButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CardInscriberScreen extends HandledScreen<CardInscriberScreenHandler> {

//    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");
    private static final Identifier TEXTURE = new Identifier("create_cards", "textures/gui/inscriber_gui.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier("create_cards", "textures/gui/card.png");
    private static final Identifier BUTTON_ACTIVE_TEXTURE = new Identifier("create_cards", "textures/gui/card_active.png");
    private ButtonWidget button;

    public CardInscriberScreen(CardInscriberScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public boolean isMouseWithinArea(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 176, 166);

        boolean isHoveringButton = isMouseWithinArea(mouseX, mouseY, x + 58, y + 48, 18, 18);
        if(!isHoveringButton) {
            context.drawTexture(BUTTON_TEXTURE, x + 58, y + 48, 0, 0, 18, 18, 18, 18);
        } else {
            context.drawTexture(BUTTON_ACTIVE_TEXTURE, x + 58, y + 48, 0, 0, 18, 18, 18, 18);
        }
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
//        CreateCards.LOGGER.info("TitleY: " + titleY); // 6
//        CreateCards.LOGGER.info("InvTitleY: " + playerInventoryTitleY); // 72
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 6 - 2;
        playerInventoryTitleY = 72 + 2;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        button = ButtonWidget.builder(Text.literal(""), btn -> {
            if(this.client == null) return;
            assert this.client.interactionManager != null;
            this.client.interactionManager.clickButton(((CardInscriberScreenHandler)this.handler).syncId, 0);
        }).dimensions(x + 58, y + 48, 18, 18).tooltip(Tooltip.of(Text.literal("Inscribe"))).build();

        addSelectableChild(button); // dont render
    }

}
