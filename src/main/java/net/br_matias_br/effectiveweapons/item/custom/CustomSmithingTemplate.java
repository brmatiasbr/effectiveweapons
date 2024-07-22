package net.br_matias_br.effectiveweapons.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;

public class CustomSmithingTemplate extends Item {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;

    private static final Text INGREDIENTS_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.ingredients")))
            .formatted(TITLE_FORMATTING);
    private static final Text APPLIES_TO_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.applies_to")))
            .formatted(TITLE_FORMATTING);

    private CustomSmithingTemplateType type = CustomSmithingTemplateType.SPIRALING_SWORD;
    public CustomSmithingTemplate(Settings settings) {
        super(settings);
    }

    public CustomSmithingTemplate(Settings settings, CustomSmithingTemplateType type){
        this(settings);
        this.type = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        this.appendTooltipFromType(this.type, tooltip);
    }

    private void appendTooltipFromType(CustomSmithingTemplateType type, List<Text> tooltip){
        Text titleText = Text.literal("-"), appliesToText = titleText, ingredientsText = Text.translatable("item.minecraft.experience_bottle").formatted(DESCRIPTION_FORMATTING);
        switch (type){
            case SPIRALING_SWORD -> {
                titleText = Text.translatable("tooltip.spiraling_sword_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("item.minecraft.iron_sword").formatted(DESCRIPTION_FORMATTING);
            }
            case ROGUE_DAGGER-> {
                titleText = Text.translatable("tooltip.rogue_dagger_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("tooltip.rogue_dagger_smithing_template_applies_to").formatted(DESCRIPTION_FORMATTING);
            }
            case BLESSED_LANCE -> {
                titleText = Text.translatable("tooltip.blessed_lance_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("tooltip.blessed_lance_smithing_template_applies_to").formatted(DESCRIPTION_FORMATTING);
            }
            case PACT_AXE -> {
                titleText = Text.translatable("tooltip.pact_axe_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("item.minecraft.iron_axe").formatted(DESCRIPTION_FORMATTING);
            }
            case DEKAJA_TOME -> {
                titleText = Text.translatable("tooltip.dekaja_tome_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("item.minecraft.book").formatted(DESCRIPTION_FORMATTING);
            }
            case DOUBLE_BOW -> {
                titleText = Text.translatable("tooltip.double_bow_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("item.minecraft.bow").formatted(DESCRIPTION_FORMATTING);
            }
            case IRON -> {
                titleText = Text.translatable("tooltip.iron_smithing_template_title").formatted(TITLE_FORMATTING);
                appliesToText = Text.translatable("tooltip.iron_smithing_template_applies_to").formatted(DESCRIPTION_FORMATTING);
                ingredientsText = Text.translatable("item.minecraft.iron_nugget").formatted(DESCRIPTION_FORMATTING);
            }
        }

        tooltip.add(titleText);
        tooltip.add(ScreenTexts.EMPTY);
        tooltip.add(APPLIES_TO_TEXT);
        tooltip.add(ScreenTexts.space().append(appliesToText));
        tooltip.add(INGREDIENTS_TEXT);
        tooltip.add(ScreenTexts.space().append(ingredientsText));
    }

    public static enum CustomSmithingTemplateType{
        IRON,
        SPIRALING_SWORD,
        ROGUE_DAGGER,
        BLESSED_LANCE,
        PACT_AXE,
        DEKAJA_TOME,
        DOUBLE_BOW
    }
}
