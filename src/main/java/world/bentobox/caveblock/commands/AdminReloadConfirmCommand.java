package world.bentobox.caveblock.commands;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.commands.admin.AdminReloadCommand;
import world.bentobox.bentobox.api.user.User;


/**
 * Replaces BentoBox's default admin reload command, which reloads silently,
 * so that the admin gets confirmation that config.yml was reloaded.
 */
public class AdminReloadConfirmCommand extends AdminReloadCommand {

    /**
     * Reload
     * @param adminCommand - parent command
     */
    public AdminReloadConfirmCommand(CompositeCommand adminCommand) {
        super(adminCommand);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        super.execute(user, label, args);
        user.sendMessage("caveblock.commands.admin.reload.reloaded");
        return true;
    }

}
