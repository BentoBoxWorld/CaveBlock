package world.bentobox.caveblock;


import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


public class CaveBlockPladdon extends Pladdon {

    @Override
    public Addon getAddon() {
        return new CaveBlock();
    }
}
