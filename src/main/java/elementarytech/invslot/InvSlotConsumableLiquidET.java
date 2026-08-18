package elementarytech.invslot;

import org.apache.commons.lang3.mutable.MutableObject;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import elementarytech.util.ETUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class InvSlotConsumableLiquidET extends InvSlotConsumableLiquid {

    public InvSlotConsumableLiquidET(TileEntityInventory base1, String name1, int oldStartIndex1, Access access1,
            int count, InvSide preferredSide1, OpType opType1) {
        super(base1, name1, oldStartIndex1, access1, count, preferredSide1, opType1);
    }

    @Override
    public FluidStack drain(Fluid fluid, int maxAmount, MutableObject<ItemStack> output, boolean simulate) {
        if (output != null) {
            output.setValue(null);
        }
        ItemStack stack = this.get();
        if (stack == null) {
            return null;
        }
        FluidStack fluidStack = ETUtils.getFluidFromItem(stack);
        if (fluidStack == null || (fluid != null && fluidStack.getFluid() != fluid)) {
            return null;
        }
        int amount = Math.min(fluidStack.amount, maxAmount);
        if (!simulate) {
            fluidStack.amount -= amount;
            if (fluidStack.amount <= 0) {
                if (output != null) {
                    output.setValue(ETUtils.getEmptyContainer(stack));
                }
                this.put(null);
            } else {
                ETUtils.setFluidInItem(stack, fluidStack);
            }
        }
        return new FluidStack(fluidStack.getFluid(), amount);
    }
}
