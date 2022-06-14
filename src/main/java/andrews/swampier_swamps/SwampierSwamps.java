package andrews.swampier_swamps;

import andrews.swampier_swamps.registry.SSBlocks;
import andrews.swampier_swamps.registry.SSFrogVariants;
import andrews.swampier_swamps.registry.SSItems;
import andrews.swampier_swamps.util.Reference;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = Reference.MODID)
public class SwampierSwamps
{
    public SwampierSwamps()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SSFrogVariants.FROG_VARIANTS.register(modEventBus);
        SSItems.ITEMS.register(modEventBus);
        SSBlocks.BLOCKS.register(modEventBus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            modEventBus.addListener(this::setupClient);
        });
        modEventBus.addListener(this::setupCommon);
    }

    void setupCommon(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {});
        //Thread Safe Stuff Bellow
//      SSNetwork.setupMessages();
    }

    void setupClient(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
//            SSBlockEntities.registerBlockEntityRenderers();
        });
        // Thread Safe Stuff Bellow
        SSBlocks.registerBlockRenderTypes();
    }
}