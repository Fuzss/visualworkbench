package fuzs.visualworkbench.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import fuzs.puzzleslib.json.JsonSerializationUtil;
import fuzs.visualworkbench.VisualWorkbench;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonConfigBuilder {
    public static final JsonConfigBuilder INSTANCE = new JsonConfigBuilder();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    private static final String JSON_CONFIG_NAME = "visual_workbench.json";
    private static final List<ResourceLocation> DEFAULT_VISUAL_WORKBENCHES = Stream.of(
                    "minecraft:crafting_table",
                    "blue_skies:bluebright_crafting_table",
                    "blue_skies:starlit_crafting_table",
                    "blue_skies:frostbright_crafting_table",
                    "blue_skies:lunar_crafting_table",
                    "blue_skies:dusk_crafting_table",
                    "blue_skies:maple_crafting_table",
                    "blue_skies:cherry_crafting_table",
                    "blocksplus:spruce_crafting_table",
                    "blocksplus:birch_crafting_table",
                    "blocksplus:jungle_crafting_table",
                    "blocksplus:acacia_crafting_table",
                    "blocksplus:dark_oak_crafting_table",
                    "blocksplus:crimson_crafting_table",
                    "blocksplus:warped_crafting_table",
                    "blocksplus:bamboo_crafting_table",
                    "blocksplus:mushroom_crafting_table",
                    "mctb:spruce_crafting_table",
                    "mctb:birch_crafting_table",
                    "mctb:acacia_crafting_table",
                    "mctb:jungle_crafting_table",
                    "mctb:dark_oak_crafting_table",
                    "mctb:warped_crafting_table",
                    "mctb:crimson_crafting_table",
                    "mctb:cherry_crafting_table",
                    "mctb:dead_crafting_table",
                    "mctb:fir_crafting_table",
                    "mctb:hellbark_crafting_table",
                    "mctb:jacaranda_crafting_table",
                    "mctb:magic_crafting_table",
                    "mctb:mahogany_crafting_table",
                    "mctb:palm_crafting_table",
                    "mctb:redwood_crafting_table",
                    "mctb:umbran_crafting_table",
                    "mctb:willow_crafting_table",
                    "betternether:crafting_table_anchor_tree",
                    "betternether:crafting_table_nether_sakura",
                    "betternether:crafting_table_crimson",
                    "betternether:crafting_table_warped",
                    "betternether:crafting_table_stalagnate",
                    "betternether:crafting_table_reed",
                    "betternether:crafting_table_willow",
                    "betternether:crafting_table_wart",
                    "betternether:crafting_table_rubeus",
                    "betternether:crafting_table_mushroom",
                    "betternether:crafting_table_mushroom_fir",
                    "betterendforge:mossy_glowshroom_crafting_table",
                    "betterendforge:lacugrove_crafting_table",
                    "betterendforge:end_lotus_crafting_table",
                    "betterendforge:pythadendron_crafting_table",
                    "betterendforge:dragon_tree_crafting_table",
                    "betterendforge:tenanea_crafting_table",
                    "betterendforge:helix_tree_crafting_table",
                    "betterendforge:umbrella_tree_crafting_table",
                    "betterendforge:jellyshroom_crafting_table",
                    "betterendforge:lucernia_crafting_table",
                    "crumbs:spruce_crafting_table",
                    "crumbs:birch_crafting_table",
                    "crumbs:jungle_crafting_table",
                    "crumbs:acacia_crafting_table",
                    "crumbs:dark_oak_crafting_table",
                    "crumbs:crimson_crafting_table",
                    "crumbs:warped_crafting_table",
                    "byg:aspen_crafting_table",
                    "byg:baobab_crafting_table",
                    "byg:blue_enchanted_crafting_table",
                    "byg:cherry_crafting_table",
                    "byg:cika_crafting_table",
                    "byg:cypress_crafting_table",
                    "byg:ebony_crafting_table",
                    "byg:fir_crafting_table",
                    "byg:green_enchanted_crafting_table",
                    "byg:holly_crafting_table",
                    "byg:jacaranda_crafting_table",
                    "byg:mahogany_crafting_table",
                    "byg:mangrove_crafting_table",
                    "byg:maple_crafting_table",
                    "byg:pine_crafting_table",
                    "byg:rainbow_eucalyptus_crafting_table",
                    "byg:redwood_crafting_table",
                    "byg:skyris_crafting_table",
                    "byg:willow_crafting_table",
                    "byg:witch_hazel_crafting_table",
                    "byg:zelkova_crafting_table",
                    "byg:sythian_crafting_table",
                    "byg:embur_crafting_table",
                    "byg:palm_crafting_table",
                    "byg:lament_crafting_table",
                    "byg:bulbis_crafting_table",
                    "byg:nightshade_crafting_table",
                    "byg:ether_crafting_table",
                    "byg:imparius_crafting_table",
                    "vct:spruce_crafting_table",
                    "vct:birch_crafting_table",
                    "vct:jungle_crafting_table",
                    "vct:acacia_crafting_table",
                    "vct:dark_oak_crafting_table",
                    "vct:mangrove_crafting_table",
                    "vct:crimson_crafting_table",
                    "vct:warped_crafting_table",
                    "vct:bop_cherry_crafting_table",
                    "vct:bop_dead_crafting_table",
                    "vct:bop_fir_crafting_table",
                    "vct:bop_hellbark_crafting_table",
                    "vct:bop_jacaranda_crafting_table",
                    "vct:bop_magic_crafting_table",
                    "vct:bop_mahogany_crafting_table",
                    "vct:bop_palm_crafting_table",
                    "vct:bop_redwood_crafting_table",
                    "vct:bop_umbran_crafting_table",
                    "vct:bop_willow_crafting_table",
                    "vct:canopy_crafting_table",
                    "vct:darkwood_crafting_table",
                    "vct:twilight_mangrove_crafting_table",
                    "vct:minewood_crafting_table",
                    "vct:sortingwood_crafting_table",
                    "vct:timewood_crafting_table",
                    "vct:transwood_crafting_table",
                    "vct:twilight_oak_crafting_table",
                    "vct:aspen_crafting_table",
                    "vct:grimwood_crafting_table",
                    "vct:kousa_crafting_table",
                    "vct:morado_crafting_table",
                    "vct:rosewood_crafting_table",
                    "vct:yucca_crafting_table",
                    "vct:maple_crafting_table",
                    "vct:bamboo_crafting_table",
                    "vct:azalea_crafting_table",
                    "vct:poise_crafting_table",
                    "vct:cherry_crafting_table",
                    "vct:willow_crafting_table",
                    "vct:wisteria_crafting_table",
                    "vct:driftwood_crafting_table",
                    "vct:river_crafting_table",
                    "vct:jacaranda_crafting_table",
                    "vct:redbud_crafting_table",
                    "vct:cypress_crafting_table",
                    "vct:brown_mushroom_crafting_table",
                    "vct:red_mushroom_crafting_table",
                    "vct:glowshroom_crafting_table",
                    "vct:twisted_crafting_table",
                    "vct:petrified_crafting_table",
                    "vct:eco_azalea_crafting_table",
                    "vct:eco_flowering_azalea_crafting_table",
                    "vct:eco_coconut_crafting_table",
                    "vct:eco_walnut_crafting_table",
                    "vct:fairy_ring_mushroom_crafting_table",
                    "vct:azure_crafting_table",
                    "vct:araucaria_crafting_table",
                    "vct:heidiphyllum_crafting_table",
                    "vct:liriodendrites_crafting_table",
                    "vct:metasequoia_crafting_table",
                    "vct:protojuniperoxylon_crafting_table",
                    "vct:protopiceoxylon_crafting_table",
                    "vct:zamites_crafting_table",
                    "vct:quark_azalea_crafting_table",
                    "vct:quark_blossom_crafting_table",
                    "vct:grongle_crafting_table",
                    "vct:smogstem_crafting_table",
                    "vct:wigglewood_crafting_table"
                    )
            .map(ResourceLocation::new)
            .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));

    private ResourceLocation[] locationValues = {new ResourceLocation("minecraft:crafting_table")};
    private Set<Block> blockValues;

    public Stream<Block> getBlockStream() {
        return Stream.of(this.locationValues)
                .filter(Objects::nonNull)
                .filter(Registry.BLOCK::containsKey)
                .map(Registry.BLOCK::get);
    }

    public boolean contains(Block block) {
        if (this.blockValues == null) {
            this.blockValues = this.getBlockStream().collect(Collectors.toSet());
        }
        return this.blockValues.contains(block);
    }

    public void load() {
        // this doesn't work on Fabric as there is no order in which registries are loaded,
        // so there is no guarantee modded crafting table blocks will be registered before our block entity is
        // therefore just hardcode to vanilla table
        if (!CoreServices.ENVIRONMENT.getModLoader().isForge()) return;
        JsonConfigFileUtil.getAndLoad(JSON_CONFIG_NAME, JsonConfigBuilder::serialize, (FileReader reader) -> this.locationValues = JsonConfigBuilder.deserialize(reader));
    }

    private static void serialize(File jsonFile) {
        final JsonObject jsonObject = JsonSerializationUtil.getConfigBase(String.format("Crafting table blocks to enable %s support for.", VisualWorkbench.MOD_NAME));
        Type token = new TypeToken<List<ResourceLocation>>(){}.getType();
        JsonElement jsonElement = GSON.toJsonTree(DEFAULT_VISUAL_WORKBENCHES, token);
        jsonObject.add("values", jsonElement);
        JsonConfigFileUtil.saveToFile(jsonFile, jsonObject);
    }

    private static ResourceLocation[] deserialize(FileReader reader) {
        final JsonElement values = JsonSerializationUtil.readJsonObject(reader).get("values");
        if (values != null) {
            return GSON.fromJson(values, ResourceLocation[].class);
        }
        VisualWorkbench.LOGGER.error("Unable to read {} json config file", VisualWorkbench.MOD_NAME);
        return new ResourceLocation[0];
    }
}
