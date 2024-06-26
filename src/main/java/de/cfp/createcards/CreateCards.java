package de.cfp.createcards;

import de.cfp.createcards.block.CardInscriberBlock;
import de.cfp.createcards.block.CardInscriberBlockEntity;
import de.cfp.createcards.block.CardReaderBlock;
import de.cfp.createcards.block.CardReaderBlockEntity;
import de.cfp.createcards.item.TicketItem;
import de.cfp.createcards.network.CardReaderUpdatePacket;
import de.cfp.createcards.screen.CardInscriberScreenHandler;
import de.cfp.createcards.screen.CardReaderScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCards implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("create_cards");
    public static final Block CARD_INSCRIBER_BLOCK = new CardInscriberBlock(FabricBlockSettings.create().strength(4.0f));
    public static final BlockEntityType<CardInscriberBlockEntity> CARD_INSCRIBER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("create_cards", "card_inscriber_block_entity"), FabricBlockEntityTypeBuilder.create(CardInscriberBlockEntity::new, CARD_INSCRIBER_BLOCK).build());
    public static final Block CARD_READER_BLOCK = new CardReaderBlock(FabricBlockSettings.create().strength(4.0f));
    public static final BlockEntityType<CardReaderBlockEntity> CARD_READER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("create_cards", "card_reader_block_entity"), FabricBlockEntityTypeBuilder.create(CardReaderBlockEntity::new, CARD_READER_BLOCK).build());
    public static final Item EMPTY_CARD = new Item(new FabricItemSettings().maxCount(1));
    public static final Item CARD = new Item(new FabricItemSettings().maxCount(1));
    public static final Item EMPTY_TICKET = new Item(new FabricItemSettings().maxCount(1));
    public static final TicketItem TICKET = new TicketItem(new FabricItemSettings().maxCount(1));
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(EMPTY_CARD))
            .displayName(Text.literal("Create: Cards"))
            .entries((ctx, entries) -> {
                entries.add(EMPTY_CARD);
                entries.add(EMPTY_TICKET);
                entries.add(CARD_INSCRIBER_BLOCK);
                entries.add(CARD_READER_BLOCK);
            }).build();
    public static final ScreenHandlerType<CardInscriberScreenHandler> CARD_INSCRIBER_SCREEN_HANDLER;
    public static final ScreenHandlerType<CardReaderScreenHandler> CARD_READER_SCREEN_HANDLER;
    public static final Identifier UPDATE_CARD_READER_PACKET_ID = new Identifier("create_cards", "update_card_reader");

    static {
//        CARD_INSCRIBER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("create_cards", "card_inscriber_screen_handler"), CardInscriberScreenHandler::new);
        CARD_INSCRIBER_SCREEN_HANDLER = new ScreenHandlerType<>(CardInscriberScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
        CARD_READER_SCREEN_HANDLER = new ScreenHandlerType<>(CardReaderScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing!");
        Registry.register(Registries.ITEM_GROUP, new Identifier("create_cards", "item_group"), ITEM_GROUP);
        Registry.register(Registries.BLOCK, new Identifier("create_cards", "card_inscriber"), CARD_INSCRIBER_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("create_cards", "card_inscriber"), new BlockItem(CARD_INSCRIBER_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("create_cards", "card_reader"), CARD_READER_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("create_cards", "card_reader"), new BlockItem(CARD_READER_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier("create_cards", "empty_card"), EMPTY_CARD);
        Registry.register(Registries.ITEM, new Identifier("create_cards", "card"), CARD);
        Registry.register(Registries.ITEM, new Identifier("create_cards", "empty_ticket"), EMPTY_TICKET);
        Registry.register(Registries.ITEM, new Identifier("create_cards", "ticket"), TICKET);
        Registry.register(Registries.SCREEN_HANDLER, new Identifier("create_cards", "inscriber"), CARD_INSCRIBER_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new Identifier("create_cards", "reader"), CARD_READER_SCREEN_HANDLER);
        CardReaderUpdatePacket.handler();
    }

    public enum IDType {
        EMPTY_CARD, EMPTY_TICKET, CARD, TICKET, NONE;
    }

    public static IDType getIDType(Item item) {
        Text name = item.getName();
        if(name.equals(Text.translatable("item.create_cards.empty_card"))) {
            return IDType.EMPTY_CARD;
        } else if(name.equals(Text.translatable("item.create_cards.card"))) {
            return IDType.CARD;
        } else if(name.equals(Text.translatable("item.create_cards.empty_ticket"))) {
            return IDType.EMPTY_TICKET;
        } else if(name.equals(Text.translatable("item.create_cards.ticket"))) {
            return IDType.TICKET;
        }
        return IDType.NONE;
    }

    public static boolean isIDEmpty(IDType type) {
        return type.toString().startsWith("EMPTY_");
    }

    public static boolean isIDValid(IDType type) {
        if(type == IDType.NONE) {
            return false;
        }
        if(type.toString().startsWith("EMPTY_")) {
            return false;
        }
        return true;
    }

}
