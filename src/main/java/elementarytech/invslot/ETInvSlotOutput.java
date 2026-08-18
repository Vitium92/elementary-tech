package elementarytech.invslot;

import java.util.Iterator;
import java.util.List;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotOutput;
import elementarytech.recipe.RecipeOutputItemStack;
import elementarytech.util.ETUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ETInvSlotOutput extends InvSlotOutput {

	public ETInvSlotOutput(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
		super(base1, name1, oldStartIndex1, count);
	}

	@SuppressWarnings("rawtypes")
	public boolean canAdd(List itemOutputs) {
		if (itemOutputs == null || itemOutputs.isEmpty()) {
			return true;
		}
		Iterator ioi = itemOutputs.iterator();
		if (this.size() >= itemOutputs.size()) {
			Object rois;
			if (ioi.hasNext()) {
				rois = ioi.next();
			} else {
				return true;
			}
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i) == null || (this.objectMatchesSlot(rois, i)
						&& this.get(i).stackSize + this.getAmountOfObject(rois) < this.getStackSizeLimit()
						&& this.get(i).stackSize + this.getAmountOfObject(rois) <= this.get(i).getMaxStackSize())) {
					if (ioi.hasNext()) {
						rois = ioi.next();
					} else {
						return true;
					}
				} else {
					if (i == this.size() - 1) {
						return false;
					}
				}
			}
		}
		return false;
	}

	private float getAmountOfObject(Object obj) {
		if (obj instanceof RecipeOutputItemStack) {
			return ((RecipeOutputItemStack) obj).quantity;
		} else if (obj instanceof ItemStack) {
			return ((ItemStack) obj).stackSize;
		}
		return Short.MAX_VALUE;
	}

	public boolean objectMatchesSlot(Object obj, int slot) {
		if (this.get(slot) == null) {
			return true;
		} else {
			if (obj instanceof ItemStack) {
				return ETUtils.isItemStacksIsEqual(this.get(slot), (ItemStack) obj, true);
			} else if (obj instanceof RecipeOutputItemStack) {
				return ((RecipeOutputItemStack) obj).matches(this.get(slot));
			}
		}
		return false;
	}

	public void add(RecipeOutputItemStack rois) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i) == null || (this.objectMatchesSlot(rois, i)
					&& this.get(i).stackSize + this.getAmountOfObject(rois) < this.getStackSizeLimit())) {
				this.add(i, rois);
				break;
			}
		}
	}

	private void add(int i, RecipeOutputItemStack rois) {
		long key = (Item.getIdFromItem(rois.itemStack.getItem()) << 32) + rois.itemStack.getItemDamage();
		float amount = 0f;
		amount += rois.quantity;
		while (amount >= 1) {
			amount--;
			this.add(rois.itemStack.copy());
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public int add(List itemOutputs) {
		if (itemOutputs == null || itemOutputs.isEmpty()) {
			return 0;
		}
		Iterator ioi = itemOutputs.iterator();
		if (this.size() >= itemOutputs.size() && ioi.hasNext()) {
			Object rois = ioi.next();
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i) == null || (this.objectMatchesSlot(rois, i)
						&& this.get(i).stackSize + this.getAmountOfObject(rois) < this.getStackSizeLimit())) {
					if (rois instanceof ItemStack) {
						this.add(((ItemStack) rois).copy());
					} else if (rois instanceof RecipeOutputItemStack) {
						this.add(i, (RecipeOutputItemStack) rois);
					}
					if (ioi.hasNext()) {
						rois = ioi.next();
					} else {
						return itemOutputs.size();
					}
				} else {
					if (i == this.size() - 1) {
						return 0;
					}
				}
			}
		}
		return 0;
	}
}
