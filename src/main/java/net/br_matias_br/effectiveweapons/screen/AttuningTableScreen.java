package net.br_matias_br.effectiveweapons.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.networking.ItemModificationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class AttuningTableScreen extends HandledScreen<AttuningTableScreenHandler> {

    public AttuningTableScreen(AttuningTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private static final Identifier TEXTURE = Identifier.of(EffectiveWeapons.MOD_ID, "textures/gui/container/attuning_table.png");
    protected static final ButtonTextures BUTTON_TEXTURE = new ButtonTextures(
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/attuning_table_button"),
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/attuning_table_button_disabled"),
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/attuning_table_button_selected"),
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/attuning_table_button_disabled_selected"));
    protected static final ButtonTextures BACK_BUTTON_TEXTURE = new ButtonTextures(
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/back_button"),
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/back_button_disabled"),
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/back_button_selected"),
            Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table/back_button_disabled_selected"));

    protected ButtonWidget resetButton;
    protected TexturedButtonWidget backButton;
    protected AttuningTexturedButtonWidget  passivesButton, metersButton, meterButton1, meterButton2, meterButton3, passiveButton1, passiveButton2, passiveButton3;
    private final ArrayList<ButtonWidget> mainSubscreen = new ArrayList<>();
    private final ArrayList<ButtonWidget> passiveSubscreen = new ArrayList<>();
    private final ArrayList<ButtonWidget> meterSubscreen = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        this.handler.setScreen(this);

        this.resetButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("message.reset"), button -> ClientPlayNetworking.send(new ItemModificationPayload("reset", true)))
                .dimensions(x + 15, y + 19, 36, 20)
                .build());

        this.backButton = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 3, y + 3, 16, 16, BACK_BUTTON_TEXTURE, button -> this.changeSubscreen(0) , null));
        this.backButton.active = false;

        this.passivesButton = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 19, 110, 19, BUTTON_TEXTURE, button -> this.changeSubscreen(1) , Text.translatable("message.passive_ability")));
        this.passivesButton.setTooltip(Tooltip.of(Text.translatable("message.add_passive_ability")));

        this.metersButton = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 59, 110, 19, BUTTON_TEXTURE, button -> this.changeSubscreen(2) , Text.translatable("message.meter_ability")));
        this.metersButton.setTooltip(Tooltip.of(Text.translatable("message.add_meter_ability")));

        this.meterButton1 = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 19, 110, 19, BUTTON_TEXTURE, button -> this.sendComponentRequest(((AttuningTexturedButtonWidget)button).getAbilityKey()) , Text.literal("---")));
        this.meterButton1.setTooltip(Tooltip.of(Text.literal("---")));
        this.meterButton1.active = false;

        this.meterButton2 = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 39, 110, 19, BUTTON_TEXTURE, button -> this.sendComponentRequest(((AttuningTexturedButtonWidget)button).getAbilityKey()) , Text.literal("---")));
        this.meterButton2.setTooltip(Tooltip.of(Text.literal("---")));
        this.meterButton2.active = false;

        this.meterButton3 = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 59, 110, 19, BUTTON_TEXTURE, button -> this.sendComponentRequest(((AttuningTexturedButtonWidget)button).getAbilityKey()) , Text.literal("---")));
        this.meterButton3.setTooltip(Tooltip.of(Text.literal("---")));
        this.meterButton3.active = false;

        this.passiveButton1 = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 19, 110, 19, BUTTON_TEXTURE, button -> this.sendComponentRequest(((AttuningTexturedButtonWidget)button).getAbilityKey()) , Text.literal("---")));
        this.passiveButton1.setTooltip(Tooltip.of(Text.literal("---")));
        this.passiveButton1.active = false;

        this.passiveButton2 = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 39, 110, 19, BUTTON_TEXTURE, button -> this.sendComponentRequest(((AttuningTexturedButtonWidget)button).getAbilityKey()) , Text.literal("---")));
        this.passiveButton2.setTooltip(Tooltip.of(Text.literal("---")));
        this.passiveButton2.active = false;

        this.passiveButton3 = this.addDrawableChild(new AttuningTexturedButtonWidget(x + 58, y + 59, 110, 19, BUTTON_TEXTURE, button -> this.sendComponentRequest(((AttuningTexturedButtonWidget)button).getAbilityKey()) , Text.literal("---")));
        this.passiveButton3.setTooltip(Tooltip.of(Text.literal("---")));
        this.passiveButton3.active = false;

        mainSubscreen.add(passivesButton);
        mainSubscreen.add(metersButton);

        passiveSubscreen.add(passiveButton1);
        passiveSubscreen.add(passiveButton2);
        passiveSubscreen.add(passiveButton3);

        meterSubscreen.add(meterButton1);
        meterSubscreen.add(meterButton2);
        meterSubscreen.add(meterButton3);

        this.changeSubscreen(0);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth + 33, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public void sendComponentRequest(String componentKey){
        ClientPlayNetworking.send(new ItemModificationPayload(componentKey, false));
    }
    protected void changeSubscreen(int subScreen){
        this.backButton.active = subScreen != 0;
        for(int i = 0; i < 3; i++){
            if(i != subScreen){
                for(ButtonWidget button : subScreenFromIndex(i)){
                    button.visible = false;
                }
            }
        }
        for(ButtonWidget buttonWidget : subScreenFromIndex(subScreen)){
            buttonWidget.visible = true;
        }
    }

    protected ArrayList<ButtonWidget> subScreenFromIndex(int index){
        return switch (index) {
            case 0 -> mainSubscreen;
            case 1 -> passiveSubscreen;
            case 2 -> meterSubscreen;
            default -> mainSubscreen;
        };
    }
}
