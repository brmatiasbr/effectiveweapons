package net.br_matias_br.effectiveweapons.block;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.block.custom.AttuningTableBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsBlocks {
    public static final Block ATTUNING_TABLE = registerBlock("attuning_table",
            new AttuningTableBlock(AbstractBlock.Settings.copy(Blocks.SMITHING_TABLE)));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(EffectiveWeapons.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(EffectiveWeapons.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks(){
        EffectiveWeapons.LOGGER.info("Registering Mod Blocks for " + EffectiveWeapons.MOD_ID);
    }
}
