package de.cfp.createcards.item;

import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TicketItem extends Item {

    public TicketItem(Settings settings) {
        super(settings);
    }

    public float getUsage(ItemStack stack) {
        if (!stack.hasNbt())
            return -1;
        NbtCompound tag = stack.getNbt();
        if (!tag.contains("usage"))
            return -1;
        return tag.getFloat("usage");
    }

    public float getUses(ItemStack stack) {
        if (!stack.hasNbt())
            return -1;
        NbtCompound tag = stack.getNbt();
        if (!tag.contains("uses"))
            return -1;
        return tag.getFloat("uses");
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        float usage = getUsage(stack);
        float uses = getUses(stack);
        return usage < uses && uses != -1.0F;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        double ratio = (double) getUsage(stack) / getUses(stack);

        // Ensure that the ratio is within the range of 0 to 1
        ratio = 1 - Math.min(Math.max(ratio, 0.0), 1.0);
        return (int) Math.round(ratio * 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        double ratio = (double) getUsage(stack) / getUses(stack);

        // Ensure that the ratio is within the range of 0 to 1
        ratio = 1 - Math.min(Math.max(ratio, 0.0), 1.0);
        return Color.mixColors(0xFF_FFC074, 0xFF_46FFE0, (float) ratio);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(isItemBarVisible(stack)) {
            tooltip.add(Text.literal((int)getUsage(stack) + "/" + (int)getUses(stack)));
            tooltip.add(Text.literal((int)(getUses(stack) - getUsage(stack)) + " left"));
        } else {
            tooltip.add(Text.literal("Used Ticket"));
        }
    }
}
