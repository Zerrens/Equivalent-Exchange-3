package com.pahimar.ee3.core.helper;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.pahimar.ee3.core.handlers.EquivalencyHandler;

/**
 * TransmutationHelper
 * 
 * Helper methods for transmutation related things
 * 
 * @author pahimar
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */
public class TransmutationHelper {

    public static ItemStack previousBlockStack = null;
    public static ItemStack currentBlockStack = null;
    public static ItemStack targetBlockStack = null;

    public static boolean transmuteInWorld(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, int targetID, int targetMeta) {

        if (Block.blocksList[targetID] != null) {
            world.setBlockAndMetadataWithNotify(x, y, z, targetID, targetMeta);
            world.spawnParticle("largesmoke", x, y + 1, z, 1.0D, 1.0D, 1.0D);
            return true;
        }

        return false;
    }

    public static String formatTargetBlockInfo(ItemStack targetBlock) {

        if (targetBlock != null) {
            return TransmutationHelper.targetBlockStack.itemID + ":" + TransmutationHelper.targetBlockStack.getItemDamage();
        }
        else {
            return "";
        }
    }

    public static void updateTargetBlock(World world, int x, int y, int z) {

        int id = world.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        Block currentBlock = Block.blocksList[id];

        if (currentBlock != null) {
            meta = currentBlock.damageDropped(meta);
        }

        currentBlockStack = new ItemStack(id, 1, meta);

        if (previousBlockStack == null) {
            previousBlockStack = currentBlockStack;
            targetBlockStack = getNextBlock(currentBlockStack.itemID, currentBlockStack.getItemDamage());
        }
        else {
            if (!EquivalencyHandler.instance().areEquivalent(TransmutationHelper.previousBlockStack, currentBlockStack)) {
                previousBlockStack = currentBlockStack;
                targetBlockStack = getNextBlock(currentBlockStack.itemID, currentBlockStack.getItemDamage());
            }
        }
    }

    public static ItemStack getNextBlock(int id, int meta) {

        ArrayList<ItemStack> list = EquivalencyHandler.instance().getEquivalencyList(id, meta);

        ItemStack nextStack = null;

        if (list != null) {
            return getNextBlock(id, meta, id, meta);
        }
        return nextStack;
    }

    private static ItemStack getNextBlock(int id, int meta, int origId, int origMeta) {

        ArrayList<ItemStack> list = EquivalencyHandler.instance().getEquivalencyList(id, meta);

        ItemStack nextStack = null;

        if (list != null) {
            nextStack = EquivalencyHandler.instance().getNextInList(id, meta);
            nextStack.stackSize = 1;

            /*
             * If the current item is the same as the original one we started
             * with, then we have recursed through the entire list and not found
             * a next block so return the original. This is the "base case" for
             * the recursion.
             */
            if ((nextStack.itemID == origId) && (nextStack.getItemDamage() == origMeta)) {
                return nextStack;
            }
            else {
                if (nextStack.getItem() instanceof ItemBlock) {
                    return nextStack;
                }
                else {
                    return getNextBlock(nextStack.itemID, nextStack.getItemDamage(), origId, origMeta);
                }
            }
        }

        // In the event the list is null, return null
        return nextStack;
    }

}
