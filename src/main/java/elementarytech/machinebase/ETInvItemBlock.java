package elementarytech.machinebase;

import elementarytech.machinebase.ETMachineBaseBlock.MachineType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ETInvItemBlock extends ItemBlock {

    public ETInvItemBlock(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName();
    }
}
