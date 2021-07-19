package fuzs.visualworkbench;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VisualWorkbench.MODID)
public class VisualWorkbench {

    public static final String MODID = "visualworkbench";
    public static final String NAME = "Visual Workbench";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static final ElementRegistry REGISTRY = PuzzlesLib.create(MODID);

    public static final AbstractElement VISUAL_WORKBENCH = REGISTRY.register("visual_workbench", VisualWorkbenchElement::new);

    public VisualWorkbench() {

        PuzzlesLib.setup(true);
    }

}
