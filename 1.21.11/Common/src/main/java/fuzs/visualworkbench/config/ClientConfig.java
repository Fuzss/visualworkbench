package fuzs.visualworkbench.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Makes crafting table contents lay flat on the table instead of floating above.")
    public IngredientRendering ingredientRendering = IngredientRendering.FLOATING;
    @Config(description = "Render the result of the crafting operation in addition to crafting table contents.")
    public boolean resultRendering = true;
    @Config(description = "Rotate crafting table contents so they always face the closest player.")
    public RotateIngredients rotateIngredients = RotateIngredients.CLOSEST_PLAYER;

    public enum RotateIngredients {
        CLOSEST_PLAYER, CRAFTING_PLAYER, NEVER
    }

    public enum IngredientRendering {
        NONE, FLAT, FLOATING
    }
}
