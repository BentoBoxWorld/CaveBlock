package world.bentobox.caveblock;


import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


public class CaveBlockPladdon extends Pladdon {

    private Addon addon;

    @Override
    public Addon getAddon() {
        if (addon == null) {
            addon = new CaveBlock();
        }
        return addon;
    }
}
