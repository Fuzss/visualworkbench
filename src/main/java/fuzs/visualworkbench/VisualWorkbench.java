package fuzs.visualworkbench;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VisualWorkbench.MODID)
public class VisualWorkbench {

    public static final String MODID = "visualworkbench";
    public static final String NAME = "Visual Workbench";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final AbstractElement VISUAL_WORKBENCH = PuzzlesLib.register(MODID, "visual_workbench", VisualWorkbenchElement::new);

    public VisualWorkbench() {

        PuzzlesLib.setup(false);
    }

}
