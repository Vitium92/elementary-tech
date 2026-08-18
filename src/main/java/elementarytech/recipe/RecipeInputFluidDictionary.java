package elementarytech.recipe;

import java.util.Arrays;
import java.util.List;

import elementarytech.ElementaryTech;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInputFluidDictionary implements IRecipeInputFluid {
    private final String fluidDictionaryName;
    private final int amount;

    public RecipeInputFluidDictionary(String fluidDictionaryName, int amount) {
        this.fluidDictionaryName = fluidDictionaryName;
        this.amount = amount;
    }

    @Override
    public List<FluidStack> getInputs() {
        FluidStack fs = toFluidStack();
        if (fs != null) {
            return Arrays.asList(fs);
        }
        return Arrays.asList(new FluidStack[0]);
    }

    public FluidStack toFluidStack() {
        List<FluidStack> fluids = ElementaryTech.fluidDictionary.getFluids(fluidDictionaryName);
        if (fluids != null && !fluids.isEmpty()) {
            return new FluidStack(fluids.get(0).getFluid(), amount);
        }
        return null;
    }

    public String getFluidDictionaryName() {
        return fluidDictionaryName;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean matches(FluidStack other) {
        if (other == null || other.getFluid() == null) return false;
        String otherName = ElementaryTech.fluidDictionary.getFluidName(other.getFluid());
        return fluidDictionaryName.equals(otherName) && other.amount >= amount;
    }

    public boolean matches(Fluid fluid) {
        if (fluid == null) return false;
        String otherName = ElementaryTech.fluidDictionary.getFluidName(fluid);
        return fluidDictionaryName.equals(otherName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecipeInputFluidDictionary other = (RecipeInputFluidDictionary) obj;
        return amount == other.amount
            && fluidDictionaryName.equals(other.fluidDictionaryName);
    }

    @Override
    public int hashCode() {
        return 31 * fluidDictionaryName.hashCode() + amount;
    }

    @Override
    public String toString() {
        return "RecipeInputFluidDictionary{" +
            "name='" + fluidDictionaryName + '\'' +
            ", amount=" + amount +
            '}';
    }
}
