package world.bentobox.caveblock;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.eclipse.jdt.annotation.NonNull;

import com.google.common.base.Enums;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.StoreAt;
import world.bentobox.bentobox.api.configuration.WorldSettings;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.database.objects.adapters.Adapter;
import world.bentobox.bentobox.database.objects.adapters.FlagSerializer;
import world.bentobox.bentobox.database.objects.adapters.FlagSerializer2;


/**
 * @author BONNe
 * This class contains all necessary settings for CaveBlock addon.
 */
@StoreAt(filename="config.yml", path="addons/CaveBlock")
@ConfigComment("CaveBlock Configuration [version]")
public class Settings implements WorldSettings
{
    // ---------------------------------------------------------------------
    // Section: Getters
    // ---------------------------------------------------------------------

    /**
     * @return the maxCoopSize
     */
    @Override
    public int getMaxCoopSize() {
        return maxCoopSize;
    }

    /**
     * @return the maxTrustSize
     */
    @Override
    public int getMaxTrustSize() {
        return maxTrustSize;
    }

    /**
     * This method returns the friendlyName object.
     * @return the friendlyName object.
     */
    @Override
    public String getFriendlyName()
    {
        return friendlyName;
    }


    /**
     * This method returns the worldName object.
     * @return the worldName object.
     */
    @Override
    public String getWorldName()
    {
        return worldName;
    }


    /**
     * This method returns the difficulty object.
     * @return the difficulty object.
     */
    @Override
    public Difficulty getDifficulty()
    {
        return difficulty;
    }


    /**
     * This method returns the islandDistance object.
     * @return the islandDistance object.
     */
    @Override
    public int getIslandDistance()
    {
        return islandDistance;
    }


    /**
     * This method returns the islandProtectionRange object.
     * @return the islandProtectionRange object.
     */
    @Override
    public int getIslandProtectionRange()
    {
        return islandProtectionRange;
    }


    /**
     * This method returns the islandStartX object.
     * @return the islandStartX object.
     */
    @Override
    public int getIslandStartX()
    {
        return islandStartX;
    }


    /**
     * This method returns the islandStartZ object.
     * @return the islandStartZ object.
     */
    @Override
    public int getIslandStartZ()
    {
        return islandStartZ;
    }


    /**
     * This method returns the islandXOffset object.
     * @return the islandXOffset object.
     */
    @Override
    public int getIslandXOffset()
    {
        return islandXOffset;
    }


    /**
     * This method returns the islandZOffset object.
     * @return the islandZOffset object.
     */
    @Override
    public int getIslandZOffset()
    {
        return islandZOffset;
    }


    /**
     * This method returns the islandHeight object.
     * @return the islandHeight object.
     */
    @Override
    public int getIslandHeight()
    {
        return islandHeight;
    }


    /**
     * This is not an option in Caveblock
     * @return false
     */
    @Override
    public boolean isUseOwnGenerator()
    {
        return false;
    }


    /**
     * This method returns the seaHeight object.
     * @return the seaHeight object.
     */
    @Override
    public int getSeaHeight()
    {
        return 0;
    }


    /**
     * 0 or -1 is unlimited. It will block cave creation if the cave count for the world is higher than this.
     * @return the maxIslands
     */
    @Override
    public int getMaxIslands()
    {
        return maxIslands;
    }


    /**
     * This method returns the defaultGameMode object.
     * @return the defaultGameMode object.
     */
    @Override
    public GameMode getDefaultGameMode()
    {
        return defaultGameMode;
    }


    /**
     * This method returns the defaultBiome object.
     * @return the defaultBiome object.
     */
    public Biome getDefaultBiome()
    {
        return defaultBiome;
    }


    /**
     * This method returns the banLimit object.
     * @return the banLimit object.
     */
    @Override
    public int getBanLimit()
    {
        return banLimit;
    }


    /**
     * This method returns the netherGenerate object.
     * @return the netherGenerate object.
     */
    @Override
    public boolean isNetherGenerate()
    {
        return netherGenerate;
    }


    /**
     * This method returns the netherIslands object.
     * @return the netherIslands object.
     */
    @Override
    public boolean isNetherIslands()
    {
        return netherIslands;
    }


    /**
     * This method returns the netherRoof object.
     * @return the netherRoof object.
     */
    public boolean isNetherRoof()
    {
        return netherRoof;
    }


    /**
     * This method returns the netherSpawnRadius object.
     * @return the netherSpawnRadius object.
     */
    @Override
    public int getNetherSpawnRadius()
    {
        return netherSpawnRadius;
    }


    /**
     * This method returns the endGenerate object.
     * @return the endGenerate object.
     */
    @Override
    public boolean isEndGenerate()
    {
        return endGenerate;
    }


    /**
     * This method returns the endIslands object.
     * @return the endIslands object.
     */
    @Override
    public boolean isEndIslands()
    {
        return endIslands;
    }


    /**
     * This method returns the dragonSpawn object.
     * @return the dragonSpawn object.
     */
    @Override
    public boolean isDragonSpawn()
    {
        return dragonSpawn;
    }


    /**
     * This method returns the removeMobsWhitelist object.
     * @return the removeMobsWhitelist object.
     */
    @Override
    public Set<EntityType> getRemoveMobsWhitelist()
    {
        return removeMobsWhitelist;
    }


    /**
     * This method returns the worldFlags object.
     * @return the worldFlags object.
     */
    @Override
    public Map<String, Boolean> getWorldFlags()
    {
        return worldFlags;
    }


    /**
     * This method returns the defaultIslandFlags object.
     * @return the defaultIslandFlags object.
     */
    @Override
    public Map<Flag, Integer> getDefaultIslandFlags()
    {
        return defaultIslandFlags;
    }


    /**
     * This method returns the defaultIslandSettings object.
     * @return the defaultIslandSettings object.
     */
    @Override
    public Map<Flag, Integer> getDefaultIslandSettings()
    {
        return defaultIslandSettings;
    }


    /**
     * This method returns the hiddenFlags object.
     * @return the hiddenFlags object.
     */
    @Override
    public List<String> getHiddenFlags()
    {
        return hiddenFlags;
    }


    /**
     * This method returns the visitorBannedCommands object.
     * @return the visitorBannedCommands object.
     */
    @Override
    public List<String> getVisitorBannedCommands()
    {
        return visitorBannedCommands;
    }


    /**
     * Optional list of commands that are banned when falling. Not applicable to all
     * game modes so defaults to empty.
     * @return the fallingBannedCommands
     * @since 1.8.0
     */
    @Override
    public List<String> getFallingBannedCommands()
    {
        return this.fallingBannedCommands;
    }


    /**
     * This method returns the maxTeamSize object.
     * @return the maxTeamSize object.
     */
    @Override
    public int getMaxTeamSize()
    {
        return maxTeamSize;
    }


    /**
     * This method returns the maxHomes object.
     * @return the maxHomes object.
     */
    @Override
    public int getMaxHomes()
    {
        return maxHomes;
    }


    /**
     * This method returns the resetLimit object.
     * @return the resetLimit object.
     */
    @Override
    public int getResetLimit()
    {
        return resetLimit;
    }


    /**
     * This method returns the leaversLoseReset object.
     * @return the leaversLoseReset object.
     */
    @Override
    public boolean isLeaversLoseReset()
    {
        return leaversLoseReset;
    }


    /**
     * This method returns the kickedKeepInventory object.
     * @return the kickedKeepInventory value.
     */
    @Override
    public boolean isKickedKeepInventory()
    {
        return kickedKeepInventory;
    }


    /**
     * This method returns the createIslandOnFirstLoginEnabled boolean value.
     * @return the createIslandOnFirstLoginEnabled value
     * @since 1.9.0
     */
    @Override
    public boolean isCreateIslandOnFirstLoginEnabled()
    {
        return this.createIslandOnFirstLoginEnabled;
    }


    /**
     * This method returns the createIslandOnFirstLoginDelay integer value.
     * @return the createIslandOnFirstLoginDelay value
     * @since 1.9.0
     */
    @Override
    public int getCreateIslandOnFirstLoginDelay()
    {
        return this.createIslandOnFirstLoginDelay;
    }


    /**
     * This method returns the createIslandOnFirstLoginAbortOnLogout boolean value.
     * @return the createIslandOnFirstLoginAbortOnLogout value
     * @since 1.9.0
     */
    @Override
    public boolean isCreateIslandOnFirstLoginAbortOnLogout()
    {
        return this.createIslandOnFirstLoginAbortOnLogout;
    }


    /**
     * This method returns the onJoinResetMoney object.
     * @return the onJoinResetMoney object.
     */
    @Override
    public boolean isOnJoinResetMoney()
    {
        return onJoinResetMoney;
    }


    /**
     * This method returns the onJoinResetInventory object.
     * @return the onJoinResetInventory object.
     */
    @Override
    public boolean isOnJoinResetInventory()
    {
        return onJoinResetInventory;
    }


    /**
     * This method returns the onJoinResetEnderChest object.
     * @return the onJoinResetEnderChest object.
     */
    @Override
    public boolean isOnJoinResetEnderChest()
    {
        return onJoinResetEnderChest;
    }


