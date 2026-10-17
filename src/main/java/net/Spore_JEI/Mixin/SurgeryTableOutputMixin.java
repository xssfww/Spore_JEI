package net.Spore_JEI.Mixin;

import com.Harbinger.Spore.SBlockEntities.SurgeryTableBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value = SurgeryTableBlockEntity.class, remap = false)
public abstract class SurgeryTableOutputMixin {
    @Shadow
    @Final
    public ItemStackHandler itemHandler;
    @Inject(method = "canInsertIntoOutputSlot(Lnet/minecraft/world/item/ItemStack;I)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void replaceStaleProduct(ItemStack result, int slot, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stored = this.itemHandler.getStackInSlot(slot);
        if (stored.isEmpty() || ItemStack.isSameItemSameTags(stored, result)) {
            return;
        }
        this.itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        ci.setReturnValue(true);
    }
}
