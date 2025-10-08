package fuzs.visualworkbench.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.visualworkbench.handler.BlockConversionHandler;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(BlockConversionHandler.INVALID_BLOCK_COMPONENT, "Unable to open. Break and replace to use.");
        builder.add(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG, "Unaltered Workbenches");
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !(holder.value() instanceof Block) && super.mustHaveTranslationKey(holder, translationKey);
    }
}
