package elementarytech.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elementarytech.ETCreativeTab;
import elementarytech.ModInfo;
import elementarytech.util.ETFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ETFluid extends Fluid {
    private ETFluidType type;
    private static List<Fluid> fluidInstances = new ArrayList<Fluid>();
    private static Map<String, ETFluidType> localFluidRegistry = new HashMap<String, ETFluidType>();
    private static Map<String, Float> realDensityMap = new HashMap<String, Float>();
    private static final int maxGaseousStateVapoursDensity = 40;

    public ETFluid(ETFluidType type1) {
        super(type1.fluidRegistryName);
        type = type1;
        this.setTemperature(type.temperature);
        this.setDensity(Math.round(type.density));
        realDensityMap.put(type1.fluidRegistryName, type.density);
        this.setUnlocalizedName(type.fluidRegistryName.replaceFirst("fluid", ""));
        this.setGaseous(type.isGaseous);
        Fluid instance = this;
        if (!FluidRegistry.registerFluid(instance)) {
            instance = FluidRegistry.getFluid(type.fluidRegistryName);
        }
        if (instance.getBlock() == null) {
            instance.setBlock(new ETFluidBlock(instance, type.blockMaterial, type.textureName,
                    "fluid" + type.fluidName.replaceFirst("fluid", "")).setFlammable(type.flammable)
                            .setBlockName("block" + type.fluidName).setCreativeTab(ETCreativeTab.tab));
        }
        instance.setGaseous(type.isGaseous);
        if (type.haveBucket) {
            Item bucket = new ItemBucket(block).setTextureName(ModInfo.MODID + ":bucket_" + type.fluidName)
                    .setUnlocalizedName("bucket_" + type.fluidName).setCreativeTab(ETCreativeTab.tab);
            GameRegistry.registerItem(bucket, "bucket_" + type.fluidName);
            FluidContainerRegistry.registerFluidContainer(instance, new ItemStack(bucket),
                    FluidContainerRegistry.EMPTY_BUCKET);
            bucket.setContainerItem(Items.bucket);
        }
        type.fluid = instance;
        localFluidRegistry.put(type1.fluidRegistryName, type);
        fluidInstances.add(this);
    }

    public ETFluid() {
        super("elementarytech_dummy");
    }

    public static void init() {
        ETFluidType[] var1 = ETFluidType.values();
        for (int i = 0; i < var1.length; i++) {
            new ETFluid(var1[i]);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerIcons(TextureMap iconRegistry) {
        Iterator<Fluid> ii = fluidInstances.iterator();
        while (ii.hasNext()) {
            Fluid instance = ii.next();
            ETFluidType instanceType = localFluidRegistry.get(instance.getName());
            if (instanceType != null) {
                instance.setIcons(iconRegistry.registerIcon(ModInfo.MODID + ":" + instanceType.textureName + "Still"),
                        iconRegistry.registerIcon(ModInfo.MODID + ":" + instanceType.textureName + "Flowing"));
            }
        }
    }

    public static ItemStack getCell(String fluidname1) {
        ItemStack filledCell = FluidContainerRegistry.fillFluidContainer(
                new FluidStack(FluidRegistry.getFluid(fluidname1), FluidContainerRegistry.BUCKET_VOLUME),
                new ItemStack(Items.bucket));
        return filledCell;
    }

    public static Block getBlock(String fluidname) {
        ETFluidType type = localFluidRegistry.get(fluidname);
        if (type != null && type.fluid != null) {
            return type.fluid.getBlock();
        }
        return null;
    }

    public static float getRealDensity(Fluid gas) {
        if (realDensityMap.containsKey(gas.getName())) {
            return realDensityMap.get(gas.getName());
        } else {
            float density = gas.getDensity();
            if (gas.getDensity() < 0) {
                density = -8000F / gas.getDensity();
            }
            return density;
        }
    }

    public static float getRealDensity(String fluidName) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid != null) {
            return getRealDensity(fluid);
        }
        return 0F;
    }

    public Fluid getFluid(String name) {
        return FluidRegistry.getFluid(name);
    }

    public String getFluidDictionaryName(Fluid fluid) {
        if (fluid == null) return null;
        return fluid.getName();
    }

    public ItemStack getFluidBucket(String fluidName) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) return null;
        FluidStack fstack = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
        ItemStack bucket = FluidContainerRegistry.fillFluidContainer(fstack, new ItemStack(Items.bucket));
        return bucket;
    }

    public enum ETFluidType {
        fluidRubberTreeSap("fluidRubberTreeSap", "fluidRubberTreeSap", 10019, 293, 273, 393, 1200f,
                "fluidrubbertreesap", Material.water, true, true, false),
        SpruceResin("SpruceResin", "fluidSpruceResin", 10018, 293, 273, 533, 1080,
                "spruceresin", Material.water, true, true, true),
        SaltWater("SaltWater", "fluidSaltWater", 10028, 293, 253, 373, 1360f,
                "saltwater", Material.water, true, false, false),
        BoricAcid("BoricAcid", 10038, 373, 249, 373, 1275,
                "boricacid", Material.water, false, false, false),
        NatriumHydroxide("NatriumHydroxideDissolvedInWater", 10021, 293, 249, 373, 1525,
                "solution.natriumhydroxide", Material.water, true, false, false),
        ZeolitePulp("ZeolitePulp", "fluidPulpZeolite", 10044, 293, 249, 373, 1150,
                "pulp.sodiumzeolite", Material.water, false, false, false),
        LithiumChloride("LithiumChlorideDissolvedInWater", 10046, 293, 249, 373, 1530,
                "solution.lithiumchloride", Material.water, false, false, false),
        CalciumChloride("CalciumChlorideDissolvedInWater", 10045, 293, 249, 373, 1630,
                "solution.calciumchloride", Material.water, false, false, false),
        SulfuricAcid("SulfuricAcid", 10002, 293, 283, 610, 1836,
                "sulfuricacid", Material.water, true, false, false),
        Mercury("Mercury", 10031, 293, 234, 630, 13546,
                "mercury", Material.water, true, false, false),
        Limemilk("Limemilk", 10010, 293, 250, 373, 1020,
                "limemilk", Material.water, true, false, false),
        Glycerol("Glycerol", 10016, 293, 291, 583, 1261,
                "glycerol", Material.water, true, false, true),
        OleicAcid("OleicAcid", 10011, 293, 288, 633, 895,
                "oleicacid", Material.water, true, false, true),
        Turpentine("Turpentine", 10025, 293, 217, 453, 1470,
                "turpentine", Material.water, true, false, true),
        CablingColophony("CablingColophony", 10017, 363, 363, 533, 1070,
                "cablingcolophony", Material.water, true, false, true);

        ETFluidType(String fluidName1, int celldamage, int temperature1, int meltingPoint1, int boilingPoint1,
                float density1, String fluidRegistryName1, Material blockMaterial1, boolean hasCell1,
                boolean haveBucket1, boolean flammable1) {
            fluidName = fluidName1;
            fluidRegistryName = fluidRegistryName1;
            textureName = "fluid" + fluidName.replaceFirst("fluid", "");
            temperature = temperature1;
            density = density1;
            cellName = "itemCell" + fluidName;
            haveBucket = haveBucket1;
            flammable = flammable1;
            isGaseous = density1 < maxGaseousStateVapoursDensity;
            blockMaterial = blockMaterial1;
            boilingPoint = boilingPoint1;
            meltingPoint = meltingPoint1;
            hasCell = hasCell1;
            damage = celldamage;
        }

        ETFluidType(String fluidName1, String textureName1, int celldamage, int temperature1, int meltingPoint1,
                int boilingPoint1, float density1, String fluidRegistryName1, Material blockMaterial1, boolean hasCell1,
                boolean haveBucket1, boolean flammable1) {
            fluidName = fluidName1;
            fluidRegistryName = fluidRegistryName1;
            textureName = textureName1;
            temperature = temperature1;
            density = density1;
            cellName = "itemCell" + fluidName;
            haveBucket = haveBucket1;
            flammable = flammable1;
            isGaseous = density1 < maxGaseousStateVapoursDensity;
            blockMaterial = blockMaterial1;
            boilingPoint = boilingPoint1;
            meltingPoint = meltingPoint1;
            hasCell = hasCell1;
            damage = celldamage;
        }

        public String fluidName;
        public String fluidRegistryName;
        public String cellName;
        public String textureName;
        int temperature;
        float density;
        boolean isGaseous;
        boolean flammable = false;
        boolean haveBucket;
        Material blockMaterial = Material.water;
        int meltingPoint;
        int boilingPoint;
        public boolean hasCell = true;
        public Fluid fluid;
        public final int damage;
    }
}
