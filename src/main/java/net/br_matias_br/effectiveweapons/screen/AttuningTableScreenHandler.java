package net.br_matias_br.effectiveweapons.screen;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.block.EffectiveWeaponsBlocks;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class AttuningTableScreenHandler extends ScreenHandler {
    public static final int RESULT_ID = 0;
    private static final int INPUT_START = 0;
    private static final int INPUT_END = 1;
    private static final int INVENTORY_START = 1;
    private static final int INVENTORY_END = 28;
    private static final int HOTBAR_START = 28;
    private static final int HOTBAR_END = 37;
    private final RecipeInputInventory input = new CraftingInventory(this, 1, 1);
    private ScreenHandlerContext context;
    private PlayerEntity player;
    private AttuningTableScreen screen;

    public AttuningTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(EffectiveWeaponsScreenHandlers.ATTUNING_TABLE_SCREEN_HANDLER_TYPE, syncId);
        this.context = context;
        this.player = playerInventory.player;

        this.addSlot(new Slot(this.input, 0, 25, 47));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public AttuningTableScreenHandler(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, ScreenHandlerContext.EMPTY);
    }
    @Override
    public void onContentChanged(Inventory inventory) {
        if(this.screen != null){
            ItemStack stack = this.input.getStack(0);

            ArrayList<AttuningTexturedButtonWidget> passiveButtons = new ArrayList<>();
            ArrayList<AttuningTexturedButtonWidget> meterButtons = new ArrayList<>();
            passiveButtons.add(this.screen.passiveButton1);
            passiveButtons.add(this.screen.passiveButton2);
            passiveButtons.add(this.screen.passiveButton3);
            meterButtons.add(this.screen.meterButton1);
            meterButtons.add(this.screen.meterButton2);
            meterButtons.add(this.screen.meterButton3);

            if(stack.getItem() instanceof AttunableItem attunableItem){
                NbtCompound compound = attunableItem.getCompoundOrDefault(stack);
                String stackPassiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
                String stackMeterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
                ArrayList<String> passiveCustomizations = attunableItem.getPossibleAttunedPassiveCustomizations();
                ArrayList<String> meterCustomizations = attunableItem.getPossibleAttunedMeterCustomizations();

                for(AttuningTexturedButtonWidget buttonWidget : passiveButtons) buttonWidget.active = false;
                for(AttuningTexturedButtonWidget buttonWidget : meterButtons) buttonWidget.active = false;

                this.screen.resetButton.active = !stackPassiveAbility.equals(attunableItem.getDefaultPassiveCustomization()) ||
                        !stackMeterAbility.equals(attunableItem.getDefaultMeterCustomization());

                for(int i = 0; i < passiveCustomizations.size(); i++){
                    String passiveAbility = passiveCustomizations.get(i);
                    AttuningTexturedButtonWidget buttonWidget = passiveButtons.get(i);
                    buttonWidget.setAbilityKey(passiveAbility);
                    buttonWidget.active = !passiveAbility.equals(stackPassiveAbility);
                    String translationKey = passiveAbility.replace("effectiveweapons:", "message.");
                    buttonWidget.setMessage(Text.translatable(translationKey));
                    String tooltipTranslationKey = passiveAbility.replace("effectiveweapons:", "tooltip.").concat("_explanation");
                    buttonWidget.setTooltip(Tooltip.of(Text.translatable(tooltipTranslationKey)));
                }

                for(int i = 0; i < meterCustomizations.size(); i++){
                    String meterAbility = meterCustomizations.get(i);
                    AttuningTexturedButtonWidget buttonWidget = meterButtons.get(i);
                    buttonWidget.setAbilityKey(meterAbility);
                    buttonWidget.active = !meterAbility.equals(stackMeterAbility);
                    String translationKey = meterAbility.replace("effectiveweapons:", "message.");
                    buttonWidget.setMessage(Text.translatable(translationKey));
                    String tooltipTranslationKey = meterAbility.replace("effectiveweapons:", "tooltip.").concat("_explanation");
                    buttonWidget.setTooltip(Tooltip.of(Text.translatable(tooltipTranslationKey)));
                }
            }
            else{
                this.screen.resetButton.active = false;
                for(AttuningTexturedButtonWidget buttonWidget : passiveButtons){
                    buttonWidget.active = false;
                    buttonWidget.setMessage(Text.literal("-------"));
                    buttonWidget.setTooltip(null);
                }

                for(AttuningTexturedButtonWidget buttonWidget : meterButtons){
                    buttonWidget.active = false;
                    buttonWidget.setMessage(Text.literal("-------"));
                    buttonWidget.setTooltip(null);
                }
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, EffectiveWeaponsBlocks.ATTUNING_TABLE);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == RESULT_ID) {
                this.context.run((world, pos) -> itemStack2.getItem().onCraftByPlayer(itemStack2, world, player));
                if (!this.insertItem(itemStack2, INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot >= INVENTORY_START && slot < HOTBAR_END) {
                if (!this.insertItem(itemStack2, INPUT_START, INPUT_END, false)) {
                    if (slot < INVENTORY_END) {
                        if (!this.insertItem(itemStack2, HOTBAR_START, HOTBAR_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(itemStack2, INVENTORY_START, INVENTORY_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(itemStack2, INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
            if (slot == RESULT_ID) {
                player.dropItem(itemStack2, false);
            }
        }

        return itemStack;
    }

    public void writeCustomization(String componentKey){
        this.context.run((world, pos) -> {
            ItemStack stack = this.input.getStack(0);
            if(!(stack.getItem() instanceof AttunableItem attunableItem)){
                if(!stack.isEmpty()) this.player.sendMessage(Text.translatable("message.attuning_error"));
                return;
            }
            NbtCompound compound = attunableItem.getCompoundOrDefault(stack);

            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
            if(componentKey.contains("passive")) {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attunableItem.getDefaultAttributeModifiers());

                compound.putString(EffectiveWeapons.PASSIVE_ABILITY, componentKey);

                RegistryEntry<EntityAttribute> attribute = EffectiveWeapons.getAttributeOf(componentKey);
                EntityAttributeModifier modifier = EffectiveWeapons.getAttributeValueOf(componentKey);

                if(attribute != null && modifier != null){
                    stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, EffectiveWeapons.getAttributeModifiersOf(componentKey, stack, attunableItem));
                }
            }
            else if(componentKey.contains("meter")){
                compound.putString(EffectiveWeapons.METER_ABILITY, componentKey);
            }
            if(!compound.getString(EffectiveWeapons.METER_ABILITY).equals(EffectiveWeapons.METER_NONE)){
                compound.putInt(attunableItem.getItemChargeId(), 0);
                stack.setDamage(1001);
            }

            NbtComponent component = NbtComponent.of(compound);
            stack.set(DataComponentTypes.CUSTOM_DATA, component);
        });
    }

    public void revertItemToDefault(){
        this.context.run((world, pos) -> {
            ItemStack stack = this.input.getStack(0);
            if(!(stack.getItem() instanceof AttunableItem attunableItem)){
                this.player.sendMessage(Text.translatable("message.attuning_error"));
                return;
            }
            NbtCompound compound = attunableItem.getCompoundOrDefault(stack);

            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);

            compound.putString(EffectiveWeapons.PASSIVE_ABILITY, attunableItem.getDefaultPassiveCustomization());
            compound.putString(EffectiveWeapons.METER_ABILITY, attunableItem.getDefaultMeterCustomization());

            compound.putInt(attunableItem.getItemChargeId(), 0);
            stack.setDamage(attunableItem.getDefaultDurabilityDamage());

            NbtComponent component = NbtComponent.of(compound);
            stack.set(DataComponentTypes.CUSTOM_DATA, component);

            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attunableItem.getDefaultAttributeModifiers());
        });
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return super.canInsertIntoSlot(stack, slot);
    }

    public void setScreen(AttuningTableScreen screen){
        this.screen = screen;
    }
}
