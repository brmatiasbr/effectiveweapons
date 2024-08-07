package net.br_matias_br.effectiveweapons.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AttuningTexturedButtonWidget extends TexturedButtonWidget {
    protected String abilityKey = "effectiveweapons:passive_featherweight"; // Fallback
    public AttuningTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, Text text) {
        super(x, y, width, height, textures, pressAction, text);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        super.renderWidget(context, mouseX, mouseY, delta);
        if(this.getMessage() != null){
            int i = this.active ? 16777215 : 10526880;
            this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    public void setAbilityKey(String key){
        abilityKey = key;
    }
    public String getAbilityKey(){
        return abilityKey;
    }
}
