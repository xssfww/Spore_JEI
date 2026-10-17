package net.Spore_JEI.JEI;

import com.Harbinger.Spore.Compat.SurgeryCraftingCategory;
import com.Harbinger.Spore.Core.SMenu;
import com.Harbinger.Spore.Recipes.SurgeryRecipe;
import com.Harbinger.Spore.Screens.SurgeryMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
public class SurgeryRecipeTransferInfo implements IRecipeTransferInfo<SurgeryMenu, SurgeryRecipe> {
    public static final int PLAYER_SLOT_COUNT = 36;
    public static final int SURGERY_INPUT_SLOT_COUNT = 16;
    public static final int SURGERY_STITCHES_SLOT = PLAYER_SLOT_COUNT + SURGERY_INPUT_SLOT_COUNT;
    public static final int SURGERY_AGENT_SLOT_COUNT = 3;
    public static final int SURGERY_LAST_SLOT = SURGERY_STITCHES_SLOT + SURGERY_AGENT_SLOT_COUNT;
    @Override
    public @NotNull Class<SurgeryMenu> getContainerClass() {
        return SurgeryMenu.class;
    }
    @Override
    public @NotNull Optional<MenuType<SurgeryMenu>> getMenuType() {
        return Optional.of(SMenu.SURGERY_MENU.get());
    }
    @Override
    public @NotNull RecipeType<SurgeryRecipe> getRecipeType() {
        return SurgeryCraftingCategory.SURGERY_TYPE;
    }
    @Override
    public boolean canHandle(@NotNull SurgeryMenu container, @NotNull SurgeryRecipe recipe) {
        return true;
    }
    @Override
    public @NotNull List<Slot> getRecipeSlots(SurgeryMenu container, @NotNull SurgeryRecipe recipe) {
        return container.slots.stream().filter(slot -> slot.index >= PLAYER_SLOT_COUNT && slot.index <= SURGERY_LAST_SLOT).toList();
    }
    @Override
    public @NotNull List<Slot> getInventorySlots(SurgeryMenu container, @NotNull SurgeryRecipe recipe) {
        return container.slots.stream().filter(slot -> slot.container instanceof Inventory).toList();
    }
}
