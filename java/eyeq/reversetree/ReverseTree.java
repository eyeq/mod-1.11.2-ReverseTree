package eyeq.reversetree;

import com.google.common.collect.Lists;
import eyeq.reversetree.block.BlockLeavesLevitation;
import eyeq.reversetree.block.BlockLevitation;
import eyeq.reversetree.block.BlockSaplingReverse;
import eyeq.reversetree.event.ReverseTreeEventHandler;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.renderer.block.statemap.StateMapper;
import eyeq.util.client.renderer.block.statemap.StateMapperNormal;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.item.UItemBlockHasSubtypes;
import eyeq.util.item.crafting.UCraftingManager;
import eyeq.util.oredict.CategoryTypes;
import eyeq.util.oredict.UOreDictionary;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

import static eyeq.reversetree.ReverseTree.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
@Mod.EventBusSubscriber
public class ReverseTree {
    public static final String MOD_ID = "eyeq_reversetree";

    @Mod.Instance(MOD_ID)
    public static ReverseTree instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    public static Block reverseSapling;
    public static Block reverseLeaves;
    public static Block levitation;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        addRecipes();
        MinecraftForge.EVENT_BUS.register(new ReverseTreeEventHandler());
        if(event.getSide().isServer()) {
            return;
        }
        renderBlockModels();
        renderItemModels();
        createFiles();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if(event.getSide().isServer()) {
            return;
        }
        registerBlockColors();
        registerItemColors();
    }

    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event) {
        reverseSapling = new BlockSaplingReverse().setHardness(0.0F).setUnlocalizedName("saplingReverse");
        reverseLeaves = new BlockLeavesLevitation().setUnlocalizedName("leavesReverse");
        levitation = new BlockLevitation(Material.GLASS).setUnlocalizedName("levitation");

        GameRegistry.register(reverseSapling, resource.createResourceLocation("reverse_sapling"));
        GameRegistry.register(reverseLeaves, resource.createResourceLocation("reverse_leaves"));
        GameRegistry.register(levitation, resource.createResourceLocation("levitation"));
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
        GameRegistry.register(new UItemBlockHasSubtypes(reverseSapling), reverseSapling.getRegistryName());
        GameRegistry.register(new ItemBlock(reverseLeaves), reverseLeaves.getRegistryName());
        GameRegistry.register(new ItemBlock(levitation), levitation.getRegistryName());

        UOreDictionary.registerOre(CategoryTypes.TREE_SAPLING, "reverse", reverseSapling);
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(UCraftingManager.getRecipeCompress(levitation, reverseLeaves));
    }

    @SideOnly(Side.CLIENT)
    public static void renderBlockModels() {
        ResourceLocationFactory mc = ResourceLocationFactory.mc;
        ModelLoader.setCustomStateMapper(reverseSapling, new StateMapper(resource, null, "reverse_sapling", Lists.newArrayList(new IProperty[]{BlockSaplingReverse.FAKE, BlockSapling.TYPE})));
        ModelLoader.setCustomStateMapper(reverseLeaves, new StateMapperNormal(mc.createModelResourceLocation("oak_leaves")));
        ModelLoader.setCustomStateMapper(levitation, new StateMapperNormal(mc.createModelResourceLocation("oak_leaves")));
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemModels() {
        ResourceLocationFactory mc = ResourceLocationFactory.mc;
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(reverseSapling), 0, ResourceLocationFactory.createModelResourceLocation(reverseSapling));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(reverseLeaves), 0, mc.createModelResourceLocation("oak_leaves"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(levitation), 0, mc.createModelResourceLocation("oak_leaves"));
    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockColors() {
        BlockColors blockColors = FMLClientHandler.instance().getClient().getBlockColors();
        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> (world != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(world, pos) : ColorizerFoliage.getFoliageColorBasic()), reverseLeaves);
        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> 0x004000 + (world != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(world, pos) : ColorizerFoliage.getFoliageColorBasic()), levitation);
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemColors() {
        final BlockColors blockColors = FMLClientHandler.instance().getClient().getBlockColors();
        ItemColors itemColors = FMLClientHandler.instance().getClient().getItemColors();
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(iblockstate, null, null, tintIndex);
        }, reverseLeaves, levitation);
    }

    public static void createFiles() {
        File project = new File("../1.11.2-ReverseTree");

        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, reverseSapling, "Reverse Sapling");
        language.register(LanguageResourceManager.JA_JP, reverseSapling, "逆さまの苗木");
        language.register(LanguageResourceManager.EN_US, reverseLeaves, "Reverse Leaves");
        language.register(LanguageResourceManager.JA_JP, reverseLeaves, "逆さまの葉");
        language.register(LanguageResourceManager.EN_US, levitation, "Levitation");
        language.register(LanguageResourceManager.JA_JP, levitation, "浮遊ブロック");

        ULanguageCreator.createLanguage(project, MOD_ID, language);
    }
}
