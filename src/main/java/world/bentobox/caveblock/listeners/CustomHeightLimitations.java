package world.bentobox.caveblock.listeners;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.caveblock.CaveBlock;


/**
 * This listener checks player movement. If enabled, players will be deny to get over world depth limit and
 * if alternative teleports is enabled, then falling in void also will be processed.
 */
public class CustomHeightLimitations implements Listener
{
	/**
	 * Simple constructor
	 * @param addon
	 */
	public CustomHeightLimitations(CaveBlock addon)
	{
		this.addon = addon;
		this.worldHeight = addon.getSettings().getWorldDepth() - 1;
	}

	/**
	 * Method onPlayerMove disables movement if user tries to get on top of the world.
	 * It allows movement only downwards.
	 *
	 * @param event of type PlayerMoveEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		final double nextY = event.getTo() == null ? 1 : event.getTo().getY();

		if (this.shouldNotBeCancelled(nextY, player, event.getFrom(), event.getTo()))
		{
			// interested only in movements that is above height limit.
			return;
		}


		// Prevent to get over world height
		if (nextY >= this.worldHeight)
		{
			User.getInstance(player).sendMessage("caveblock.cave-limit-reached");
			event.setCancelled(true);
		}
	}


	/**
	 * Method onPlayerTeleport disables all teleports that involves moving on top of the world.
	 *
	 * @param event of type PlayerTeleportEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		final double nextY = event.getTo() == null ? 1 : event.getTo().getY();

		if (this.shouldNotBeCancelled(nextY, player, event.getFrom(), event.getTo()))
		{
			// interested only in movements that is below 0 or above height limit.
			return;
		}

		// Prevent to get over world height
		if (nextY >= this.worldHeight)
		{
			User.getInstance(player).sendMessage("caveblock.general.errors.cave-limit-reached");
			event.setCancelled(true);
		}
	}


	/**
	 * This method checks and returns if current player movement from location to to location should be
	 * managed by current addon.
	 * @param nextY NextY location for player.
 	 * @param player Player who makes movement
	 * @param from Start location
	 * @param to Next location
	 * @return {@code true} if addon should not manage current movement, {@code false} otherwise.
	 */
	private boolean shouldNotBeCancelled(double nextY, Player player, Location from, Location to)
	{
		return nextY < this.worldHeight ||
			from == to ||
			from.getY() >= nextY &&
				from.getBlockX() == to.getBlockX() &&
				from.getBlockZ() == to.getBlockZ() ||
			player.isOp() ||
			player.isDead() ||
			player.getGameMode().equals(GameMode.CREATIVE) ||
			player.getGameMode().equals(GameMode.SPECTATOR) ||
			player.hasPermission("caveblock.skywalker") ||
			this.addon.getPlayers().isInTeleport(player.getUniqueId()) ||
			!Util.sameWorld(this.addon.getOverWorld(), player.getWorld()) ||
			CaveBlock.SKY_WALKER_FLAG.isSetForWorld(player.getWorld());
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * CaveBlock addon
	 */
	private CaveBlock addon;

	/**
	 * This variable store world height.
	 */
	private int worldHeight;
}
