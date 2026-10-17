package net.Spore_JEI.JEI;

import com.Harbinger.Spore.Compat.GraftingCraftingCategory;
import com.Harbinger.Spore.Compat.SurgeryCraftingCategory;
import com.Harbinger.Spore.Core.SMenu;
import com.Harbinger.Spore.Screens.GraftingMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.Spore_JEI.Spore_JEI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
@JeiPlugin
public class SporeJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Spore_JEI.MODID, "jei_plugin");
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }
    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        registration.addRecipeTransferHandler(new SurgeryRecipeTransferHandler(registration.getTransferHelper(), registration.getJeiHelpers().getIngredientManager()), SurgeryCraftingCategory.SURGERY_TYPE);
        registration.addRecipeTransferHandler(GraftingMenu.class, SMenu.GRAFTING_MENU.get(), GraftingCraftingCategory.GRAFTING_TYPE, 36, 3, 0, 36);
    }
}
