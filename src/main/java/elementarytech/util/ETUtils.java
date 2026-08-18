package elementarytech.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import elementarytech.ModInfo;
import elementarytech.recipe.IRecipeInputFluid;
import ic2.api.recipe.IRecipeInput;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.oredict.OreDictionary;

public class ETUtils {
    private static java.util.Map<String, ItemStack> itemStackRegistry = new java.util.HashMap<String, ItemStack>();

    public static void registerLocally(String name, ItemStack stack) {
        itemStackRegistry.put(name, stack);
    }

    public static ItemStack getThisModItemStack(String name) {
        if (itemStackRegistry.get(name) != null) {
            return itemStackRegistry.get(name).copy();
        }
        if (GameRegistry.findItem(ModInfo.MODID, name) != null) {
            return new ItemStack(GameRegistry.findItem(ModInfo.MODID, name));
        } else if (GameRegistry.findBlock(ModInfo.MODID, name) == null) {
            throw new IllegalArgumentException("No such item in item registry: " + ModInfo.MODID + ":" + name);
        } else {
            return new ItemStack(GameRegistry.findBlock(ModInfo.MODID, name));
        }
    }

    public static ItemStack getThisModItemStackWithSize(String name, int i) {
        if (itemStackRegistry.get(name) != null) {
            ItemStack stack = itemStackRegistry.get(name).copy();
            stack.stackSize = i;
            return stack;
        }
        if (GameRegistry.findItem(ModInfo.MODID, name) != null) {
            return new ItemStack(GameRegistry.findItem(ModInfo.MODID, name), i);
        } else if (GameRegistry.findBlock(ModInfo.MODID, name) == null) {
            throw new IllegalArgumentException("No such item in item registry: " + ModInfo.MODID + ":" + name);
        } else {
            return new ItemStack(GameRegistry.findBlock(ModInfo.MODID, name), i);
        }
    }

    public static Block getThisModBlock(String name) {
        if (GameRegistry.findBlock(ModInfo.MODID, name) == null) {
            throw new IllegalArgumentException("No such block in block registry: " + ModInfo.MODID + ":" + name);
        } else {
            return GameRegistry.findBlock(ModInfo.MODID, name);
        }
    }

    public static ItemStack getOreDictItemStack(String name) {
        java.util.List<ItemStack> ores = OreDictionary.getOres(name);
        if (ores.isEmpty())
            return null;
        ItemStack ore = ores.get(0);
        if (ore == null)
            return null;
        ItemStack orecopy = ore.copy();
        orecopy.stackSize = 1;
        return orecopy;
    }

    public static boolean hasOreDictionaryEntry(String name) {
        return !OreDictionary.getOres(name).isEmpty();
    }

    public static ItemStack getOreDictItemStackWithSize(String name, int size) {
        java.util.List<ItemStack> ores = OreDictionary.getOres(name);
        if (ores.isEmpty())
            return null;
        ItemStack ore = ores.get(0);
        if (ore == null)
            return null;
        ItemStack orecopy = ore.copy();
        orecopy.stackSize = size;
        return orecopy;
    }

    public static FluidStack getFluidStackWithSize(String name, int i) {
        if (FluidRegistry.isFluidRegistered(name)) {
            return FluidRegistry.getFluidStack(name, i);
        } else {
            throw new IllegalArgumentException("No such fluid: " + name);
        }
    }

    public static FluidStack getFluidStackIfExist(String string, int amount) {
        if (FluidRegistry.isFluidRegistered(string)) {
            return getFluidStackWithSize(string, amount);
        }
        return null;
    }

    public static String getFirstOreDictName(ItemStack stack) {
        int[] arrayIDs = OreDictionary.getOreIDs(stack);
        if (arrayIDs.length > 0) {
            return OreDictionary.getOreName(arrayIDs[0]);
        }
        return "";
    }

    public static String getFirstOreDictName(Fluid fluid) {
        return fluid != null ? fluid.getName() : "";
    }

    public static String getFirstOreDictNameExcludingTagAny(ItemStack stack) {
        int[] arrayIDs = OreDictionary.getOreIDs(stack);
        for (int i = 0; i < arrayIDs.length; i++) {
            if (!OreDictionary.getOreName(arrayIDs[i]).contains("Any")) {
                return OreDictionary.getOreName(arrayIDs[i]);
            }
        }
        return "";
    }

    public static int getAmountOf(ItemStack is) {
        if (is == null) return 0;
        return is.stackSize;
    }

    public static int getAmountOf(ItemStack is, String oreDictName) {
        if (is == null) return 0;
        int[] ids = OreDictionary.getOreIDs(is);
        for (int id : ids) {
            if (OreDictionary.getOreName(id).equals(oreDictName)) {
                return is.stackSize;
            }
        }
        return 0;
    }

