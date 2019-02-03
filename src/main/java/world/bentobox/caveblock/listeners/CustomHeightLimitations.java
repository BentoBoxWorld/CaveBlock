package world.bentobox.caveblock.listeners;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.bentobox.util.teleport.SafeSpotTeleport;
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
		final double nextY = event.getTo().getY();

		if (this.addon.getSettings().isSkyWalking() ||
			player.isOp() ||
			player.isDead() ||
			player.getGameMode().equals(GameMode.CREATIVE) ||
			player.getGameMode().equals(GameMode.SPECTATOR) ||
			this.addon.getPlayers().isInTeleport(player.getUniqueId()) ||
			player.hasPermission("caveblock.skywalker") ||
			!Util.sameWorld(this.addon.getOverWorld(), player.getWorld()) ||
			nextY > 0 && nextY < this.worldHeight ||
			// Next check will allow to go down, but never up.
			event.getFrom().getBlockY() <= event.getFrom().getBlockY() &&
				event.getFrom().getBlockX() == event.getTo().getBlockX() &&
				event.getFrom().getBlockZ() == event.getTo().getBlockZ())
		{
			// interested only in movements that is below 0 or above height limit.
			return;
		}

		// Use custom teleport to different world
		if (this.addon.getSettings().isAlternativeTeleports() && nextY <= 0)
		{
			switch (player.getWorld().getEnvironment())
			{
				case NORMAL:
				{
					// From normal world users will get to nether.

					Location to = this.addon.getIslands().getIslandAt(event.getFrom()).
						map(i -> i.getSpawnPoint(World.Environment.NETHER)).
						orElse(event.getFrom().toVector().toLocation(this.addon.getNetherWorld()));

					event.setCancelled(true);

					new SafeSpotTeleport.Builder(this.addon.getPlugin()).
						entity(event.getPlayer()).
						location(to).
						portal().
						build();

					break;
				}
				case NETHER:
				{
					// From nether world users will get to the end.

					Location to = this.addon.getIslands().getIslandAt(event.getFrom()).
						map(i -> i.getSpawnPoint(World.Environment.THE_END)).
						orElse(event.getFrom().toVector().toLocation(this.addon.getEndWorld()));

					event.setCancelled(true);

					new SafeSpotTeleport.Builder(this.addon.getPlugin()).
						entity(event.getPlayer()).
						location(to).
						portal().
						build();

					break;
				}
				case THE_END:
				{
					// From the end users will get to over world.

					Location to = this.addon.getIslands().getIslandAt(event.getFrom()).
						map(i -> i.getSpawnPoint(World.Environment.NORMAL)).
						orElse(event.getFrom().toVector().toLocation(this.addon.getOverWorld()));

					event.setCancelled(true);

					new SafeSpotTeleport.Builder(this.addon.getPlugin()).
						entity(event.getPlayer()).
						location(to).
						portal().
						build();
					break;
				}
				default:
					break;
			}

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
	 * Method onPlayerTeleport disables all teleports that involves moving on top of the world.
	 *
	 * @param event of type PlayerTeleportEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		final double nextY = event.getTo().getY();

		if (this.addon.getSettings().isSkyWalking() ||
			player.isOp() ||
			player.isDead() ||
			player.getGameMode().equals(GameMode.CREATIVE) ||
			player.getGameMode().equals(GameMode.SPECTATOR) ||
			this.addon.getPlayers().isInTeleport(player.getUniqueId()) ||
			player.hasPermission("caveblock.skywalker") ||
			!Util.sameWorld(this.addon.getOverWorld(), player.getWorld()) ||
			nextY > 0 && nextY < this.worldHeight ||
			// Next check will allow to go down, but never up.
			event.getFrom().getBlockY() <= event.getFrom().getBlockY() &&
				event.getFrom().getBlockX() == event.getTo().getBlockX() &&
				event.getFrom().getBlockZ() == event.getTo().getBlockZ())
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
