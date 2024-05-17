package de.cfp.createcards.client;

import de.cfp.createcards.CreateCards;
import de.cfp.createcards.screen.CardInscriberScreen;
import de.cfp.createcards.screen.CardReaderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class CreateCardsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(CreateCards.CARD_INSCRIBER_SCREEN_HANDLER, CardInscriberScreen::new);
        HandledScreens.register(CreateCards.CARD_READER_SCREEN_HANDLER, CardReaderScreen::new);
    }
}
