package world.bentobox.caveblock.listeners;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import world.bentobox.bentobox.util.Util;
import world.bentobox.caveblock.CaveBlock;
import world.bentobox.caveblock.Settings;


/**
 * This class allows to enable beacon in CaveBlock, if cave roof is made of bedrock.
 * It will replace Bedrock with black glass.
 */
public class BeaconEnabler implements Listener
{
	/**
	 * Constructor BeaconEnabler creates a new BeaconEnabler instance.
	 *
	 * @param addon of type CaveBlock
	 */
	public BeaconEnabler(CaveBlock addon)
	{
		this.addon = addon;
		this.settings = addon.getSettings();
	}


	/**
	 * Method onBlockPlacement detects if beacon is placed and replace roof bedrock with black glass.
	 *
	 * @param event of type BlockPlaceEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlacement(BlockPlaceEvent event)
	{
		World world = event.getPlayer().getWorld();

		if (!Util.sameWorld(this.addon.getOverWorld(), world) ||
			!this.settings.isBeaconAllowed() ||
			!this.isRoofEnabled(world) ||
			!event.getBlock().getType().equals(Material.BEACON))
		{
			// This should work only if it is cave block world or world has roof from bedrock. Otherwise,
			// players can dig till top themself.
			return;
		}


		Block roofBlock = world.getBlockAt(event.getBlock().getX(), this.settings.getWorldDepth() - 1, event.getBlock().getZ());

		if (roofBlock.getType().equals(Material.BEDROCK))
		{
			// Replace only bedrock.
			roofBlock.setType(Material.BLACK_STAINED_GLASS);
		}
	}


	/**
	 * Method onBlockBreak detects if beacon is destroyed and replace roof black glass with bedrock.
	 *
	 * @param event of type BlockBreakEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		World world = event.getPlayer().getWorld();

		if (!Util.sameWorld(this.addon.getOverWorld(), world) ||
			!this.isRoofEnabled(world) ||
			!this.settings.isBeaconAllowed() ||
			!event.getBlock().getType().equals(Material.BEACON))
		{
			// This should work only if it is cave block world or world has roof from bedrock.
			return;
		}

		Block roofBlock = world.getBlockAt(event.getBlock().getX(), this.settings.getWorldDepth() - 1, event.getBlock().getZ());

		if (roofBlock.getType().equals(Material.BLACK_STAINED_GLASS))
		{
			// Replace only black glass.
			roofBlock.setType(Material.BEDROCK);
		}
	}


	/**
	 * Method onBlockDamage detects if user tries to destroy black glass on roof and disable it.
	 *
	 * @param event of type BlockDamageEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockDamage(BlockDamageEvent event)
	{
		World world = event.getPlayer().getWorld();

		if (!Util.sameWorld(this.addon.getOverWorld(), world) ||
			!this.isRoofEnabled(world) ||
			!this.settings.isBeaconAllowed() ||
			event.getBlock().getY() != this.settings.getWorldDepth() - 1)
		{
			// This should work only if it is cave block world or world has roof from bedrock.
			return;
		}

		// Cancel break event if it is black glass.
		event.setCancelled(event.getBlock().getType().equals(Material.BLACK_STAINED_GLASS));
	}


	/**
	 * Method onBlockExplode detects if explosion tries to destroy black glass on roof and disable it.
	 *
	 * @param event of type BlockExplodeEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockExplode(BlockExplodeEvent event)
	{
		World world = event.getBlock().getWorld();

		if (!Util.sameWorld(this.addon.getOverWorld(), world) ||
			!this.isRoofEnabled(world) ||
			!this.settings.isBeaconAllowed() ||
			event.getBlock().getY() < this.settings.getWorldDepth() - 9)
		{
			// This should work only if it is cave block world or world has roof from bedrock.
			return;
		}

		final int blockY = this.settings.getWorldDepth() - 1;

		// Remove all black stained glass from explosion block list if it is on the roof.
		event.blockList().removeIf(block ->
			block.getY() == blockY && block.getType().equals(Material.BLACK_STAINED_GLASS));
	}


	/**
	 * Method onEntityExplode detects if explosion tries to destroy black glass on roof and disable it.
	 *
	 * @param event of type EntityExplodeEvent
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		World world = event.getLocation().getWorld();

		if (!Util.sameWorld(this.addon.getOverWorld(), world) ||
			!this.isRoofEnabled(world) ||
			!this.settings.isBeaconAllowed() ||
			event.getLocation().getY() < this.settings.getWorldDepth() - 9)
		{
			// This should work only if it is cave block world or world has roof from bedrock.
			return;
		}

		final int blockY = this.settings.getWorldDepth() - 1;

		// Remove all black stained glass from explosion block list if it is on the roof.
		event.blockList().removeIf(block ->
			block.getY() == blockY && block.getType().equals(Material.BLACK_STAINED_GLASS));
	}


	/**
	 * This method checks if in given world bedrock roof is enabled.
	 * @param world World that must be checked.
 	 * @return <code>true</code> - bedrock roof is enabled, otherwise <code>false</code>
	 */
	private boolean isRoofEnabled(World world)
	{
		return world.getEnvironment().equals(World.Environment.NORMAL) && this.settings.isNormalRoof() ||
			world.getEnvironment().equals(World.Environment.NETHER) && this.settings.isNetherRoof() ||
			world.getEnvironment().equals(World.Environment.THE_END) && this.settings.isEndRoof();
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

	/**
	 * CaveBlock addon.
	 */
	private CaveBlock addon;

	/**
	 * Addon settings.
	 */
	private Settings settings;
}
