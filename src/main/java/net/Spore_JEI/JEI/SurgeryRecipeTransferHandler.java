package net.Spore_JEI.JEI;

import com.Harbinger.Spore.Compat.SurgeryCraftingCategory;
import com.Harbinger.Spore.Core.SMenu;
import com.Harbinger.Spore.Core.Sitems;
import com.Harbinger.Spore.Recipes.SurgeryRecipe;
import com.Harbinger.Spore.Screens.SurgeryMenu;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.RecipeTransferResult;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
public class SurgeryRecipeTransferHandler implements IRecipeTransferHandler<SurgeryMenu, SurgeryRecipe> {
    private final IRecipeTransferHandler<SurgeryMenu, SurgeryRecipe> delegate;
    private final IIngredientManager ingredientManager;
    public SurgeryRecipeTransferHandler(@NotNull IRecipeTransferHandlerHelper transferHelper, @NotNull IIngredientManager ingredientManager) {
        this.delegate = transferHelper.createUnregisteredRecipeTransferHandler(new SurgeryRecipeTransferInfo());
        this.ingredientManager = ingredientManager;
    }
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
    @SuppressWarnings("unchecked")
    @Override
    public IRecipeTransferError transferRecipe(@NotNull IRecipeTransferContext context, boolean doTransfer) {
        return delegate.transferRecipe(new StitchesAwareTransferContext(context, ingredientManager), doTransfer);
    }
    @Override
    @SuppressWarnings("removal")
    public IRecipeTransferError transferRecipe(@NotNull SurgeryMenu container, @NotNull SurgeryRecipe recipe, @NotNull IRecipeSlotsView recipeSlots, @NotNull Player player, boolean maxTransfer, boolean doTransfer) {
        return delegate.transferRecipe(container, recipe, new StitchesAwareSlotsView(recipeSlots, player, ingredientManager), player, maxTransfer, doTransfer);
    }
    @SuppressWarnings({"NonExtendableApiUsage", "rawtypes"})
    private static final class StitchesAwareTransferContext implements IRecipeTransferContext {
        private final IRecipeTransferContext original;
        private final IRecipeSlotsView recipeSlots;
        private StitchesAwareTransferContext(@NotNull IRecipeTransferContext original, @NotNull IIngredientManager ingredientManager) {
            this.original = original;
            this.recipeSlots = new StitchesAwareSlotsView(original.getRecipeSlots(), original.getPlayer(), ingredientManager);
        }
        @Override
        public int getTransferId() {
            return original.getTransferId();
        }
        @Override
        public void completeRecipeTransfer(@NotNull RecipeTransferResult result) {
            original.completeRecipeTransfer(result);
        }
        @Override
        public @NotNull Object getRecipe() {
            return original.getRecipe();
        }
        @Override
        public @NotNull RecipeType<?> getRecipeType() {
            return original.getRecipeType();
        }
        @Override
        public @NotNull AbstractContainerMenu getContainer() {
            return original.getContainer();
        }
        @Override
        public @NotNull AbstractContainerScreen<?> getScreen() {
            return original.getScreen();
        }
        @Override
        public @NotNull IRecipeSlotsView getRecipeSlots() {
            return recipeSlots;
        }
        @Override
        public @NotNull Player getPlayer() {
            return original.getPlayer();
        }
        @Override
        public boolean isMaxTransfer() {
            return original.isMaxTransfer();
        }
    }
    @SuppressWarnings("NonExtendableApiUsage")
    private static final class StitchesAwareSlotsView implements IRecipeSlotsView {
        private static final List<Supplier<Item>> AGENT_ITEMS = List.of(Sitems.HARDENING_AGENT, Sitems.SHARPENING_AGENT, Sitems.INTEGRATING_AGENT);
        private final IRecipeSlotsView original;
        private final List<IRecipeSlotView> inputSlots;
        private StitchesAwareSlotsView(@NotNull IRecipeSlotsView original, @NotNull Player player, @NotNull IIngredientManager ingredientManager) {
            this.original = original;
            List<IRecipeSlotView> slots = new ArrayList<>(original.getSlotViews(RecipeIngredientRole.INPUT));
            List<IRecipeSlotView> catalysts = original.getSlotViews(RecipeIngredientRole.CATALYST);
            if (!catalysts.isEmpty()) {
                slots.add(catalysts.get(0));
            }
            for (int i = 1; i < catalysts.size() && i <= AGENT_ITEMS.size(); i++) {
                slots.add(new AgentSlotView(catalysts.get(i), agentIngredient(player, AGENT_ITEMS.get(i - 1).get(), ingredientManager)));
            }
            this.inputSlots = List.copyOf(slots);
        }
        @Override
        public @NotNull List<IRecipeSlotView> getSlotViews() {
            return original.getSlotViews();
        }
        @Override
        public @NotNull List<IRecipeSlotView> getSlotViews(@NotNull RecipeIngredientRole role) {
            if (role == RecipeIngredientRole.INPUT) {
                return inputSlots;
            }
            return original.getSlotViews(role);
        }
    }
    private static @NotNull List<ITypedIngredient<?>> agentIngredient(@NotNull Player player, @NotNull Item item, @NotNull IIngredientManager ingredientManager) {
        if (!hasItem(player, item)) {
            return List.of();
        }
        Optional<ITypedIngredient<ItemStack>> typed = ingredientManager.createTypedIngredient(VanillaTypes.ITEM_STACK, new ItemStack(item));
        return typed.<List<ITypedIngredient<?>>>map(List::of).orElseGet(List::of);
    }
    private static boolean hasItem(@NotNull Player player, @NotNull Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item)) {
                return true;
            }
        }
        return false;
    }
    @SuppressWarnings("NonExtendableApiUsage")
    private record AgentSlotView(IRecipeSlotView template, List<ITypedIngredient<?>> ingredients) implements IRecipeSlotView {
        private AgentSlotView(@NotNull IRecipeSlotView template, @NotNull List<ITypedIngredient<?>> ingredients) {
            this.template = template;
            this.ingredients = ingredients;
        }
        @Override
        public @NotNull Stream<ITypedIngredient<?>> getAllIngredients() {
            return ingredients.stream();
        }
        @Override
        public @NotNull List<ITypedIngredient<?>> getAllIngredientsList() {
            return ingredients;
        }
        @Override
        public @NotNull Optional<ITypedIngredient<?>> getDisplayedIngredient() {
            return ingredients.isEmpty() ? Optional.empty() : Optional.of(ingredients.get(0));
        }
        @Override
        public @NotNull Stream<ITypedIngredient<?>> getDisplayedIngredients() {
            return ingredients.stream();
        }
        @Override
        public @NotNull Optional<TagKey<?>> getTagKey() {
            return template.getTagKey();
        }
        @Override
        public @NotNull RecipeIngredientRole getRole() {
            return template.getRole();
        }
        @Override
        public void drawHighlight(@NotNull GuiGraphics guiGraphics, int color) {
            template.drawHighlight(guiGraphics, color);
        }
        @Override
        public @NotNull Optional<String> getSlotName() {
            return template.getSlotName();
        }
    }
}
