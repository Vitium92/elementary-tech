package elementarytech.machines.evaporator;

import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elementarytech.recipe.UniversalRecipeInput;
import elementarytech.recipe.UniversalRecipeManager;
import elementarytech.recipe.UniversalRecipeOutput;
import ic2.api.item.IC2Items;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatorTileEntity extends TileEntityLiquidTankInventory implements IHasGui {

    public final InvSlotOutput outputSlot;
    public InvSlot fuelSlot;
    public final InvSlotConsumableLiquid fluidItemsSlot;
    public final InvSlotConsumableLiquid fillItemsSlot;
    public final InvSlotOutput emptyFluidItemsSlot;

    public short progress = 0;
    public short maxProgress = 450;
    public int fuel = 0;
    public int maxFuel = 0;
    protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager();

    public EvaporatorTileEntity() {
        super(8);
        this.outputSlot = new InvSlotOutput(this, "output", 0, 1);
        this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, 1, true);
        this.fluidItemsSlot = new InvSlotConsumableLiquid(this, "drainInput", 2, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
        this.fillItemsSlot = new InvSlotConsumableLiquid(this, "fillInput", 4, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Fill);
        this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 3, 1);
    }

    public static void init() {
        addRecipe(new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"), 200), IC2Items.getItem("resin"));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);

        try {
            this.fuel = nbttagcompound.getInteger("fuel");
        } catch (Throwable var4) {
            this.fuel = nbttagcompound.getShort("fuel");
        }

        try {
            this.maxFuel = nbttagcompound.getInteger("maxFuel");
        } catch (Throwable var3) {
            this.maxFuel = nbttagcompound.getShort("maxFuel");
        }

        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("fuel", this.fuel);
        nbttagcompound.setInteger("maxFuel", this.maxFuel);
        nbttagcompound.setShort("progress", this.progress);
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != (short) side && side != 0 && side != 1;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        if (this.outputSlot.get() != null)
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, this.outputSlot.get()));
        if (this.fuelSlot.get() != null)
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, this.fuelSlot.get()));
        if (this.emptyFluidItemsSlot.get() != null)
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, this.emptyFluidItemsSlot.get()));
        return new ItemStack(elementarytech.ElementaryTech.evaporatorBlock, 1);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / maxProgress;
    }

    public int gaugeFuelScaled(int i) {
        if (this.maxFuel == 0) {
            this.maxFuel = this.fuel;
            if (this.maxFuel == 0) {
                this.maxFuel = 160;
            }
        }
        return this.fuel * i / this.maxFuel;
    }

    public boolean enableUpdateEntity() {
        return IC2.platform.isSimulating();
    }

    @Override
    public void updateEntityServer() {
        super.updateEntityServer();
        if (this.needsFluid()) {
            handleFluidSlotsBehaviour();
        }
        if (this.fuel <= 0 && this.canOperate() && this.fuelSlot instanceof InvSlotConsumableFuel) {
            this.fuel = this.maxFuel = ((InvSlotConsumableFuel) this.fuelSlot).consumeFuel();
        }

        if (this.isBurning() && this.canOperate()) {
            ++this.progress;

            if (this.progress >= maxProgress) {
                this.progress = 0;
                this.operate();
            }
        } else {
            this.progress = 0;
        }

        if (this.fuel > 0) {
            --this.fuel;
        }

        if (this.getActive() != this.isBurning()) {
            this.setActive(this.isBurning());
        }
    }

    protected void handleFluidSlotsBehaviour() {
        if (this.fluidItemsSlot.get() != null && this.emptyFluidItemsSlot.get() == null) {
            net.minecraftforge.fluids.FluidStack fluid = net.minecraftforge.fluids.FluidContainerRegistry.getFluidForFilledItem(this.fluidItemsSlot.get());
            if (fluid != null) {
                int filled = this.fluidTank.fill(fluid, true);
                if (filled > 0) {
                    ItemStack fluidItem = this.fluidItemsSlot.get();
                    fluidItem.stackSize--;
                    if (fluidItem.stackSize <= 0) {
                        this.fluidItemsSlot.put(null);
                    }
                    this.emptyFluidItemsSlot.put(new ItemStack(net.minecraft.init.Items.bucket));
                }
            }
        }
        if (this.fillItemsSlot.get() != null && this.fluidTank.getFluid() != null) {
            ItemStack filledContainer = net.minecraftforge.fluids.FluidContainerRegistry.fillFluidContainer(this.fluidTank.getFluid(), this.fillItemsSlot.get());
            if (filledContainer != null) {
                this.fluidTank.drain(this.fluidTank.getFluidAmount(), true);
                ItemStack fillItem = this.fillItemsSlot.get();
                fillItem.stackSize--;
                if (fillItem.stackSize <= 0) {
                    this.fillItemsSlot.put(null);
                }
                this.emptyFluidItemsSlot.put(filledContainer);
            }
        }
    }

    public void operate() {
        UniversalRecipeOutput output = this.getOutput();
        if (output != null && output.hasItemOutput()) {
            this.outputSlot.put(output.getItemOutputStacks().get(0).copy());
        }
        FluidStack input = getRecipeInputFluid();
        if (input != null) {
            this.fluidTank.drain(input.amount, true);
        }
    }

    protected FluidStack getRecipeInputFluid() {
        if (this.fluidTank.getFluid() == null) return null;
        UniversalRecipeInput input = new UniversalRecipeInput(this.fluidTank.getFluid());
        UniversalRecipeOutput output = recipeManager.getRecipeFor(input);
        if (output != null && output.hasFluidOutput()) {
            return output.getFirstFluidOutput();
        }
        return null;
    }

    public boolean isBurning() {
        return this.fuel > 0;
    }

    public boolean canOperate() {
        if (this.fluidTank.getFluid() == null) {
            return false;
        }
        UniversalRecipeOutput output = getOutput();
        if (output == null || !output.hasItemOutput()) return false;
        for (ItemStack stack : output.getItemOutputStacks()) {
            if (!this.outputSlot.canAdd(java.util.Collections.singletonList(stack))) return false;
        }
        return true;
    }

    @Override
    public String getInventoryName() {
        return "Solid fuel evaporator";
    }

    @Override
    public ContainerBase<? extends EvaporatorTileEntity> getGuiContainer(EntityPlayer entityPlayer) {
        return new EvaporatorContainer(entityPlayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new EvaporatorGui(new EvaporatorContainer(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {}

    @Override
    public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
        return false;
    }

    @Override
    public boolean canFill(ForgeDirection arg0, Fluid fluid1) {
        return true;
    }

    public boolean getGui(EntityPlayer player) {
        return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
    }

    public int mX() {
        switch (this.getFacing()) {
            case 4: return -1;
            case 5: return 1;
            default: return 0;
        }
    }

    public int mZ() {
        switch (this.getFacing()) {
            case 2: return -1;
            case 3: return 1;
            default: return 0;
        }
    }

    public static void addRecipe(FluidStack input, ItemStack output) {
        recipeManager.addRecipe(
            new UniversalRecipeInput(input),
            new UniversalRecipeOutput(new elementarytech.recipe.RecipeOutputItemStack(output), 20)
        );
    }

    public UniversalRecipeOutput getOutput() {
        if (this.fluidTank.getFluid() == null) {
            return null;
        }
        UniversalRecipeInput input = new UniversalRecipeInput(this.fluidTank.getFluid());
        UniversalRecipeOutput output = recipeManager.getRecipeFor(input);
        if (output == null) return null;
        if (!output.hasItemOutput()) return null;
        for (ItemStack stack : output.getItemOutputStacks()) {
            if (!this.outputSlot.canAdd(java.util.Collections.singletonList(stack))) return null;
        }
        return output;
    }

    public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
        return null;
    }
}