    /**
     * This method returns the onLeaveResetMoney object.
     * @return the onLeaveResetMoney object.
     */
    @Override
    public boolean isOnLeaveResetMoney()
    {
        return onLeaveResetMoney;
    }


    /**
     * This method returns the onLeaveResetInventory object.
     * @return the onLeaveResetInventory object.
     */
    @Override
    public boolean isOnLeaveResetInventory()
    {
        return onLeaveResetInventory;
    }


    /**
     * This method returns the onLeaveResetEnderChest object.
     * @return the onLeaveResetEnderChest object.
     */
    @Override
    public boolean isOnLeaveResetEnderChest()
    {
        return onLeaveResetEnderChest;
    }


    /**
     * This method returns the allowSetHomeInNether object.
     * @return the allowSetHomeInNether object.
     */
    @Override
    public boolean isAllowSetHomeInNether()
    {
        return allowSetHomeInNether;
    }


    /**
     * This method returns the requireConfirmationToSetHomeInNether object.
     * @return the requireConfirmationToSetHomeInNether object.
     */
    @Override
    public boolean isRequireConfirmationToSetHomeInNether()
    {
        return requireConfirmationToSetHomeInNether;
    }


    /**
     * This method returns the allowSetHomeInTheEnd object.
     * @return the allowSetHomeInTheEnd object.
     */
    @Override
    public boolean isAllowSetHomeInTheEnd()
    {
        return allowSetHomeInTheEnd;
    }


    /**
     * This method returns the requireConfirmationToSetHomeInTheEnd object.
     * @return the requireConfirmationToSetHomeInTheEnd object.
     */
    @Override
    public boolean isRequireConfirmationToSetHomeInTheEnd()
    {
        return requireConfirmationToSetHomeInTheEnd;
    }


    /**
     * This method returns the deathsCounted object.
     * @return the deathsCounted object.
     */
    @Override
    public boolean isDeathsCounted()
    {
        return deathsCounted;
    }


    /**
     * @return true if deaths in the world are reset when the player has a new cave
     * @since 1.6.0
     */
    @Override
    public boolean isDeathsResetOnNewIsland()
    {
        return this.deathsResetOnNewIsland;
    }


    /**
     * This method returns the deathsMax object.
     * @return the deathsMax object.
     */
    @Override
    public int getDeathsMax()
    {
        return deathsMax;
    }


    /**
     * This method returns the teamJoinDeathReset object.
     * @return the teamJoinDeathReset object.
     */
    @Override
    public boolean isTeamJoinDeathReset()
    {
        return teamJoinDeathReset;
    }


    /**
     * This method returns the geoLimitSettings object.
     * @return the geoLimitSettings object.
     */
    @Override
    public List<String> getGeoLimitSettings()
    {
        return geoLimitSettings;
    }


    /**
     * This method returns the ivSettings object.
     * @return the ivSettings object.
     */
    @Override
    public List<String> getIvSettings()
    {
        return ivSettings;
    }


    /**
     * @return the permission prefix
     */
    @Override
    public String getPermissionPrefix()
    {
        return "caveblock";
    }


    /**
     * @return true if water is not safe in this world, e.g, should not be a home location
     */
    @Override
    public boolean isWaterUnsafe()
    {
        return false;
    }


    /**
     * This method returns the resetEpoch object.
     * @return the resetEpoch object.
     */
    @Override
    public long getResetEpoch()
    {
        return resetEpoch;
    }


    /**
     * This method returns the worldDepth value.
     * @return the value of worldDepth.
     */
    public int getWorldDepth()
    {
        return worldDepth;
    }


    /**
     * This method returns the normalRoof value.
     * @return the value of normalRoof.
     */
    public boolean isNormalRoof()
    {
        return normalRoof;
    }


    /**
     * This method returns the normalFloor value.
     * @return the value of normalFloor.
     */
    public boolean isNormalFloor()
    {
        return normalFloor;
    }


    /**
     * This method returns the normalMainBlock value.
     * @return the value of normalMainBlock.
     */
    public Material getNormalMainBlock()
    {
        return normalMainBlock;
    }


    /**
     * This method returns the normalBlocks value.
     * @return the value of normalBlocks.
     */
    public List<String> getNormalBlocks()
    {
        return normalBlocks;
    }


    /**
     * This method returns the netherFloor value.
     * @return the value of netherFloor.
     */
    public boolean isNetherFloor()
    {
        return netherFloor;
    }


    /**
     * This method returns the netherMainBlock value.
     * @return the value of netherMainBlock.
     */
    public Material getNetherMainBlock()
    {
        return netherMainBlock;
    }


    /**
     * This method returns the netherBlocks value.
     * @return the value of netherBlocks.
     */
    public List<String> getNetherBlocks()
    {
        return netherBlocks;
    }


    /**
     * This method returns the endRoof value.
     * @return the value of endRoof.
     */
    public boolean isEndRoof()
    {
        return endRoof;
    }


    /**
     * This method returns the endFloor value.
     * @return the value of endFloor.
     */
    public boolean isEndFloor()
    {
        return endFloor;
    }


    /**
     * This method returns the endMainBlock value.
     * @return the value of endMainBlock.
     */
    public Material getEndMainBlock()
    {
        return endMainBlock;
    }


    /**
     * This method returns the endBlocks value.
     * @return the value of endBlocks.
     */
    public List<String> getEndBlocks()
    {
        return endBlocks;
    }


    /**
     * This method returns the numberOfBlockGenerationTries value.
     * @return the value of numberOfBlockGenerationTries.
     */
    public int getNumberOfBlockGenerationTries()
    {
        return numberOfBlockGenerationTries;
    }

