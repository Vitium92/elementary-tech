package elementarytech;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import elementarytech.machinebase.ETMachineBaseBlock;
import elementarytech.machines.evaporator.ElectricEvaporatorBlock;
import elementarytech.machines.evaporator.ElectricEvaporatorTileEntity;
import elementarytech.machines.evaporator.EvaporatorBlock;
import elementarytech.machines.evaporator.EvaporatorTileEntity;
import elementarytech.handpump.HandPump;
import elementarytech.handpump.AdvancedHandPump;
import elementarytech.tree.RubberTreeBlock;
import elementarytech.tree.SackBlock;
import elementarytech.tree.SackTileEntity;
import elementarytech.util.ETBucketHandler;
import elementarytech.util.ETConfig;
import elementarytech.util.ETUtils;
import elementarytech.util.FluidDictionary;
import elementarytech.world.ETFluid;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModInfo.MODID, name = ModInfo.MODNAME, version = ModInfo.MODVERSION,
     dependencies = "required-after:IC2@[2.2.767-experimental,)")
public class ElementaryTech {

    @Instance(ModInfo.MODID)
    public static ElementaryTech instance;

    @SidedProxy(
        clientSide = "elementarytech.ClientProxy",
        serverSide = "elementarytech.ServerProxy"
    )
    public static ServerProxy proxy;

    public static final Logger log = LogManager.getLogger(ModInfo.MODID);
    public static ETConfig config;

    public static Block evaporatorBlock;
    public static Block electricEvaporatorBlock;
    public static Block rubberTreeBlock;
    public static Block spruceTreeBlock;
    public static Block sackBlock;
    public static Item handpump;
    public static Item advancedHandpump;
    public static Block ic2Leaves;
    public static Block ic2Wood;
    public static FluidDictionary fluidDictionary;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new ETConfig(event.getSuggestedConfigurationFile());
        fluidDictionary = new FluidDictionary();

        ETFluid.init();

        evaporatorBlock = (new EvaporatorBlock()).setBlockName("evaporatorBlock")
                .setBlockTextureName(ModInfo.MODID + ":solidFuelEvaporatorFrontActive")
                .setHardness(5.0F).setResistance(5.0F);
        GameRegistry.registerBlock(evaporatorBlock, "evaporatorBlock");
        GameRegistry.registerTileEntity(EvaporatorTileEntity.class, "evaporatorTileEntity");

        electricEvaporatorBlock = (new ElectricEvaporatorBlock()).setBlockName("electricEvaporatorBlock")
                .setBlockTextureName(ModInfo.MODID + ":electricEvaporatorFrontActive")
                .setHardness(5.0F).setResistance(5.0F);
        GameRegistry.registerBlock(electricEvaporatorBlock, "electricEvaporatorBlock");
        GameRegistry.registerTileEntity(ElectricEvaporatorTileEntity.class, "electricEvaporatorTileEntity");

        ETMachineBaseBlock.init();

        rubberTreeBlock = (new RubberTreeBlock(false)).setBlockName("rubberTreeBlock")
                .setBlockTextureName(ModInfo.MODID + ":blockRubWoodFront")
                .setHardness(2.0F).setResistance(5.0F);
        GameRegistry.registerBlock(rubberTreeBlock, "rubberTreeBlock");

        spruceTreeBlock = (new RubberTreeBlock(true)).setBlockName("spruceTreeBlock")
                .setBlockTextureName(ModInfo.MODID + ":blockSpruceFront")
                .setHardness(2.0F).setResistance(5.0F);
        GameRegistry.registerBlock(spruceTreeBlock, "spruceTreeBlock");

        sackBlock = (new SackBlock()).setBlockName("sackBlock")
                .setBlockTextureName(ModInfo.MODID + ":sackItem")
                .setHardness(0.5F).setResistance(0.5F);
        GameRegistry.registerBlock(sackBlock, "sackBlock");
        GameRegistry.registerTileEntity(SackTileEntity.class, "sackTileEntity");

        handpump = new HandPump();
        GameRegistry.registerItem(handpump, "Handpump");

        advancedHandpump = new AdvancedHandPump();
        GameRegistry.registerItem(advancedHandpump, advancedHandpump.getUnlocalizedName());

        MinecraftForge.EVENT_BUS.register(new ETBucketHandler());
        proxy.load();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ic2Leaves = StackUtil.getBlock(IC2Items.getItem("rubberLeaves"));
        ic2Wood = StackUtil.getBlock(IC2Items.getItem("rubberWood"));
    }

}
