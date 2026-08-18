package elementarytech.nei;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import elementarytech.recipe.IRecipeInputFluid;
import elementarytech.recipe.RecipeOutputItemStack;
import elementarytech.recipe.UniversalRecipeInput;
import elementarytech.recipe.UniversalRecipeOutput;
import ic2.api.recipe.IRecipeInput;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public abstract class MachineRecipeHandler extends TemplateRecipeHandler {
    protected int ticks;

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal(getRecipeId());
    }

    public abstract String getRecipeId();

    @Override
    public abstract String getGuiTexture();

    @Override
    public abstract String getOverlayIdentifier();

    public abstract Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList();

    @Override
    public void drawBackground(int i) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 140, 65);
    }

    @Override
    public void drawExtras(int recipeNumber) {
        CachedIORecipe recipe = (CachedIORecipe) this.arecipes.get(recipeNumber);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (PositionedStack stack : recipe.ingredients) {
            if (stack.item.stackSize == 0) {
                GuiDraw.fontRenderer.drawStringWithShadow("0.001", stack.relx + 3, stack.rely + 9, 16777215);
            }
            if (stack instanceof ETPositionedStack)
                drawFormattedString((ETPositionedStack) stack);
        }
        for (PositionedStack stack : recipe.otherStacks) {
            if (stack instanceof ETPositionedStack)
                drawFormattedString((ETPositionedStack) stack);
        }
        if (recipe.output instanceof ETPositionedStack) {
            ETPositionedStack rOutput = (ETPositionedStack) recipe.output;
            drawFormattedString(rOutput);
        }
        GuiDraw.changeTexture(this.getGuiTexture());
    }

    public void drawFormattedString(ETPositionedStack rOutput) {
        if (Math.abs(rOutput.sQuantity - Math.round(rOutput.sQuantity)) < 0.01f) {
            if (Math.round(rOutput.sQuantity) != 1) {
                GuiDraw.fontRenderer.drawStringWithShadow(String.format("%d", Math.round(rOutput.sQuantity)),
                        rOutput.relx + 11, rOutput.rely + 9, 16777215);
            }
        } else {
            GuiDraw.fontRenderer.drawStringWithShadow(String.format("%.1f", rOutput.sQuantity), rOutput.relx + 3,
                    rOutput.rely + 9, 16777215);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        ++this.ticks;
    }

    @Override
    public void loadTransferRects() {
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.getRecipeList().entrySet()
                    .iterator();

            while (i$.hasNext()) {
                Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue(),
                        getAdditionalIngredients()));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(result);
        if (fluidStack == null && result.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem ifc = (IFluidContainerItem) result.getItem();
            fluidStack = ifc.getFluid(result);
        } else if (result.getItem() instanceof ItemBlock) {
            Block blockfluid = ((ItemBlock) result.getItem()).field_150939_a;
            if (blockfluid instanceof BlockFluidBase) {
                Fluid fluid = ((BlockFluidBase) blockfluid).getFluid();
                if (fluid != null) {
                    fluidStack = new FluidStack(fluid, 1000);
                }
            }
        }
        if (fluidStack != null) {
            while (i$.hasNext()) {
                Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                for (FluidStack output : entry.getValue().getFluidOutputs()) {
                    if (output != null && output.getFluid() == fluidStack.getFluid()) {
                        this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue(),
                                getAdditionalIngredients()));
                        break;
                    }
                }
            }
        } else {
            while (i$.hasNext()) {
                Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                for (RecipeOutputItemStack output : entry.getValue().getItemOutputs()) {
                    if (NEIServerUtils.areStacksSameTypeCrafting(output.itemStack, result)) {
                        this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue(),
                                getAdditionalIngredients()));
                        break;
                    }
                }
            }
        }
    }

    public List<PositionedStack> getAdditionalIngredients() {
        return null;
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(ingredient);
        if (fluidStack == null && ingredient.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem ifc = (IFluidContainerItem) ingredient.getItem();
            fluidStack = ifc.getFluid(ingredient);
        } else if (ingredient.getItem() instanceof ItemBlock) {
            Block blockfluid = ((ItemBlock) ingredient.getItem()).field_150939_a;
            if (blockfluid instanceof BlockFluidBase) {
                Fluid fluid = ((BlockFluidBase) blockfluid).getFluid();
                if (fluid != null) {
                    fluidStack = new FluidStack(fluid, 1000);
                }
            }
        }

        if (fluidStack != null && fluidStack.getFluid() != null) {
            while (i$.hasNext()) {
                Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                if (entry.getKey().containsFluid(fluidStack)) {
                    this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue(),
                            getAdditionalIngredients()));
                }
            }
        } else {
            while (i$.hasNext()) {
                Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                if (entry.getKey().containsItem(ingredient)) {
                    this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue(),
                            getAdditionalIngredients()));
                }
            }
        }
    }

    protected int[] getFluidInputPosX() {
        return null;
    }

    protected int[] getFluidInputPosY() {
        return null;
    }

    protected int[] getFluidOutputPosX() {
        return null;
    }

    protected int[] getFluidOutputPosY() {
        return null;
    }

    protected abstract int[] getInputPosX();

    protected abstract int[] getInputPosY();

    protected abstract int[] getOutputPosX();

    protected abstract int[] getOutputPosY();

    public class CachedIORecipe extends CachedRecipe {
        private final List<PositionedStack> ingredients = new ArrayList<PositionedStack>();
        public PositionedStack output;
        public final List<PositionedStack> otherStacks = new ArrayList<PositionedStack>();
        public UniversalRecipeInput urInput;
        public UniversalRecipeOutput urOutput;

        @Override
        public List<PositionedStack> getIngredients() {
            return this.getCycledIngredients(MachineRecipeHandler.this.cycleticks / 20, this.ingredients);
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return this.otherStacks;
        }

        public CachedIORecipe(UniversalRecipeInput input, UniversalRecipeOutput output1,
                List<PositionedStack> additionalIngredients) {
            super();
            if (input == null) {
                throw new NullPointerException("Input must not be null (recipe " + input + " -> " + output1 + ").");
            } else if (output1 == null) {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            } else if ((output1.getFluidOutputs() == null || output1.getFluidOutputs().size() == 0)
                    && (output1.getItemOutputs() == null || output1.getItemOutputs().size() == 0)) {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            } else {
                this.urInput = input;
                this.urOutput = output1;
                ArrayList<List<ItemStack>> items = new ArrayList<List<ItemStack>>();
                ArrayList<List<FluidStack>> fluidItems = new ArrayList<List<FluidStack>>();

                if (input.getFluidInputs() != null && input.getFluidInputs().size() > 0) {
                    for (IRecipeInputFluid fstackRI : input.getFluidInputs()) {
                        List<FluidStack> fstackList = new ArrayList<FluidStack>();
                        fstackList.add(fstackRI.getInputs().get(0));
                        fluidItems.add(fstackList);
                    }
                }

                if (input.getItemInputs() != null && input.getItemInputs().size() > 0) {
                    for (IRecipeInput iri : input.getItemInputs()) {
                        List<ItemStack> itemInputs = new ArrayList<ItemStack>();
                        itemInputs.add(iri.getInputs().get(0).copy());
                        items.add(itemInputs);
                    }
                }

                int var7 = 0;
                int var8 = 0;
                boolean skipOneFluidOutput = false;
                if (output1.getItemOutputs() != null && output1.getItemOutputs().size() > 0) {
                    int x = MachineRecipeHandler.this.getOutputPosX()[0],
                            y = MachineRecipeHandler.this.getOutputPosY()[0];
                    if (output1.getItemOutputs().get(0) != null)
                        this.output = new ETPositionedStack(output1.getItemOutputs().get(0), x, y);
                    for (int i = 1; i < output1.getItemOutputs().size(); i++) {
                        RecipeOutputItemStack rOut = output1.getItemOutputs().get(i);
                        var7++;
                        if (var7 < MachineRecipeHandler.this.getOutputPosX().length) {
                            x = MachineRecipeHandler.this.getOutputPosX()[var7];
                        }
                        if (var7 < MachineRecipeHandler.this.getOutputPosY().length) {
                            y = MachineRecipeHandler.this.getOutputPosY()[var7];
                        }
                        if (rOut != null) {
                            this.otherStacks.add(new ETPositionedStack(rOut, x, y));
                        }
                    }
                } else {
                    if (MachineRecipeHandler.this.getFluidOutputPosX() != null) {
                        this.output = new ETPositionedStack(output1.getFluidOutputs().get(0),
                                MachineRecipeHandler.this.getFluidOutputPosX()[0],
                                MachineRecipeHandler.this.getFluidOutputPosY()[0]);
                    } else {
                        this.output = new ETPositionedStack(output1.getFluidOutputs().get(0),
                                MachineRecipeHandler.this.getOutputPosX()[0],
                                MachineRecipeHandler.this.getOutputPosY()[0]);
                    }
                    skipOneFluidOutput = true;
                    var8++;
                }

                if (output1.getFluidOutputs() != null && output1.getFluidOutputs().size() > 0) {
                    int x = MachineRecipeHandler.this.getOutputPosX()[0],
                            y = MachineRecipeHandler.this.getOutputPosY()[0];
                    if (MachineRecipeHandler.this.getFluidOutputPosX() != null) {
                        x = MachineRecipeHandler.this.getFluidOutputPosX()[0];
                        y = MachineRecipeHandler.this.getFluidOutputPosY()[0];
                    }
                    int startIndex = skipOneFluidOutput ? 1 : 0;
                    for (int i = startIndex; i < output1.getFluidOutputs().size(); i++) {
                        FluidStack fstack = output1.getFluidOutputs().get(i);
                        var7++;
                        if (MachineRecipeHandler.this.getFluidOutputPosX() != null) {
                            if (var8 < MachineRecipeHandler.this.getFluidOutputPosX().length) {
                                x = MachineRecipeHandler.this.getFluidOutputPosX()[var8];
                            }
                            if (var8 < MachineRecipeHandler.this.getFluidOutputPosY().length) {
                                y = MachineRecipeHandler.this.getFluidOutputPosY()[var8];
                            }
                            var8++;
                        } else {
                            if (var7 < MachineRecipeHandler.this.getOutputPosX().length) {
                                x = MachineRecipeHandler.this.getOutputPosX()[var7];
                            }
                            if (var7 < MachineRecipeHandler.this.getOutputPosY().length) {
                                y = MachineRecipeHandler.this.getOutputPosY()[var7];
                            }
                        }
                        this.otherStacks.add(new ETPositionedStack(fstack, x, y));
                    }
                }

                if (MachineRecipeHandler.this.getFluidInputPosX() != null) {
                    int x = MachineRecipeHandler.this.getFluidInputPosX()[0],
                            y = MachineRecipeHandler.this.getFluidInputPosY()[0];
                    for (int i = 0; i < fluidItems.size(); i++) {
                        if (i < MachineRecipeHandler.this.getFluidInputPosX().length) {
                            x = MachineRecipeHandler.this.getFluidInputPosX()[i];
                        }
                        if (i < MachineRecipeHandler.this.getFluidInputPosY().length) {
                            y = MachineRecipeHandler.this.getFluidInputPosY()[i];
                        }
                        this.ingredients.add(new ETPositionedStack(fluidItems.get(i), x, y));
                    }
                    int x2 = MachineRecipeHandler.this.getInputPosX()[0];
                    int y2 = MachineRecipeHandler.this.getInputPosY()[0];
                    for (int i = 0; i < items.size(); i++) {
                        if (i < MachineRecipeHandler.this.getInputPosX().length) {
                            x2 = MachineRecipeHandler.this.getInputPosX()[i];
                        }
                        if (i < MachineRecipeHandler.this.getInputPosY().length) {
                            y2 = MachineRecipeHandler.this.getInputPosY()[i];
                        }
                        this.ingredients.add(new PositionedStack(items.get(i), x2, y2));
                    }
                } else {
                    int x = MachineRecipeHandler.this.getInputPosX()[0],
                            y = MachineRecipeHandler.this.getInputPosY()[0];
                    for (int i = 0; i < fluidItems.size() + items.size(); i++) {
                        if (i < MachineRecipeHandler.this.getInputPosX().length) {
                            x = MachineRecipeHandler.this.getInputPosX()[i];
                        }
                        if (i < MachineRecipeHandler.this.getInputPosY().length) {
                            y = MachineRecipeHandler.this.getInputPosY()[i];
                        }
                        if (i < fluidItems.size()) {
                            this.ingredients.add(new ETPositionedStack(fluidItems.get(i), x, y));
                        } else {
                            this.ingredients.add(new PositionedStack(items.get(i - fluidItems.size()), x, y));
                        }
                    }
                }
                if (additionalIngredients != null) {
                    this.ingredients.addAll(additionalIngredients);
                }
            }
        }
    }
}
