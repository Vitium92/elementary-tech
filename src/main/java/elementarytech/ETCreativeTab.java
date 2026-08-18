package elementarytech;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ETCreativeTab extends CreativeTabs {

    public static final ETCreativeTab tab = new ETCreativeTab();

    public ETCreativeTab() {
        super("ElementaryTech");
    }

    @Override
    public Item getTabIconItem() {
        return null;
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Elementary Tech";
    }
}