    /**
     * This method returns the newMaterialGenerator value.
     * @return the value of newMaterialGenerator.
     */
    public boolean isNewMaterialGenerator()
    {
        return newMaterialGenerator;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMobLimitSettings()
    {
        return mobLimitSettings;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPlayerCommandAliases()
    {
        return playerCommandAliases;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdminCommandAliases()
    {
        return adminCommandAliases;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultPlayerAction()
    {
        return defaultPlayerAction;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultNewPlayerAction()
    {
        return defaultNewPlayerAction;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMakeNetherPortals()
    {
        return this.makeNetherPortals;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMakeEndPortals()
    {
        return this.makeEndPortals;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull List<String> getOnRespawnCommands()
    {
        return this.onRespawnCommands;
    }


    // ---------------------------------------------------------------------
    // Section: Setters
    // ---------------------------------------------------------------------

    /**
     * @param maxCoopSize the maxCoopSize to set
     */
    public void setMaxCoopSize(int maxCoopSize) {
        this.maxCoopSize = maxCoopSize;
    }

    /**
     * @param maxTrustSize the maxTrustSize to set
     */
    public void setMaxTrustSize(int maxTrustSize) {
        this.maxTrustSize = maxTrustSize;
    }

    /**
     * This method sets the friendlyName object value.
     * @param friendlyName the friendlyName object new value.
     *
     */
    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }


    /**
     * This method sets the worldName object value.
     * @param worldName the worldName object new value.
     *
     */
    public void setWorldName(String worldName)
    {
        this.worldName = worldName;
    }


    /**
     * This method sets the difficulty object value.
     * @param difficulty the difficulty object new value.
     *
     */
    @Override
    public void setDifficulty(Difficulty difficulty)
    {
        this.difficulty = difficulty;
    }


    /**
     * This method sets the islandDistance object value.
     * @param islandDistance the islandDistance object new value.
     *
     */
    public void setIslandDistance(int islandDistance)
    {
        this.islandDistance = islandDistance;
    }


    /**
     * This method sets the islandProtectionRange object value.
     * @param islandProtectionRange the islandProtectionRange object new value.
     *
     */
    public void setIslandProtectionRange(int islandProtectionRange)
    {
        this.islandProtectionRange = islandProtectionRange;
    }


    /**
     * This method sets the islandStartX object value.
     * @param islandStartX the islandStartX object new value.
     *
     */
    public void setIslandStartX(int islandStartX)
    {
        this.islandStartX = islandStartX;
    }


    /**
     * This method sets the islandStartZ object value.
     * @param islandStartZ the islandStartZ object new value.
     *
     */
    public void setIslandStartZ(int islandStartZ)
    {
        this.islandStartZ = islandStartZ;
    }


    /**
     * This method sets the islandXOffset object value.
     * @param islandXOffset the islandXOffset object new value.
     *
     */
    public void setIslandXOffset(int islandXOffset)
    {
        this.islandXOffset = islandXOffset;
    }


    /**
     * This method sets the islandZOffset object value.
     * @param islandZOffset the islandZOffset object new value.
     *
     */
    public void setIslandZOffset(int islandZOffset)
    {
        this.islandZOffset = islandZOffset;
    }


    /**
     * This method sets the islandHeight object value.
     * @param islandHeight the islandHeight object new value.
     *
     */
    public void setIslandHeight(int islandHeight)
    {
        this.islandHeight = islandHeight;
    }


    /**
     * This method sets the maxIslands object value.
     * @param maxIslands the maxIslands object new value.
     *
     */
    public void setMaxIslands(int maxIslands)
    {
        this.maxIslands = maxIslands;
    }


    /**
     * This method sets the defaultGameMode object value.
     * @param defaultGameMode the defaultGameMode object new value.
     *
     */
    public void setDefaultGameMode(GameMode defaultGameMode)
    {
        this.defaultGameMode = defaultGameMode;
    }


    /**
     * This method sets the defaultBiome object value.
     * @param defaultBiome the defaultBiome object new value.
     *
     */
    public void setDefaultBiome(Biome defaultBiome)
    {
        this.defaultBiome = defaultBiome;
    }


    /**
     * This method sets the banLimit object value.
     * @param banLimit the banLimit object new value.
     *
     */
    public void setBanLimit(int banLimit)
    {
        this.banLimit = banLimit;
    }


    /**
     * This method sets the netherGenerate object value.
     * @param netherGenerate the netherGenerate object new value.
     *
     */
    public void setNetherGenerate(boolean netherGenerate)
    {
        this.netherGenerate = netherGenerate;
    }


    /**
     * This method sets the netherIslands object value.
     * @param netherIslands the netherIslands object new value.
     *
     */
    public void setNetherIslands(boolean netherIslands)
    {
        this.netherIslands = netherIslands;
    }


    /**
     * This method sets the netherRoof object value.
     * @param netherRoof the netherRoof object new value.
     *
     */
    public void setNetherRoof(boolean netherRoof)
    {
        this.netherRoof = netherRoof;
    }


    /**
     * This method sets the netherSpawnRadius object value.
     * @param netherSpawnRadius the netherSpawnRadius object new value.
     *
     */
    public void setNetherSpawnRadius(int netherSpawnRadius)
    {
        this.netherSpawnRadius = netherSpawnRadius;
    }


    /**
     * This method sets the endGenerate object value.
     * @param endGenerate the endGenerate object new value.
     *
     */
    public void setEndGenerate(boolean endGenerate)
    {
        this.endGenerate = endGenerate;
    }


    /**
     * This method sets the endIslands object value.
     * @param endIslands the endIslands object new value.
     *
     */
    public void setEndIslands(boolean endIslands)
    {
        this.endIslands = endIslands;
    }


    /**
     * This method sets the dragonSpawn object value.
     * @param dragonSpawn the dragonSpawn object new value.
     *
     */
    public void setDragonSpawn(boolean dragonSpawn)
    {
        this.dragonSpawn = dragonSpawn;
    }


    /**
     * This method sets the removeMobsWhitelist object value.
     * @param removeMobsWhitelist the removeMobsWhitelist object new value.
     *
     */
    public void setRemoveMobsWhitelist(Set<EntityType> removeMobsWhitelist)
    {
        this.removeMobsWhitelist = removeMobsWhitelist;
    }


    /**
     * This method sets the worldFlags object value.
     * @param worldFlags the worldFlags object new value.
     *
     */
    public void setWorldFlags(Map<String, Boolean> worldFlags)
    {
        this.worldFlags = worldFlags;
    }


    /**
     * This method sets the defaultIslandFlags object value.
     * @param defaultIslandFlags the defaultIslandFlags object new value.
     *
     */
    public void setDefaultIslandFlags(Map<Flag, Integer> defaultIslandFlags)
    {
        this.defaultIslandFlags = defaultIslandFlags;
    }


    /**
     * This method sets the defaultIslandSettings object value.
     * @param defaultIslandSettings the defaultIslandSettings object new value.
     *
     */
    public void setDefaultIslandSettings(Map<Flag, Integer> defaultIslandSettings)
    {
        this.defaultIslandSettings = defaultIslandSettings;
    }


    /**
     * This method sets the hiddenFlags object value.
     * @param hiddenFlags the hiddenFlags object new value.
     *
     */
    public void setHiddenFlags(List<String> hiddenFlags)
    {
        this.hiddenFlags = hiddenFlags;
    }


    /**
     * This method sets the visitorBannedCommands object value.
     * @param visitorBannedCommands the visitorBannedCommands object new value.
     *
     */
    public void setVisitorBannedCommands(List<String> visitorBannedCommands)
    {
        this.visitorBannedCommands = visitorBannedCommands;
    }


    /**
     * This method sets the fallingBannedCommands object value.
     * @param fallingBannedCommands the fallingBannedCommands object new value.
     *
     */
    public void setFallingBannedCommands(List<String> fallingBannedCommands)
    {
        this.fallingBannedCommands = fallingBannedCommands;
    }


    /**
     * This method sets the maxTeamSize object value.
     * @param maxTeamSize the maxTeamSize object new value.
     *
     */
    public void setMaxTeamSize(int maxTeamSize)
    {
        this.maxTeamSize = maxTeamSize;
    }


    /**
     * This method sets the maxHomes object value.
     * @param maxHomes the maxHomes object new value.
     *
     */
    public void setMaxHomes(int maxHomes)
    {
        this.maxHomes = maxHomes;
    }


    /**
     * This method sets the resetLimit object value.
     * @param resetLimit the resetLimit object new value.
     *
     */
    public void setResetLimit(int resetLimit)
    {
        this.resetLimit = resetLimit;
    }


    /**
     * This method sets the leaversLoseReset object value.
     * @param leaversLoseReset the leaversLoseReset object new value.
     *
     */
    public void setLeaversLoseReset(boolean leaversLoseReset)
    {
        this.leaversLoseReset = leaversLoseReset;
    }


    /**
     * This method sets the kickedKeepInventory object value.
     * @param kickedKeepInventory the kickedKeepInventory object new value.
     *
     */
    public void setKickedKeepInventory(boolean kickedKeepInventory)
    {
        this.kickedKeepInventory = kickedKeepInventory;
    }


    /**
     * This method sets the onJoinResetMoney object value.
     * @param onJoinResetMoney the onJoinResetMoney object new value.
     *
     */
    public void setOnJoinResetMoney(boolean onJoinResetMoney)
    {
        this.onJoinResetMoney = onJoinResetMoney;
    }


    /**
     * This method sets the onJoinResetInventory object value.
     * @param onJoinResetInventory the onJoinResetInventory object new value.
     *
     */
    public void setOnJoinResetInventory(boolean onJoinResetInventory)
    {
        this.onJoinResetInventory = onJoinResetInventory;
    }


    /**
     * This method sets the onJoinResetEnderChest object value.
     * @param onJoinResetEnderChest the onJoinResetEnderChest object new value.
     *
     */
    public void setOnJoinResetEnderChest(boolean onJoinResetEnderChest)
    {
        this.onJoinResetEnderChest = onJoinResetEnderChest;
    }


    /**
     * This method sets the onLeaveResetMoney object value.
     * @param onLeaveResetMoney the onLeaveResetMoney object new value.
     *
     */
    public void setOnLeaveResetMoney(boolean onLeaveResetMoney)
    {
        this.onLeaveResetMoney = onLeaveResetMoney;
    }


    /**
     * This method sets the onLeaveResetInventory object value.
     * @param onLeaveResetInventory the onLeaveResetInventory object new value.
     *
     */
    public void setOnLeaveResetInventory(boolean onLeaveResetInventory)
    {
        this.onLeaveResetInventory = onLeaveResetInventory;
    }


    /**
     * This method sets the onLeaveResetEnderChest object value.
     * @param onLeaveResetEnderChest the onLeaveResetEnderChest object new value.
     *
     */
    public void setOnLeaveResetEnderChest(boolean onLeaveResetEnderChest)
    {
        this.onLeaveResetEnderChest = onLeaveResetEnderChest;
    }


    /**
     * This method sets the createCaveOnFirstLoginEnabled object value.
     * @param createIslandOnFirstLoginEnabled the createCaveOnFirstLoginEnabled object new value.
     *
     */
    public void setCreateIslandOnFirstLoginEnabled(boolean createIslandOnFirstLoginEnabled)
    {
        this.createIslandOnFirstLoginEnabled = createIslandOnFirstLoginEnabled;
    }

    /**
     * This method sets the createCaveOnFirstLoginDelay object value.
     * @param createIslandOnFirstLoginDelay the createCaveOnFirstLoginDelay object new value.
     *
     */
    public void setCreateIslandOnFirstLoginDelay(int createIslandOnFirstLoginDelay)
    {
        this.createIslandOnFirstLoginDelay = createIslandOnFirstLoginDelay;
    }


    /**
     * This method sets the createCaveOnFirstLoginDelay object value.
     * @param createIslandOnFirstLoginAbortOnLogout the createCaveOnFirstLoginDelay object new value.
     *
     */
    public void setCreateIslandOnFirstLoginAbortOnLogout(boolean createIslandOnFirstLoginAbortOnLogout)
    {
        this.createIslandOnFirstLoginAbortOnLogout = createIslandOnFirstLoginAbortOnLogout;
    }


    /**
     * This method sets the allowSetHomeInNether object value.
     * @param allowSetHomeInNether the allowSetHomeInNether object new value.
     *
     */
    public void setAllowSetHomeInNether(boolean allowSetHomeInNether)
    {
        this.allowSetHomeInNether = allowSetHomeInNether;
    }


    /**
     * This method sets the requireConfirmationToSetHomeInNether object value.
     * @param requireConfirmationToSetHomeInNether the requireConfirmationToSetHomeInNether object new value.
     *
     */
    public void setRequireConfirmationToSetHomeInNether(boolean requireConfirmationToSetHomeInNether)
    {
        this.requireConfirmationToSetHomeInNether = requireConfirmationToSetHomeInNether;
    }


    /**
     * This method sets the allowSetHomeInTheEnd object value.
     * @param allowSetHomeInTheEnd the allowSetHomeInTheEnd object new value.
     *
     */
    public void setAllowSetHomeInTheEnd(boolean allowSetHomeInTheEnd)
    {
        this.allowSetHomeInTheEnd = allowSetHomeInTheEnd;
    }


    /**
     * This method sets the requireConfirmationToSetHomeInTheEnd object value.
     * @param requireConfirmationToSetHomeInTheEnd the requireConfirmationToSetHomeInTheEnd object new value.
     *
     */
    public void setRequireConfirmationToSetHomeInTheEnd(boolean requireConfirmationToSetHomeInTheEnd)
    {
        this.requireConfirmationToSetHomeInTheEnd = requireConfirmationToSetHomeInTheEnd;
    }


    /**
     * This method sets the deathsCounted object value.
     * @param deathsCounted the deathsCounted object new value.
     *
     */
    public void setDeathsCounted(boolean deathsCounted)
    {
        this.deathsCounted = deathsCounted;
    }


    /**
     * This method sets the deathsResetOnNewIsland value.
     * @param deathsResetOnNewIsland the deathsResetOnNewIsland new value.
     *
     */
    public void setDeathsResetOnNewIsland(boolean deathsResetOnNewIsland)
    {
        this.deathsResetOnNewIsland = deathsResetOnNewIsland;
    }


    /**
     * This method sets the deathsMax object value.
     * @param deathsMax the deathsMax object new value.
     *
     */
    public void setDeathsMax(int deathsMax)
    {
        this.deathsMax = deathsMax;
    }


    /**
     * This method sets the teamJoinDeathReset object value.
     * @param teamJoinDeathReset the teamJoinDeathReset object new value.
     *
     */
    public void setTeamJoinDeathReset(boolean teamJoinDeathReset)
    {
        this.teamJoinDeathReset = teamJoinDeathReset;
    }


    /**
     * This method sets the geoLimitSettings object value.
     * @param geoLimitSettings the geoLimitSettings object new value.
     *
     */
    public void setGeoLimitSettings(List<String> geoLimitSettings)
    {
        this.geoLimitSettings = geoLimitSettings;
    }


    /**
     * This method sets the ivSettings object value.
     * @param ivSettings the ivSettings object new value.
     *
     */
    public void setIvSettings(List<String> ivSettings)
    {
        this.ivSettings = ivSettings;
    }

    /**
     * This method sets the resetEpoch object value.
     * @param resetEpoch the resetEpoch object new value.
     *
     */
    @Override
    public void setResetEpoch(long resetEpoch)
    {
        this.resetEpoch = resetEpoch;
    }


    /**
     * This method sets the worldDepth value.
     * @param worldDepth the worldDepth new value.
     *
     */
    public void setWorldDepth(int worldDepth)
    {
        this.worldDepth = worldDepth;
    }


    /**
     * This method sets the normalRoof value.
     * @param normalRoof the normalRoof new value.
     *
     */
    public void setNormalRoof(boolean normalRoof)
    {
        this.normalRoof = normalRoof;
    }


    /**
     * This method sets the normalFloor value.
     * @param normalFloor the normalFloor new value.
     *
     */
    public void setNormalFloor(boolean normalFloor)
    {
        this.normalFloor = normalFloor;
    }


    /**
     * This method sets the normalMainBlock value.
     * @param normalMainBlock the normalMainBlock new value.
     *
     */
    public void setNormalMainBlock(Material normalMainBlock)
    {
        this.normalMainBlock = normalMainBlock;
    }


    /**
     * This method sets the normalBlocks value.
     * @param normalBlocks the normalBlocks new value.
     *
     */
    public void setNormalBlocks(List<String> normalBlocks)
    {
        this.normalBlocks = normalBlocks;
    }


    /**
     * This method sets the netherFloor value.
     * @param netherFloor the netherFloor new value.
     *
     */
    public void setNetherFloor(boolean netherFloor)
    {
        this.netherFloor = netherFloor;
    }


    /**
     * This method sets the netherMainBlock value.
     * @param netherMainBlock the netherMainBlock new value.
     *
     */
    public void setNetherMainBlock(Material netherMainBlock)
    {
        this.netherMainBlock = netherMainBlock;
    }


    /**
     * This method sets the netherBlocks value.
     * @param netherBlocks the netherBlocks new value.
     *
     */
    public void setNetherBlocks(List<String> netherBlocks)
    {
        this.netherBlocks = netherBlocks;
    }


    /**
     * This method sets the endRoof value.
     * @param endRoof the endRoof new value.
     *
     */
    public void setEndRoof(boolean endRoof)
    {
        this.endRoof = endRoof;
    }


    /**
     * This method sets the endFloor value.
     * @param endFloor the endFloor new value.
     *
     */
    public void setEndFloor(boolean endFloor)
    {
        this.endFloor = endFloor;
    }


    /**
     * This method sets the endMainBlock value.
     * @param endMainBlock the endMainBlock new value.
     *
     */
    public void setEndMainBlock(Material endMainBlock)
    {
        this.endMainBlock = endMainBlock;
    }


    /**
     * This method sets the endBlocks value.
     * @param endBlocks the endBlocks new value.
     *
     */
    public void setEndBlocks(List<String> endBlocks)
    {
        this.endBlocks = endBlocks;
    }


    /**
     * This method sets the numberOfBlockGenerationTries value.
     * @param numberOfBlockGenerationTries the numberOfBlockGenerationTries new value.
     *
     */
    public void setNumberOfBlockGenerationTries(int numberOfBlockGenerationTries)
    {
        this.numberOfBlockGenerationTries = numberOfBlockGenerationTries;
    }


    /**
     * This method sets the newMaterialGenerator value.
     * @param newMaterialGenerator the numberOfBlockGenerationTries new value.
     *
     */
    public void setNewMaterialGenerator(boolean newMaterialGenerator)
    {
        this.newMaterialGenerator = newMaterialGenerator;
    }


    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }


    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    /**
     * This method sets the playerCommandAliases value.
     * @param playerCommandAliases the playerCommandAliases new value.
     *
     */
    public void setPlayerCommandAliases(String playerCommandAliases)
    {
        this.playerCommandAliases = playerCommandAliases;
    }


    /**
     * This method sets the adminCommandAliases value.
     * @param adminCommandAliases the adminCommandAliases new value.
     *
     */
    public void setAdminCommandAliases(String adminCommandAliases)
    {
        this.adminCommandAliases = adminCommandAliases;
    }

    /**
     * @return the onJoinCommands
     */
    @NonNull
    @Override
    public List<String> getOnJoinCommands() {
        return onJoinCommands;
    }


    /**
     * @param onJoinCommands the onJoinCommands to set
     */
    public void setOnJoinCommands(List<String> onJoinCommands) {
        this.onJoinCommands = onJoinCommands;
    }


    /**
     * @return the onLeaveCommands
     */
    @NonNull
    @Override
    public List<String> getOnLeaveCommands() {
        return onLeaveCommands;
    }


    /**
     * @param onLeaveCommands the onLeaveCommands to set
     */
    public void setOnLeaveCommands(List<String> onLeaveCommands) {
        this.onLeaveCommands = onLeaveCommands;
    }

    /**
     * @return the onJoinResetHealth
     */
    @Override
    public boolean isOnJoinResetHealth() {
        return onJoinResetHealth;
    }


    /**
     * @param onJoinResetHealth the onJoinResetHealth to set
     */
    public void setOnJoinResetHealth(boolean onJoinResetHealth) {
        this.onJoinResetHealth = onJoinResetHealth;
    }


    /**
     * @return the onJoinResetHunger
     */
    @Override
    public boolean isOnJoinResetHunger() {
        return onJoinResetHunger;
    }


    /**
     * @param onJoinResetHunger the onJoinResetHunger to set
     */
    public void setOnJoinResetHunger(boolean onJoinResetHunger) {
        this.onJoinResetHunger = onJoinResetHunger;
    }


    /**
     * @return the onJoinResetXP
     */
    @Override
    public boolean isOnJoinResetXP() {
        return onJoinResetXP;
    }


    /**
     * @param onJoinResetXP the onJoinResetXP to set
     */
    public void setOnJoinResetXP(boolean onJoinResetXP) {
        this.onJoinResetXP = onJoinResetXP;
    }


    /**
     * @return the onLeaveResetHealth
     */
    @Override
    public boolean isOnLeaveResetHealth() {
        return onLeaveResetHealth;
    }


    /**
     * @param onLeaveResetHealth the onLeaveResetHealth to set
     */
    public void setOnLeaveResetHealth(boolean onLeaveResetHealth) {
        this.onLeaveResetHealth = onLeaveResetHealth;
    }


    /**
     * @return the onLeaveResetHunger
     */
    @Override
    public boolean isOnLeaveResetHunger() {
        return onLeaveResetHunger;
    }


    /**
     * @param onLeaveResetHunger the onLeaveResetHunger to set
     */
    public void setOnLeaveResetHunger(boolean onLeaveResetHunger) {
        this.onLeaveResetHunger = onLeaveResetHunger;
    }


    /**
     * @return the onLeaveResetXP
     */
    @Override
    public boolean isOnLeaveResetXP() {
        return onLeaveResetXP;
    }


    /**
     * @param onLeaveResetXP the onLeaveResetXP to set
     */
    public void setOnLeaveResetXP(boolean onLeaveResetXP) {
        this.onLeaveResetXP = onLeaveResetXP;
    }


    /**
     * Method Settings#isTeleportPlayerToIslandUponIslandCreation returns the teleportPlayerToIslandUponIslandCreation of this object.
     *
     * @return the teleportPlayerToIslandUponIslandCreation (type boolean) of this object.
     */
    @Override
    public boolean isTeleportPlayerToIslandUponIslandCreation()
    {
        return teleportPlayerToIslandUponIslandCreation;
    }


    /**
     * Method Settings#setTeleportPlayerToIslandUponIslandCreation sets new value for the teleportPlayerToIslandUponIslandCreation of this object.
     * @param teleportPlayerToIslandUponIslandCreation new value for this object.
     *
     */
    public void setTeleportPlayerToIslandUponIslandCreation(boolean teleportPlayerToIslandUponIslandCreation)
    {
        this.teleportPlayerToIslandUponIslandCreation = teleportPlayerToIslandUponIslandCreation;
    }


    /**
     * Method Settings#isPasteMissingIslands returns the pasteMissingIslands of this object.
     *
     * @return the pasteMissingIslands (type boolean) of this object.
     */
    @Override
    public boolean isPasteMissingIslands()
    {
        return pasteMissingIslands;
    }


    /**
     * Method Settings#setPasteMissingIslands sets new value for the pasteMissingIslands of this object.
     * @param pasteMissingIslands new value for this object.
     *
     */
    public void setPasteMissingIslands(boolean pasteMissingIslands)
    {
        this.pasteMissingIslands = pasteMissingIslands;
    }


    /**
     * Method Settings#setDefaultPlayerAction sets new value for the defaultPlayerAction of this object.
     * @param defaultPlayerAction new value for this object.
     */
    public void setDefaultPlayerAction(String defaultPlayerAction)
    {
        this.defaultPlayerAction = defaultPlayerAction;
    }


    /**
     * Method Settings#setDefaultNewPlayerAction sets new value for the defaultNewPlayerAction of this object.
     * @param defaultNewPlayerAction new value for this object.
     */
    public void setDefaultNewPlayerAction(String defaultNewPlayerAction)
    {
        this.defaultNewPlayerAction = defaultNewPlayerAction;
    }


    /**
     * Method Settings#setMobLimitSettings sets new value for the mobLimitSettings of this object.
     * @param mobLimitSettings new value for this object.
     */
    public void setMobLimitSettings(List<String> mobLimitSettings)
    {
        this.mobLimitSettings = mobLimitSettings;
    }


    /**
     * Method Settings#getDefaultNetherBiome returns the defaultNetherBiome of this object.
     *
     * @return the defaultNetherBiome (type Biome) of this object.
     */
    public Biome getDefaultNetherBiome()
    {
        return defaultNetherBiome;
    }


    /**
     * Method Settings#setDefaultNetherBiome sets new value for the defaultNetherBiome of this object.
     * @param defaultNetherBiome new value for this object.
     *
     */
    public void setDefaultNetherBiome(Biome defaultNetherBiome)
    {
        this.defaultNetherBiome = defaultNetherBiome;
    }


    /**
     * Method Settings#getDefaultTheEndBiome returns the defaultTheEndBiome of this object.
     *
     * @return the defaultTheEndBiome (type Biome) of this object.
     */
    public Biome getDefaultTheEndBiome()
    {
        return defaultTheEndBiome;
    }


    /**
     * Method Settings#setDefaultTheEndBiome sets new value for the defaultTheEndBiome of this object.
     * @param defaultTheEndBiome new value for this object.
     *
     */
    public void setDefaultTheEndBiome(Biome defaultTheEndBiome)
    {
        this.defaultTheEndBiome = defaultTheEndBiome;
    }

    /**
     * @return the spawnLimitMonsters
     */
    public int getSpawnLimitMonsters() {
        return spawnLimitMonsters;
    }

    /**
     * @param spawnLimitMonsters the spawnLimitMonsters to set
     */
    public void setSpawnLimitMonsters(int spawnLimitMonsters) {
        this.spawnLimitMonsters = spawnLimitMonsters;
    }

    /**
     * @return the spawnLimitAnimals
     */
    public int getSpawnLimitAnimals() {
        return spawnLimitAnimals;
    }

    /**
     * @param spawnLimitAnimals the spawnLimitAnimals to set
     */
    public void setSpawnLimitAnimals(int spawnLimitAnimals) {
        this.spawnLimitAnimals = spawnLimitAnimals;
    }

    /**
     * @return the spawnLimitWaterAnimals
     */
    public int getSpawnLimitWaterAnimals() {
        return spawnLimitWaterAnimals;
    }

    /**
     * @param spawnLimitWaterAnimals the spawnLimitWaterAnimals to set
     */
    public void setSpawnLimitWaterAnimals(int spawnLimitWaterAnimals) {
        this.spawnLimitWaterAnimals = spawnLimitWaterAnimals;
    }

    /**
     * @return the spawnLimitAmbient
     */
    public int getSpawnLimitAmbient() {
        return spawnLimitAmbient;
    }

    /**
     * @param spawnLimitAmbient the spawnLimitAmbient to set
     */
    public void setSpawnLimitAmbient(int spawnLimitAmbient) {
        this.spawnLimitAmbient = spawnLimitAmbient;
    }

    /**
     * @return the ticksPerAnimalSpawns
     */
    public int getTicksPerAnimalSpawns() {
        return ticksPerAnimalSpawns;
    }

    /**
     * @param ticksPerAnimalSpawns the ticksPerAnimalSpawns to set
     */
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        this.ticksPerAnimalSpawns = ticksPerAnimalSpawns;
    }

    /**
     * @return the ticksPerMonsterSpawns
     */
    public int getTicksPerMonsterSpawns() {
        return ticksPerMonsterSpawns;
    }

    /**
     * @param ticksPerMonsterSpawns the ticksPerMonsterSpawns to set
     */
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        this.ticksPerMonsterSpawns = ticksPerMonsterSpawns;
    }

    /* (non-Javadoc)
     * @see world.bentobox.bentobox.api.configuration.WorldSettings#isCheckForBlocks()
     */
    @Override
    public boolean isCheckForBlocks() {
        // Do not check for blocks when looking for new island spots
        return false;
    }


    /**
     * Sets make nether portals.
     *
     * @param makeNetherPortals the make nether portals
     */
    public void setMakeNetherPortals(boolean makeNetherPortals)
    {
        this.makeNetherPortals = makeNetherPortals;
    }


    /**
     * Sets make end portals.
     *
     * @param makeEndPortals the make end portals
     */
    public void setMakeEndPortals(boolean makeEndPortals)
    {
        this.makeEndPortals = makeEndPortals;
    }


    /**
     * Sets on respawn commands.
     *
     * @param onRespawnCommands the on respawn commands
     */
    public void setOnRespawnCommands(List<String> onRespawnCommands)
    {
        this.onRespawnCommands = onRespawnCommands;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------




    /* Commands */
    @ConfigComment("Cave Command. What command users will run to access their cave.")
    @ConfigComment("To define alias, just separate commands with white space.")
    @ConfigEntry(path = "caveblock.command.cave")
    private String playerCommandAliases = "cave cb";

    @ConfigComment("The Cave admin command.")
    @ConfigComment("To define alias, just separate commands with white space.")
    @ConfigEntry(path = "caveblock.command.admin")
    private String adminCommandAliases = "cbadmin cba";

    @ConfigComment("The default action for new player command call.")
    @ConfigComment("Sub-command of main player command that will be run on first player command call.")
    @ConfigComment("By default it is sub-command 'create'.")
    @ConfigEntry(path = "caveblock.command.new-player-action", since = "1.13.0")
    private String defaultNewPlayerAction = "create";

    @ConfigComment("The default action for player command.")
    @ConfigComment("Sub-command of main player command that will be run on each player command call.")
    @ConfigComment("By default it is sub-command 'go'.")
    @ConfigEntry(path = "caveblock.command.default-action", since = "1.13.0")
    private String defaultPlayerAction = "go";

    /*      WORLD       */
    @ConfigComment("Friendly name for this world. Used in admin commands. Must be a single word")
    @ConfigEntry(path = "world.friendly-name")
    private String friendlyName = "CaveBlock";

    @ConfigComment("Name of the world - if it does not exist then it will be generated.")
    @ConfigComment("It acts like a prefix for nether and end (e.g. CaveBlock-world, CaveBlock-world_nether, CaveBlock-world_end)")
    @ConfigEntry(path = "world.world-name")
    private String worldName = "caveblock-world";

    @ConfigComment("World difficulty setting - PEACEFUL, EASY, NORMAL, HARD")
    @ConfigComment("Other plugins may override this setting")
    @ConfigEntry(path = "world.difficulty")
    private Difficulty difficulty = Difficulty.HARD;

    @ConfigComment("Spawn limits. These override the limits set in bukkit.yml")
    @ConfigComment("If set to a negative number, the server defaults will be used")
    @ConfigEntry(path = "world.spawn-limits.monsters", since = "1.15.1")
    private int spawnLimitMonsters = -1;
    @ConfigEntry(path = "world.spawn-limits.animals", since = "1.15.1")
    private int spawnLimitAnimals = -1;
    @ConfigEntry(path = "world.spawn-limits.water-animals", since = "1.15.1")
    private int spawnLimitWaterAnimals = -1;
    @ConfigEntry(path = "world.spawn-limits.ambient", since = "1.15.1")
    private int spawnLimitAmbient = -1;
    @ConfigComment("Setting to 0 will disable animal spawns, but this is not recommended. Minecraft default is 400.")
    @ConfigComment("A negative value uses the server default")
    @ConfigEntry(path = "world.spawn-limits.ticks-per-animal-spawns", since = "1.15.1")
    private int ticksPerAnimalSpawns = -1;
    @ConfigComment("Setting to 0 will disable monster spawns, but this is not recommended. Minecraft default is 400.")
    @ConfigComment("A negative value uses the server default")
    @ConfigEntry(path = "world.spawn-limits.ticks-per-monster-spawns", since = "1.15.1")
    private int ticksPerMonsterSpawns = -1;

    @ConfigComment("Radius of cave in blocks. (So distance between caves is twice this)")
    @ConfigComment("Will be rounded up to the nearest 16 blocks.")
    @ConfigComment("It is the same for every dimension : Overworld, Nether and End.")
    @ConfigComment("This value cannot be changed mid-game and the plugin will not start if it is different.")
    @ConfigEntry(path = "world.distance-between-caves", needsReset = true)
    private int islandDistance = 100;

    @ConfigComment("Default protection range radius in blocks. Cannot be larger than distance.")
    @ConfigComment("Admins can change protection sizes for players individually using /cbadmin range set <player> <new range>")
    @ConfigComment("or set this permission: caveblock.island.range.<number>")
    @ConfigEntry(path = "world.protection-range")
    private int islandProtectionRange = 50;

    @ConfigComment("Start caves at these coordinates. This is where new caves will start in the")
    @ConfigComment("world. These must be a factor of your cave distance, but the plugin will auto")
    @ConfigComment("calculate the closest location on the grid. Caves develop around this location")
    @ConfigComment("both positively and negatively in a square grid.")
    @ConfigComment("If none of this makes sense, leave it at 0,0.")
    @ConfigEntry(path = "world.start-x", needsReset = true)
    private int islandStartX = 0;

    @ConfigEntry(path = "world.start-z", needsReset = true)
    private int islandStartZ = 0;

    @ConfigEntry(path = "world.offset-x")
    private int islandXOffset;
    @ConfigEntry(path = "world.offset-z")
    private int islandZOffset;

    @ConfigComment("Cave height - Lowest is 5.")
    @ConfigComment("It is the y coordinate of the bedrock block in the schem.")
    @ConfigEntry(path = "world.cave-height")
    private int islandHeight = 60;

    @ConfigComment("Maximum number of caves in the world. Set to -1 or 0 for unlimited.")
    @ConfigComment("If the number of caves is greater than this number, it will stop players from creating caves.")
    @ConfigEntry(path = "world.max-caves")
    private int maxIslands = -1;

    @ConfigComment("The default game mode for this world. Players will be set to this mode when they create")
    @ConfigComment("a new cave for example. Options are SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR")
    @ConfigEntry(path = "world.default-game-mode")
    private GameMode defaultGameMode = GameMode.SURVIVAL;

    @ConfigComment("The default biome for the overworld")
    @ConfigEntry(path = "world.default-biome")
    private Biome defaultBiome = Enums.getIfPresent(Biome.class, "DRIPSTONE_CAVES").or(Biome.THE_VOID);

    @ConfigComment("The maximum number of players a player can ban at any one time in this game mode.")
    @ConfigComment("The permission caveblock.ban.maxlimit.X where X is a number can also be used per player")
    @ConfigComment("-1 = unlimited")
    @ConfigEntry(path = "world.ban-limit")
    private int banLimit = -1;

    @ConfigComment("")
    @ConfigComment("This is cave.. no height... only depth. Max 256.")
    @ConfigComment("Should not be less then cave height.")
    @ConfigEntry(path = "world.world-depth", needsReset = true)
    private int worldDepth = 256;

    @ConfigComment("This indicate how many times block should be tried to generate.")
    @ConfigEntry(path = "world.generation-tries", needsReset = true)
    private int numberOfBlockGenerationTries = 1;

    @ConfigComment("Should we use the new material generator ?")
    @ConfigComment("This will generate ores and blocks similar to how vanilla does,")
    @ConfigComment("but it will override the blocks settings of each world.")
    @ConfigEntry(path = "world.use-new-material-generator", needsReset = true)
    private boolean newMaterialGenerator = false;

    @ConfigComment("")
    @ConfigComment("Make over world roof of bedrock, if false, it will be made from stone")
    @ConfigEntry(path = "world.normal.roof", needsReset = true)
    private boolean normalRoof = true;

    @ConfigComment("Make over world floor of bedrock, if false, it will be made from stone")
    @ConfigEntry(path = "world.normal.floor", needsReset = true)
    private boolean normalFloor = true;

    @ConfigComment("Main block of which world will be generated.")
    @ConfigEntry(path = "world.normal.main-block", needsReset = true)
    private Material normalMainBlock = Material.STONE;

    @ConfigComment("Blocks that will occasionally replace main block by random chance.")
    @ConfigComment("Blocks will replace only main-block and will try to create packs that")
    @ConfigComment("are set in their strings. Chance of spawning also is required.")
    @ConfigComment("For materials first string must be MATERIAL, for entity: ENTITY.")
    @ConfigComment("Entities spawned via generator are not protected from despawing.")
    @ConfigComment("Working only with 2 high mobs currently.")
    @ConfigComment("Example:")
    @ConfigComment("MATERIAL:DIAMOND_ORE:100:5 - means there is 100% chace of spawing diamonds")
    @ConfigComment("where max amount in pack are 5 per each subchunk!")
    @ConfigEntry(path = "world.normal.blocks", needsReset = true)
    private List<String> normalBlocks = new ArrayList<>();

    // Nether
    @ConfigComment("Generate Nether - if this is false, the nether world will not be made and access to")
    @ConfigComment("the nether will not occur. Other plugins may still enable portal usage.")
    @ConfigComment("Note: Some default challenges will not be possible if there is no nether.")
    @ConfigComment("Note that with a standard nether all players arrive at the same portal and entering a")
    @ConfigComment("portal will return them back to their caves.")
    @ConfigEntry(path = "world.nether.generate")
    private boolean netherGenerate = true;

    @ConfigComment("Caves in Nether. Change to false for standard vanilla nether.")
    @ConfigEntry(path = "world.nether.caves", needsReset = true)
    private boolean netherIslands = true;

    @ConfigComment("The default biome for the nether world (this may affect what mobs can spawn)")
    @ConfigEntry(path = "world.nether.biome", since = "1.14.0")
    private Biome defaultNetherBiome = Enums.getIfPresent(Biome.class, "NETHER_WASTES").or(Biome.THE_VOID);

    @ConfigComment("Nether spawn protection radius - this is the distance around the nether spawn")
    @ConfigComment("that will be protected from player interaction (breaking blocks, pouring lava etc.)")
    @ConfigComment("Minimum is 0 (not recommended), maximum is 100. Default is 25.")
    @ConfigComment("Only applies to vanilla nether")
    @ConfigEntry(path = "world.nether.spawn-radius")
    private int netherSpawnRadius = 32;

    @ConfigComment("Make over world roof of bedrock, if false, it will be made from stone")
    @ConfigEntry(path = "world.nether.roof", needsReset = true)
    private boolean netherRoof = true;

    @ConfigComment("Make over world floor of bedrock, if false, it will be made from stone")
    @ConfigEntry(path = "world.nether.floor", needsReset = true)
    private boolean netherFloor = true;

    @ConfigComment("Main block of which world will be generated.")
    @ConfigEntry(path = "world.nether.main-block", needsReset = true)
    private Material netherMainBlock = Material.NETHERRACK;

    @ConfigComment("Blocks that will occasionally replace main block by random chance.")
    @ConfigComment("Blocks will replace only main-block and will try to create packs that")
    @ConfigComment("are set in their strings. Chance of spawning also is required.")
    @ConfigComment("For materials first string must be MATERIAL, for entity: ENTITY.")
    @ConfigComment("Entities spawned via generator are not protected from despawing.")
    @ConfigComment("Working only with 2 high mobs currently.")
    @ConfigComment("Example:")
    @ConfigComment("MATERIAL:DIAMOND_ORE:100:5 - means there is 100% chace of spawing diamonds")
    @ConfigComment("where max amount in pack are 5 per each subchunk!")
    @ConfigEntry(path = "world.nether.blocks", needsReset = true)
    private List<String> netherBlocks = new ArrayList<>();

    @ConfigComment("This option indicates if nether portals should be linked via dimensions.")
    @ConfigComment("Option will simulate vanilla portal mechanics that links portals together")
    @ConfigComment("or creates a new portal, if there is not a portal in that dimension.")
    @ConfigEntry(path = "world.nether.create-and-link-portals")
    private boolean makeNetherPortals = false;

    // End
    @ConfigEntry(path = "world.end.generate")
    private boolean endGenerate = true;

    @ConfigEntry(path = "world.end.caves", needsReset = true)
    private boolean endIslands = true;

    @ConfigComment("The default biome for the end world (this may affect what mobs can spawn)")
    @ConfigEntry(path = "world.end.biome", since = "1.14.0")
    private Biome defaultTheEndBiome = Enums.getIfPresent(Biome.class, "THE_END").or(Biome.THE_VOID);

    @ConfigEntry(path = "world.end.dragon-spawn", experimental = true)
    private boolean dragonSpawn = false;

    @ConfigComment("Make over world roof of bedrock, if false, it will be made from stone")
    @ConfigEntry(path = "world.end.roof", needsReset = true)
    private boolean endRoof = true;

    @ConfigComment("Make over world floor of bedrock, if false, it will be made from stone")
    @ConfigEntry(path = "world.end.floor", needsReset = true)
    private boolean endFloor = true;

    @ConfigComment("Main block of which world will be generated.")
    @ConfigEntry(path = "world.end.main-block", needsReset = true)
    private Material endMainBlock = Material.END_STONE;

    @ConfigComment("Blocks that will occasionally replace main block by random chance.")
    @ConfigComment("Blocks will replace only main-block and will try to create packs that")
    @ConfigComment("are set in their strings. Chance of spawning also is required.")
    @ConfigComment("For materials first string must be MATERIAL, for entity: ENTITY.")
    @ConfigComment("Entities spawned via generator are not protected from despawing.")
    @ConfigComment("Working only with 2 high mobs currently.")
    @ConfigComment("Example:")
    @ConfigComment("MATERIAL:DIAMOND_ORE:100:5 - means there is 100% chace of spawing diamonds")
    @ConfigComment("where max amount in pack are 5 per each subchunk!")
    @ConfigEntry(path = "world.end.blocks", needsReset = true)
    private List<String> endBlocks = new ArrayList<>();

    @ConfigComment("This option indicates if obsidian platform in the end should be generated")
    @ConfigComment("when player enters the end world.")
    @ConfigEntry(path = "world.end.create-obsidian-platform")
    private boolean makeEndPortals = false;

    // Other staff.

    @ConfigComment("Mob white list - these mobs will NOT be removed when logging in or doing /cave")
    @ConfigEntry(path = "world.remove-mobs-whitelist")
    private Set<EntityType> removeMobsWhitelist = new HashSet<>();

    @ConfigComment("World flags. These are boolean settings for various flags for this world")
    @ConfigEntry(path = "world.flags")
    private Map<String, Boolean> worldFlags = new HashMap<>();

    @ConfigComment("These are the default protection settings for new caves.")
    @ConfigComment("The value is the minimum cave rank required allowed to do the action.")
    @ConfigComment("Ranks are the following:")
    @ConfigComment("  VISITOR   = 0")
    @ConfigComment("  COOP      = 200")
    @ConfigComment("  TRUSTED   = 400")
    @ConfigComment("  MEMBER    = 500")
    @ConfigComment("  SUB-OWNER = 900")
    @ConfigComment("  OWNER     = 1000")
    @ConfigEntry(path = "world.default-cave-flags")
    @Adapter(FlagSerializer.class)
    private Map<Flag, Integer> defaultIslandFlags = new HashMap<>();

    @ConfigComment("These are the default settings for new caves")
    @ConfigEntry(path = "world.default-cave-settings")
    @Adapter(FlagSerializer2.class)
    private Map<Flag, Integer> defaultIslandSettings = new HashMap<>();

    @ConfigComment("These settings/flags are hidden from users")
    @ConfigComment("Ops can toggle hiding in-game using SHIFT-LEFT-CLICK on flags in settings")
    @ConfigEntry(path = "world.hidden-flags")
    private List<String> hiddenFlags = new ArrayList<>();

    @ConfigComment("Visitor banned commands - Visitors to caves cannot use these commands in this world")
    @ConfigEntry(path = "world.visitor-banned-commands")
    private List<String> visitorBannedCommands = new ArrayList<>();

    @ConfigComment("Falling banned commands - players cannot use these commands when falling")
    @ConfigComment("if the PREVENT_TELEPORT_WHEN_FALLING world setting flag is active")
    @ConfigEntry(path = "world.falling-banned-commands")
    private List<String> fallingBannedCommands = new ArrayList<>();

    // ---------------------------------------------

    /*      Cave      */
    @ConfigComment("Default max team size")
    @ConfigComment("Permission size cannot be less than the default below. ")
    @ConfigEntry(path = "cave.max-team-size")
    private int maxTeamSize = 4;

    @ConfigComment("Default maximum number of coop rank members per cave")
    @ConfigComment("Players can have the caveblock.coop.maxsize.<number> permission to be bigger but")
    @ConfigComment("permission size cannot be less than the default below. ")
    @ConfigEntry(path = "cave.max-coop-size", since = "1.13.0")
    private int maxCoopSize = 4;

    @ConfigComment("Default maximum number of trusted rank members per cave")
    @ConfigComment("Players can have the caveblock.trust.maxsize.<number> permission to be bigger but")
    @ConfigComment("permission size cannot be less than the default below. ")
    @ConfigEntry(path = "cave.max-trusted-size", since = "1.13.0")
    private int maxTrustSize = 4;

    @ConfigComment("Default maximum number of homes a player can have. Min = 1")
    @ConfigComment("Accessed via /cave sethome <number> or /cave go <number>")
    @ConfigEntry(path = "cave.max-homes")
    private int maxHomes = 5;

    // Reset
    @ConfigComment("How many resets a player is allowed (override with /cbadmin clearresets <player>)")
    @ConfigComment("Value of -1 means unlimited, 0 means hardcore - no resets.")
    @ConfigComment("Example, 2 resets means they get 2 resets or 3 caves lifetime")
    @ConfigEntry(path = "cave.reset.reset-limit")
    private int resetLimit = -1;

    @ConfigComment("Kicked or leaving players lose resets")
    @ConfigComment("Players who leave a team will lose a cave reset chance")
    @ConfigComment("If a player has zero resets left and leaves a team, they cannot make a new")
    @ConfigComment("cave by themselves and can only join a team.")
    @ConfigComment("Leave this true to avoid players exploiting free caves")
    @ConfigEntry(path = "cave.reset.leavers-lose-reset")
    private boolean leaversLoseReset = false;

    @ConfigComment("Allow kicked players to keep their inventory.")
    @ConfigComment("Overrides the on-leave inventory reset for kicked players.")
    @ConfigEntry(path = "cave.reset.kicked-keep-inventory")
    private boolean kickedKeepInventory = false;

    @ConfigComment("What the plugin should reset when the player joins or creates a cave")
    @ConfigComment("Reset Money - if this is true, will reset the player's money to the starting money")
    @ConfigComment("Recommendation is that this is set to true, but if you run multi-worlds")
    @ConfigComment("make sure your economy handles multi-worlds too.")
    @ConfigEntry(path = "cave.reset.on-join.money")
    private boolean onJoinResetMoney = false;

    @ConfigComment("Reset inventory - if true, the player's inventory will be cleared.")
    @ConfigComment("Note: if you have MultiInv running or a similar inventory control plugin, that")
    @ConfigComment("plugin may still reset the inventory when the world changes.")
    @ConfigEntry(path = "cave.reset.on-join.inventory")
    private boolean onJoinResetInventory = false;

    @ConfigComment("Reset health - if true, the player's health will be reset.")
    @ConfigEntry(path = "cave.reset.on-join.health")
    private boolean onJoinResetHealth = true;

    @ConfigComment("Reset hunger - if true, the player's hunger will be reset.")
    @ConfigEntry(path = "cave.reset.on-join.hunger")
    private boolean onJoinResetHunger = true;

    @ConfigComment("Reset experience points - if true, the player's experience will be reset.")
    @ConfigEntry(path = "cave.reset.on-join.exp")
    private boolean onJoinResetXP = false;

    @ConfigComment("Reset Ender Chest - if true, the player's Ender Chest will be cleared.")
    @ConfigEntry(path = "cave.reset.on-join.ender-chest")
    private boolean onJoinResetEnderChest = false;

    @ConfigComment("What the plugin should reset when the player leaves or is kicked from a cave")
    @ConfigComment("Reset Money - if this is true, will reset the player's money to the starting money")
    @ConfigComment("Recommendation is that this is set to true, but if you run multi-worlds")
    @ConfigComment("make sure your economy handles multi-worlds too.")
    @ConfigEntry(path = "cave.reset.on-leave.money")
    private boolean onLeaveResetMoney = false;

    @ConfigComment("Reset inventory - if true, the player's inventory will be cleared.")
    @ConfigComment("Note: if you have MultiInv running or a similar inventory control plugin, that")
    @ConfigComment("plugin may still reset the inventory when the world changes.")
    @ConfigEntry(path = "cave.reset.on-leave.inventory")
    private boolean onLeaveResetInventory = false;

    @ConfigComment("Reset health - if true, the player's health will be reset.")
    @ConfigEntry(path = "cave.reset.on-leave.health")
    private boolean onLeaveResetHealth = false;

    @ConfigComment("Reset hunger - if true, the player's hunger will be reset.")
    @ConfigEntry(path = "cave.reset.on-leave.hunger")
    private boolean onLeaveResetHunger = false;

    @ConfigComment("Reset experience - if true, the player's experience will be reset.")
    @ConfigEntry(path = "cave.reset.on-leave.exp")
    private boolean onLeaveResetXP = false;

    @ConfigComment("Reset Ender Chest - if true, the player's Ender Chest will be cleared.")
    @ConfigEntry(path = "cave.reset.on-leave.ender-chest")
    private boolean onLeaveResetEnderChest = false;

    @ConfigComment("Toggles the automatic cave creation upon the player's first login on your server.")
    @ConfigComment("If set to true,")
    @ConfigComment("   * Upon connecting to your server for the first time, the player will be told that")
    @ConfigComment("    a cave will be created for him.")
    @ConfigComment("  * Make sure you have a Blueprint Bundle called \"default\": this is the one that will")
    @ConfigComment("    be used to create the cave.")
    @ConfigComment("  * A cave will be created for the player without needing him to run the create command.")
    @ConfigComment("If set to false, this will disable this feature entirely.")
    @ConfigComment("Warning:")
    @ConfigComment("  * If you are running multiple gamemodes on your server, and all of them have")
    @ConfigComment("    this feature enabled, a cave in all the gamemodes will be created simultaneously.")
    @ConfigComment("    However, it is impossible to know on which cave the player will be teleported to afterwards.")
    @ConfigComment("  * Cave creation can be resource-intensive, please consider the options below to help mitigate")
    @ConfigComment("    the potential issues, especially if you expect a lot of players to connect to your server")
    @ConfigComment("    in a limited period of time.")
    @ConfigEntry(path = "cave.create-cave-on-first-login.enable")
    private boolean createIslandOnFirstLoginEnabled;

    @ConfigComment("Time in seconds after the player logged in, before his cave gets created.")
    @ConfigComment("If set to 0 or less, the cave will be created directly upon the player's login.")
    @ConfigComment("It is recommended to keep this value under a minute's time.")
    @ConfigEntry(path = "cave.create-cave-on-first-login.delay")
    private int createIslandOnFirstLoginDelay = 5;

    @ConfigComment("Toggles whether the cave creation should be aborted if the player logged off while the")
    @ConfigComment("delay (see the option above) has not worn off yet.")
    @ConfigComment("If set to true,")
    @ConfigComment("  * If the player has logged off the server while the delay (see the option above) has not")
    @ConfigComment("    worn off yet, this will cancel the cave creation.")
    @ConfigComment("  * If the player relogs afterward, since he will not be recognized as a new player, no cave")
    @ConfigComment("    would be created for him.")
    @ConfigComment("  * If the cave creation started before the player logged off, it will continue.")
    @ConfigComment("If set to false, the player's cave will be created even if he went offline in the meantime.")
    @ConfigComment("Note this option has no effect if the delay (see the option above) is set to 0 or less.")
    @ConfigEntry(path = "cave.create-cave-on-first-login.abort-on-logout")
    private boolean createIslandOnFirstLoginAbortOnLogout = true;

    @ConfigComment("Toggles whether the player should be teleported automatically to his cave when it is created.")
    @ConfigComment("If set to false, the player will be told his cave is ready but will have to teleport to his cave using the command.")
    @ConfigEntry(path = "cave.teleport-player-to-cave-when-created", since = "1.10.0")
    private boolean teleportPlayerToIslandUponIslandCreation = true;

    @ConfigComment("Create Nether or End cave if they are missing when a player goes through a portal.")
    @ConfigComment("Nether and End cave are usually pasted when a player makes their cave, but if they are")
    @ConfigComment("missing for some reason, you can switch this on.")
    @ConfigComment("Note that bedrock removal glitches can exploit this option.")
    @ConfigEntry(path = "cave.create-missing-nether-end-caves", since = "1.10.0")
    private boolean pasteMissingIslands = false;

    // Commands
    @ConfigComment("List of commands to run when a player joins an cave or creates one.")
    @ConfigComment("These commands are run by the console, unless otherwise stated using the [SUDO] prefix,")
    @ConfigComment("in which case they are executed by the player.")
    @ConfigComment("")
    @ConfigComment("Available placeholders for the commands are the following:")
    @ConfigComment("   * [name]: name of the player")
    @ConfigComment("")
    @ConfigComment("Here are some examples of valid commands to execute:")
    @ConfigComment("   * '[SUDO] bbox version'")
    @ConfigComment("   * 'bsbadmin deaths set [player] 0'")
    @ConfigComment("")
    @ConfigComment("Note that player-executed commands might not work, as these commands can be run with said player being offline.")
    @ConfigEntry(path = "cave.commands.on-join", since = "1.8.0")
    private List<String> onJoinCommands = new ArrayList<>();

    @ConfigComment("List of commands to run when a player leaves a cave, resets his cave or gets kicked from it.")
    @ConfigComment("These commands are run by the console, unless otherwise stated using the [SUDO] prefix,")
    @ConfigComment("in which case they are executed by the player.")
    @ConfigComment("")
    @ConfigComment("Available placeholders for the commands are the following:")
    @ConfigComment("   * [name]: name of the player")
    @ConfigComment("")
    @ConfigComment("Here are some examples of valid commands to execute:")
    @ConfigComment("   * '[SUDO] bbox version'")
    @ConfigComment("   * 'bsbadmin deaths set [player] 0'")
    @ConfigComment("")
    @ConfigComment("Note that player-executed commands might not work, as these commands can be run with said player being offline.")
    @ConfigEntry(path = "cave.commands.on-leave", since = "1.8.0")
    private List<String> onLeaveCommands = new ArrayList<>();

    @ConfigComment("List of commands that should be executed when the player respawns after death if Flags.ISLAND_RESPAWN is true.")
    @ConfigComment("These commands are run by the console, unless otherwise stated using the [SUDO] prefix,")
    @ConfigComment("in which case they are executed by the player.")
    @ConfigComment("")
    @ConfigComment("Available placeholders for the commands are the following:")
    @ConfigComment("   * [name]: name of the player")
    @ConfigComment("")
    @ConfigComment("Here are some examples of valid commands to execute:")
    @ConfigComment("   * '[SUDO] bbox version'")
    @ConfigComment("   * 'bsbadmin deaths set [player] 0'")
    @ConfigComment("")
    @ConfigComment("Note that player-executed commands might not work, as these commands can be run with said player being offline.")
    @ConfigEntry(path = "cave.commands.on-respawn", since = "1.14.0")
    private List<String> onRespawnCommands = new ArrayList<>();

    // Sethome
    @ConfigComment("Allow setting home in the nether. Only available on nether islands, not vanilla nether.")
    @ConfigEntry(path = "cave.sethome.nether.allow")
    private boolean allowSetHomeInNether = true;

    @ConfigEntry(path = "cave.sethome.nether.require-confirmation")
    private boolean requireConfirmationToSetHomeInNether = true;

    @ConfigComment("Allow setting home in the end. Only available on end islands, not vanilla end.")
    @ConfigEntry(path = "cave.sethome.the-end.allow")
    private boolean allowSetHomeInTheEnd = true;

    @ConfigEntry(path = "cave.sethome.the-end.require-confirmation")
    private boolean requireConfirmationToSetHomeInTheEnd = true;

    // Deaths
    @ConfigComment("Whether deaths are counted or not.")
    @ConfigEntry(path = "cave.deaths.counted")
    private boolean deathsCounted = true;

    @ConfigComment("Maximum number of deaths to count. The death count can be used by add-ons.")
    @ConfigEntry(path = "cave.deaths.max")
    private int deathsMax = 10;

    @ConfigComment("Reset player death count when they start a new cave or reset a cave")
    @ConfigEntry(path = "cave.deaths.reset-on-new")
    private boolean deathsResetOnNewIsland = true;

    @ConfigComment("When a player joins a team, reset their death count")
    @ConfigEntry(path = "cave.deaths.team-join-reset")
    private boolean teamJoinDeathReset = true;

    // ---------------------------------------------
    /*      PROTECTION      */

    @ConfigComment("Geo restrict mobs.")
    @ConfigComment("Mobs that exit the cave space where they were spawned will be removed.")
    @ConfigEntry(path = "protection.geo-limit-settings")
    private List<String> geoLimitSettings = new ArrayList<>();

    @ConfigComment("CaveBlock blocked mobs.")
    @ConfigComment("List of mobs that should not spawn in the CaveBlock.")
    @ConfigEntry(path = "protection.block-mobs", since = "1.13.0")
    private List<String> mobLimitSettings = new ArrayList<>();

    // Invincible visitor settings
    @ConfigComment("Invincible visitors. List of damages that will not affect visitors.")
    @ConfigComment("Make list blank if visitors should receive all damages")
    @ConfigEntry(path = "protection.invincible-visitors")
    private List<String> ivSettings = new ArrayList<>();

    //---------------------------------------------------------------------------------------/
    @ConfigComment("These settings should not be edited")
    @ConfigEntry(path = "do-not-edit-these-settings.reset-epoch")
    private long resetEpoch = 0;
    private boolean debug;
}
