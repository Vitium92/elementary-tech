package elementarytech.machines.evaporator;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elementarytech.ETCreativeTab;
import elementarytech.ModInfo;
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

public class EvaporatorBlock extends Block implements ITileEntityProvider {

    IIcon textureFrontActive, textureSide, textureBottom, textureTop;

    public EvaporatorBlock() {
        super(Material.iron);
        this.setCreativeTab(ETCreativeTab.tab);
        this.setStepSound(soundTypeMetal);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return new ItemStack(Blocks.furnace, 1).getItem();
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag) {
        ItemStack result = new ItemStack(Blocks.furnace, 1);
        this.dropBlockAsItem(world, x, y, z, result);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new EvaporatorTileEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(ModInfo.MODID + ":solidFuelEvaporatorFront");
        this.textureFrontActive = reg.registerIcon(ModInfo.MODID + ":solidFuelEvaporatorFrontActive");
        this.textureSide = reg.registerIcon(ModInfo.MODID + ":solidFuelEvaporatorSide");
        this.textureTop = reg.registerIcon(ModInfo.MODID + ":solidFuelEvaporatorTop");
        this.textureBottom = reg.registerIcon(ModInfo.MODID + ":solidFuelEvaporatorBottom");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof EvaporatorTileEntity) {
            EvaporatorTileEntity bte = (EvaporatorTileEntity) te;
            if (bte == null || entityPlayer.isSneaking()) {
                return false;
            } else {
                return bte.getGui(entityPlayer);
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int var7 = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        TileEntity t = world.getTileEntity(x, y, z);
        if (t != null && t instanceof EvaporatorTileEntity) {
            EvaporatorTileEntity te = (EvaporatorTileEntity) t;
            if (player.isSneaking()) {
                switch (var7) {
                    case 0: te.setFacing((short) 3); break;
                    case 1: te.setFacing((short) 4); break;
                    case 2: te.setFacing((short) 2); break;
                    case 3: te.setFacing((short) 5); break;
                }
            } else {
                switch (var7) {
                    case 0: te.setFacing((short) 2); break;
                    case 1: te.setFacing((short) 5); break;
                    case 2: te.setFacing((short) 3); break;
                    case 3: te.setFacing((short) 4); break;
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IIcon faceIcon = this.blockIcon;
        int facing = 3;
        int mask[] = {
            0,1,2,3,4,5,
            1,0,3,2,4,5,
            2,3,0,1,4,5,
            2,3,1,0,4,5,
            2,3,5,4,0,1,
            2,3,4,5,1,0
        };
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            EvaporatorTileEntity tebh = (EvaporatorTileEntity) te;
            facing = tebh.getFacing();
            if (tebh.getActive()) {
                faceIcon = this.textureFrontActive;
            }
        }

        switch (mask[facing * 6 + side]) {
            case 0: return faceIcon;
            case 1: return this.textureSide;
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
            case 0: return this.textureTop;
            case 1: return this.textureBottom;
            case 2: return this.textureSide;
            case 3: return this.blockIcon;
            case 4: return this.textureSide;
            case 5: return this.textureSide;
            default: return this.textureSide;
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof EvaporatorTileEntity) {
            EvaporatorTileEntity ete = (EvaporatorTileEntity) te;
            if (ete.getActive()) {
                world.spawnParticle("snowshovel", x + 0.2D, y + 1.2D, z + 0.2D, 0D, 0.05D, 0D);
                world.spawnParticle("flame", x + 0.5D + ete.mX() * 0.5D + (random.nextDouble() * 0.4D - 0.2D) * ete.mZ(), y + random.nextDouble() * 0.25D, z + 0.5D + ete.mZ() * 0.5D + (random.nextDouble() * 0.4D - 0.2D) * ete.mX(), 0D, 0.01D, 0D);
            }
        }
    }
}