    public static boolean isItemStacksIsEqual(ItemStack stack1, ItemStack stack2, boolean useOreDictionary) {
        if (stack2 == null && stack1 == null) {
            return true;
        } else if (stack2 == null || stack1 == null) {
            return false;
        }
        if (useOreDictionary && isItemsHaveSameOreDictionaryEntry(stack1, stack2)) {
            return true;
        } else {
            if (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE
                    || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return stack1.getItem() == stack2.getItem();
            } else {
                return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage();
            }
        }
    }

    public static boolean isItemStacksIsEqual(ItemStack stack1, String stack2name, boolean useOreDictionary) {
        return isItemStacksIsEqual(stack1, getThisModItemStack(stack2name), useOreDictionary);
    }

    public static boolean areItemStacksCompatible(ItemStack template, ItemStack candidate) {
        if (template == null || candidate == null) return false;
        if (isItemStacksIsEqual(template, candidate, true)) return true;
        if (template.getItem() != candidate.getItem()) return false;
        if (template.getItemDamage() != OreDictionary.WILDCARD_VALUE
                && candidate.getItemDamage() != OreDictionary.WILDCARD_VALUE
                && template.getItemDamage() != candidate.getItemDamage()) return false;
        return true;
    }

    public static boolean isItemsHaveSameOreDictionaryEntry(ItemStack is, ItemStack is1) {
        int[] odids1 = OreDictionary.getOreIDs(is);
        int[] odids2 = OreDictionary.getOreIDs(is1);
        if (odids1 != null && odids1.length > 0 && odids2 != null && odids2.length > 0) {
            for (int i1 = 0; i1 < odids1.length; i1++) {
                for (int i2 = 0; i2 < odids2.length; i2++) {
                    if (!OreDictionary.getOreName(odids1[i1]).contains("Any") && odids1[i1] == odids2[i2]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBlockRegisteredInOreDictionaryAs(Block block, String string) {
        Iterator<ItemStack> isoi = OreDictionary.getOres(string).iterator();
        while (isoi.hasNext()) {
            if (Block.getBlockFromItem(isoi.next().getItem()) == block) {
                return true;
            }
        }
        return false;
    }

    public static boolean addItemStackToInventory(EntityPlayer player, ItemStack stack) {
        ItemStack[] inv = player.inventory.mainInventory;
        for (int i = 0; i <= 35; i++) {
            if (inv[i] != null) {
                if (inv[i].getItem() == stack.getItem()) {
                    if (inv[i].getItemDamage() == stack.getItemDamage()
                            && inv[i].stackSize < inv[i].getMaxStackSize()) {
                        inv[i].stackSize += stack.stackSize;
                        if (inv[i].stackSize > inv[i].getMaxStackSize()) {
                            stack.stackSize = inv[i].stackSize - inv[i].getMaxStackSize();
                        } else {
                            return true;
                        }
                    }
                }
            } else {
                inv[i] = stack;
                return true;
            }
        }
        return false;
    }

    public static MovingObjectPosition returnMOPFromPlayer(EntityPlayer entityplayer, World world) {
        float f1 = entityplayer.rotationPitch;
        float f2 = entityplayer.rotationYaw;
        double x = entityplayer.posX;
        double y = entityplayer.posY + entityplayer.getEyeHeight();

        if (world.isRemote) {
            y -= entityplayer.getDefaultEyeHeight();
        }

        double z = entityplayer.posZ;
        Vec3 vec3d = Vec3.createVectorHelper(x, y, z);
        float f3 = MathHelper.cos(-f2 * 0.01745329F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f9 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec3d1 = vec3d.addVector(f7 * d3, f6 * d3, f9 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec3d, vec3d1, true);

        if (movingobjectposition == null) {
            return null;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return movingobjectposition;
        }
        return null;
    }

    public static short getFacingFromPlayerView(EntityLivingBase player, boolean ignoreSneaking) {
        int var6 = MathHelper.floor_double(player.rotationPitch * 4.0F / 360.0F + 0.5D) & 3;
        int var7 = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        {
            if (var6 == 1) {
                return 1;
            } else if (var6 == 3) {
                return 0;
            } else {
                if (player.isSneaking() && !ignoreSneaking) {
                    switch (var7) {
                    case 0:
                        return 3;
                    case 1:
                        return 4;
                    case 2:
                        return 2;
                    case 3:
                        return 5;
                    default:
                        break;
                    }
                } else {
                    switch (var7) {
                    case 0:
                        return 2;
                    case 1:
                        return 5;
                    case 2:
                        return 3;
                    case 3:
                        return 4;
                    default:
                        break;
                    }
                }
            }
        }
        return 3;
    }

    public static List<ItemStack> convertRecipeInputToItemStackList(List<IRecipeInput> input) {
        Iterator<IRecipeInput> irii = input.iterator();
        List<ItemStack> output = new ArrayList<ItemStack>();
        while (irii.hasNext()) {
            IRecipeInput iri = irii.next();
            ItemStack stack = iri.getInputs().get(0);
            stack.stackSize = iri.getAmount();
            output.add(stack);
        }
        return output;
    }

    public static List<FluidStack> convertRecipeInputToFluidStackList(List<IRecipeInputFluid> input) {
        Iterator<IRecipeInputFluid> irii = input.iterator();
        List<FluidStack> output = new ArrayList<FluidStack>();
        while (irii.hasNext()) {
            IRecipeInputFluid iri = irii.next();
            FluidStack stack = iri.getInputs().get(0).copy();
            stack.amount = iri.getAmount();
            output.add(stack);
        }
        return output;
    }

    public static List<ItemStack> getEntryListForOre(String name) {
        ArrayList<ItemStack> outputList = new ArrayList<ItemStack>();
        ArrayList<ItemStack> oreList = OreDictionary.getOres(name);
        Iterator<ItemStack> oreListIterator = oreList.iterator();
        while (oreListIterator.hasNext()) {
            outputList.add(oreListIterator.next().copy());
        }
        return outputList;
    }

    public static ItemStack getItemStackIfExist(String name) {
        if (hasOreDictionaryEntry(name)) {
            return getOreDictItemStack(name);
        } else {
            if (itemStackRegistry.get(name) != null) {
                return itemStackRegistry.get(name).copy();
            }
            if (GameRegistry.findItem(ModInfo.MODID, name) != null) {
                return new ItemStack(GameRegistry.findItem(ModInfo.MODID, name));
            } else if (GameRegistry.findBlock(ModInfo.MODID, name) == null) {
                return null;
            } else {
                return new ItemStack(GameRegistry.findBlock(ModInfo.MODID, name));
            }
        }
    }

    public static ItemStack getThisModItemStackWithDamage(String name, int value) {
        ItemStack stack = getThisModItemStack(name);
        stack.setItemDamage(value);
        return stack;
    }

    public static FluidStack getFluidFromItem(ItemStack stack) {
        if (stack == null) return null;
        if (stack.getItem() instanceof IFluidContainerItem) {
            return ((IFluidContainerItem) stack.getItem()).getFluid(stack);
        }
        return FluidContainerRegistry.getFluidForFilledItem(stack);
    }

    public static ItemStack getEmptyContainer(ItemStack stack) {
        if (stack == null) return null;
        ItemStack drained = FluidContainerRegistry.drainFluidContainer(stack);
        if (drained != null) return drained.copy();
        if (stack.getItem() instanceof IFluidContainerItem) {
            return FluidContainerRegistry.drainFluidContainer(stack);
        }
        return null;
    }

    public static void setFluidInItem(ItemStack stack, FluidStack fluidStack) {
        if (stack == null || !(stack.getItem() instanceof IFluidContainerItem)) return;
        IFluidContainerItem container = (IFluidContainerItem) stack.getItem();
        container.fill(stack, fluidStack, true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void handleFluidSlotsBehaviour(ic2.core.block.invslot.InvSlotConsumableLiquid fillInputSlot,
            ic2.core.block.invslot.InvSlotConsumableLiquid drainInputSlot,
            ic2.core.block.invslot.InvSlotOutput emptyFluidItemsSlot,
            net.minecraftforge.fluids.IFluidTank fluidTank) {
        org.apache.commons.lang3.mutable.MutableObject output;
        if (drainInputSlot != null && !drainInputSlot.isEmpty()) {
            output = new org.apache.commons.lang3.mutable.MutableObject();
            if (fluidTank
                    .fill(drainInputSlot.drain(null, fluidTank.getCapacity() - fluidTank.getFluidAmount(), output,
                            true), false) > 0
                    && (output.getValue() == null || emptyFluidItemsSlot.canAdd((net.minecraft.item.ItemStack) output.getValue()))) {
                fluidTank.fill(
                        drainInputSlot.drain(null, fluidTank.getCapacity() - fluidTank.getFluidAmount(), output, false),
                        true);
                if (output.getValue() != null) {
                    emptyFluidItemsSlot.add((net.minecraft.item.ItemStack) output.getValue());
                }
            }
        }
        if (fillInputSlot != null && !fillInputSlot.isEmpty()) {
            output = new org.apache.commons.lang3.mutable.MutableObject();
            if (fillInputSlot.transferFromTank(fluidTank, output, true)
                    && (output.getValue() == null || emptyFluidItemsSlot.canAdd((net.minecraft.item.ItemStack) output.getValue()))) {
                fillInputSlot.transferFromTank(fluidTank, output, false);
                if (output.getValue() != null) {
                    emptyFluidItemsSlot.add((net.minecraft.item.ItemStack) output.getValue());
                }
            }
        }
    }

    public static net.minecraftforge.fluids.IFluidTank getFluidTankIFluidTank(final elementarytech.util.ETFluidTank tank) {
        return tank;
    }
}
