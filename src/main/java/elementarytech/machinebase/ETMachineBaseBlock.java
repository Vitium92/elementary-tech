package elementarytech.machinebase;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elementarytech.ETCreativeTab;
import elementarytech.ModInfo;
import elementarytech.machines.evaporator.SolarEvaporatorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ETMachineBaseBlock extends Block implements ITileEntityProvider {

    public enum MachineType {
        SolarEvaporator,
        LeadOven,
        BronzeVat
    }

    private static ETMachineBaseBlock[] machineBlocks = new ETMachineBaseBlock[MachineType.values().length];
    private static boolean[] initialized = new boolean[MachineType.values().length];

    private final MachineType machineType;
    public final MachineType type;
    IIcon textureFrontActive, textureFrontInactive, textureSide, textureBottom, textureTop, textureBack;
    private IIcon innerSideIcon, innerBottomIcon;

    public ETMachineBaseBlock(MachineType type) {
        super(Material.iron);
        this.machineType = type;
        this.type = type;
        this.setCreativeTab(ETCreativeTab.tab);
        this.setStepSound(soundTypeMetal);
        this.setBlockName(type.name().substring(0, 1).toLowerCase() + type.name().substring(1));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @Override
    public int getLightOpacity() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        if (block == this) {
            return false;
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
    }
    
    public MachineType getMachineType() {
        return machineType;
    }

    public static void init() {
        for (MachineType type : MachineType.values()) {
            if (!initialized[type.ordinal()]) {
                machineBlocks[type.ordinal()] = new ETMachineBaseBlock(type);
                GameRegistry.registerBlock(machineBlocks[type.ordinal()], ETInvItemBlock.class, type.name());
                GameRegistry.registerTileEntity(
                    ETMachineBaseBlock.getTileEntityClass(type),
                    ModInfo.MODID + ":" + type.name()
                );
                initialized[type.ordinal()] = true;
            }
        }
    }

    private static Class<? extends TileEntity> getTileEntityClass(MachineType type) {
        switch (type) {
            case SolarEvaporator:
                return SolarEvaporatorTileEntity.class;
            case LeadOven:
                return elementarytech.machines.leadoven.LeadOvenTileEntity.class;
            case BronzeVat:
                return elementarytech.machines.bronzevat.BronzeVatTileEntity.class;
            default:
                return SolarEvaporatorTileEntity.class;
        }
    }

    public static ETMachineBaseBlock getMachineBlock(MachineType type) {
        return machineBlocks[type.ordinal()];
    }

    public static ItemStack getMachineBlock(MachineType type, int stackSize) {
        return new ItemStack(machineBlocks[type.ordinal()], stackSize);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag) {
        ItemStack result = new ItemStack(this);
        this.dropBlockAsItem(world, x, y, z, result);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        switch (machineType) {
            case SolarEvaporator:
                return new SolarEvaporatorTileEntity();
            case LeadOven:
                return new elementarytech.machines.leadoven.LeadOvenTileEntity();
            case BronzeVat:
                return new elementarytech.machines.bronzevat.BronzeVatTileEntity();
            default:
                return new SolarEvaporatorTileEntity();
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public int getRenderType() {
        return elementarytech.ElementaryTech.proxy.shareBlockRendererByMachineType(machineType);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        String prefix = ModInfo.MODID + ":";
        switch (machineType) {
            case SolarEvaporator:
                this.blockIcon = reg.registerIcon(prefix + "solarEvaporatorFront");
                this.textureFrontActive = reg.registerIcon(prefix + "solarEvaporatorFront");
                this.textureSide = reg.registerIcon(prefix + "solarEvaporatorSide");
                this.textureTop = reg.registerIcon(prefix + "solarEvaporatorTop");
                this.textureBottom = reg.registerIcon(prefix + "solarEvaporatorBottom");
                this.innerSideIcon = reg.registerIcon(prefix + "solarEvaporatorInnerSide");
                this.innerBottomIcon = reg.registerIcon(prefix + "solarEvaporatorInnerBottom");
                break;
            case LeadOven:
                this.blockIcon = reg.registerIcon(prefix + "leadOvenFront");
                this.textureFrontActive = reg.registerIcon(prefix + "leadOvenFrontActive");
                this.textureFrontInactive = reg.registerIcon(prefix + "leadOvenFront");
                this.textureSide = reg.registerIcon(prefix + "leadOvenSide");
                this.textureTop = reg.registerIcon(prefix + "leadOvenTop");
                this.textureBottom = reg.registerIcon(prefix + "leadOvenBottom");
                this.textureBack = reg.registerIcon(prefix + "leadOvenBack");
                break;
            case BronzeVat:
                this.blockIcon = reg.registerIcon(prefix + "bronzeVatFront");
                this.textureFrontActive = reg.registerIcon(prefix + "bronzeVatFrontActive");
                this.textureSide = reg.registerIcon(prefix + "bronzeVatSide");
                this.textureTop = reg.registerIcon(prefix + "bronzeVatTop");
                this.textureBottom = reg.registerIcon(prefix + "bronzeVatBottom");
                this.innerSideIcon = reg.registerIcon(prefix + "bronzeVatInnerSide");
                this.innerBottomIcon = reg.registerIcon(prefix + "bronzeVatInnerBottom");
                break;
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getAdditionalIconsForBlockRenderer(int index) {
        switch (machineType) {
            case SolarEvaporator:
                return this.textureSide;
            case BronzeVat:
                return this.textureSide;
            default:
                return this.blockIcon;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IIcon faceIcon = this.blockIcon;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ic2.core.block.TileEntityInventory) {
            ic2.core.block.TileEntityInventory inv = (ic2.core.block.TileEntityInventory) te;
            if (inv.getActive()) {
                faceIcon = this.textureFrontActive;
            } else if (this.textureFrontInactive != null) {
                faceIcon = this.textureFrontInactive;
            }
        }
        int facing = 3;
        if (te instanceof ic2.core.block.TileEntityInventory) {
            facing = ((ic2.core.block.TileEntityInventory) te).getFacing();
        }
        int[] mask = {
            0,1,2,3,4,5,
            1,0,3,2,4,5,
            2,3,0,1,4,5,
            2,3,1,0,4,5,
            2,3,5,4,0,1,
            2,3,4,5,1,0
        };
        switch (mask[facing * 6 + side]) {
            case 0: return faceIcon;
            case 1: return this.textureBack != null ? this.textureBack : this.textureSide;
            case 2: return this.textureBottom;
            case 3: return this.textureTop;
            case 4: return this.textureSide;
            case 5: return this.textureSide;
            default: return this.textureSide;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (side) {
            case 0: return this.textureBottom;
            case 1: return this.textureTop;
            case 2: return this.textureBack != null ? this.textureBack : this.textureSide;
            case 3: return this.blockIcon;
            case 4: return this.textureSide;
            case 5: return this.textureSide;
            default: return this.textureSide;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof ic2.core.IHasGui) {
            if (player.isSneaking()) return false;
            return ic2.core.IC2.platform.launchGui(player, (ic2.core.IHasGui) te);
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int facing = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ic2.core.block.TileEntityInventory) {
            ic2.core.block.TileEntityInventory ste = (ic2.core.block.TileEntityInventory) te;
            switch (facing) {
                case 0: ste.setFacing((short) 2); break;
                case 1: ste.setFacing((short) 5); break;
                case 2: ste.setFacing((short) 3); break;
                case 3: ste.setFacing((short) 4); break;
            }
        }
    }
}
