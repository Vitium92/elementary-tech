package elementarytech.nei;

import java.util.List;

import codechicken.nei.PositionedStack;
import elementarytech.recipe.RecipeOutputItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ETPositionedStack extends PositionedStack {

    public final float sQuantity;

    public ETPositionedStack(RecipeOutputItemStack object, int x, int y) {
        super(object.itemStack.copy(), x, y);
        sQuantity = object.itemStack.stackSize;
    }

    public ETPositionedStack(FluidStack fluidStack, int x, int y) {
        super(new ItemStack(Items.bucket), x, y);
        sQuantity = fluidStack.amount / 1000f;
    }

    public ETPositionedStack(List<FluidStack> list, int x, int y) {
        super(new ItemStack(Items.bucket), x, y);
        sQuantity = list.get(0).amount / 1000f;
        this.items = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            this.items[i] = new ItemStack(Items.bucket);
        }
        this.item = this.items[0];
    }
}
