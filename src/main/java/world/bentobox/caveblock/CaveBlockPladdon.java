package world.bentobox.caveblock;

import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


@Plugin(name = "CaveBlock", version = "1.0")
@ApiVersion(ApiVersion.Target.v1_18)
public class CaveBlockPladdon extends Pladdon {

    @Override
    public Addon getAddon() {
        return new CaveBlock();
    }
}
