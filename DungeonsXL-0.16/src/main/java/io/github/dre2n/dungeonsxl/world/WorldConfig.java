/*
 * Copyright (C) 2012-2018 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.caliburn.CaliburnAPI;
import io.github.dre2n.caliburn.item.UniversalItemStack;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.game.GameRuleProvider;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.util.DeserializationUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * The world configuration is a simple game rule source.
 * Besides game rules, WorldConfig also stores some map specific data such as the invited players.
 * It is used directly in dungeon map config.yml files, but also part of dungeon and main config files.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class WorldConfig extends GameRuleProvider {

    CompatibilityHandler compat = CompatibilityHandler.getInstance();

    private File file;

    private List<String> invitedPlayers = new ArrayList<>();
    private GameType forcedGameType;

    public WorldConfig() {
    }

    public WorldConfig(File file) {
        this.file = file;
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
        load(configFile);
    }

    public WorldConfig(ConfigurationSection configFile) {
        load(configFile);
    }

    // Load & Save
    public void load(ConfigurationSection configFile) {
        /* Messages */
        ConfigurationSection configSectionMessages = configFile.getConfigurationSection("message");
        if (configSectionMessages != null) {
            Set<String> list = configSectionMessages.getKeys(false);
            for (String messagePath : list) {
                int messageId = NumberUtil.parseInt(messagePath);
                setMessage(messageId, configSectionMessages.getString(messagePath));
            }
        }

        /* Secure Objects */
        if (configFile.contains("secureObjects")) {
            if (Version.andHigher(Version.MC1_9).contains(compat.getVersion())) {
                secureObjects = UniversalItemStack.deserializeList(configFile.getList("secureObjects"));
            } else {
                secureObjects = DeserializationUtil.deserializeStackList(configFile.getStringList("secureObjects"));
            }
        }

        /* Invited Players */
        if (configFile.contains("invitedPlayers")) {
            invitedPlayers = configFile.getStringList("invitedPlayers");

        }

        /* Keep Inventory */
        if (configFile.contains("keepInventory")) {
            if (!configFile.contains("keepInventoryOnEnter")) {
                keepInventoryOnEnter = configFile.getBoolean("keepInventory");
            }
            if (!configFile.contains("keepInventoryOnEscape")) {
                keepInventoryOnEscape = configFile.getBoolean("keepInventory");
            }
            if (!configFile.contains("keepInventoryOnFinish")) {
                keepInventoryOnFinish = configFile.getBoolean("keepInventory");
            }
        }

        if (configFile.contains("keepInventoryOnEnter")) {
            keepInventoryOnEnter = configFile.getBoolean("keepInventoryOnEnter");
        }

        if (configFile.contains("keepInventoryOnEscape")) {
            keepInventoryOnEscape = configFile.getBoolean("keepInventoryOnEscape");
        }

        if (configFile.contains("keepInventoryOnFinish")) {
            keepInventoryOnFinish = configFile.getBoolean("keepInventoryOnFinish");
        }

        if (configFile.contains("keepInventoryOnDeath")) {
            keepInventoryOnDeath = configFile.getBoolean("keepInventoryOnDeath");
        }

        /* World interaction */
        if (configFile.contains("gameMode")) {
            if (EnumUtil.isValidEnum(GameMode.class, configFile.getString("gameMode").toUpperCase())) {
                gameMode = GameMode.valueOf(configFile.getString("gameMode"));
            } else {
                gameMode = GameMode.getByValue(configFile.getInt("gameMode"));
            }
        }

        if (configFile.contains("breakBlocks")) {
            breakBlocks = configFile.getBoolean("breakBlocks");
        }

        if (configFile.contains("breakPlacedBlocks")) {
            breakPlacedBlocks = configFile.getBoolean("breakPlacedBlocks");
        }

        if (configFile.contains("breakWhitelist")) {
            breakWhitelist = new HashMap<>();
            for (Entry<String, Object> entry : configFile.getConfigurationSection("breakWhitelist").getValues(true).entrySet()) {
                Material breakable = Material.matchMaterial(entry.getKey());

                HashSet<Material> tools = new HashSet<>();
                if (entry.getValue() instanceof List) {
                    for (String materialString : (List<String>) entry.getValue()) {
                        Material tool = Material.matchMaterial(materialString);
                        if (tool != null) {
                            tools.add(tool);
                        }
                    }
                }

                breakWhitelist.put(breakable, tools);
            }
        }

        if (configFile.contains("placeBlocks")) {
            placeBlocks = configFile.getBoolean("placeBlocks");
        }

        if (configFile.contains("placeWhitelist")) {
            placeWhitelist = new HashSet<>();
            for (String materialString : configFile.getStringList("placeWhitelist")) {
                Material material = Material.matchMaterial(materialString);
                if (material != null) {
                    placeWhitelist.add(material);
                }
            }
        }

        /* PvP */
        if (configFile.contains("playerVersusPlayer")) {
            playerVersusPlayer = configFile.getBoolean("playerVersusPlayer");
        }

        /* Friendly fire */
        if (configFile.contains("friendlyFire")) {
            friendlyFire = configFile.getBoolean("friendlyFire");
        }

        /* Lives */
        if (configFile.contains("initialLives")) {
            initialLives = configFile.getInt("initialLives");
        }

        if (configFile.contains("initialGroupLives")) {
            initialGroupLives = configFile.getInt("initialGroupLives");
        }

        /* Lobby */
        if (configFile.contains("isLobbyDisabled")) {
            lobbyDisabled = configFile.getBoolean("isLobbyDisabled");
        }

        /* Times */
        if (configFile.contains("timeToNextPlay")) {
            timeToNextPlay = configFile.getInt("timeToNextPlay");
        }

        if (configFile.contains("timeToNextLoot")) {
            timeToNextLoot = configFile.getInt("timeToNextLoot");
        }

        if (configFile.contains("timeToNextWave")) {
            timeToNextWave = configFile.getInt("timeToNextWave");
        }

        if (configFile.contains("timeUntilKickOfflinePlayer")) {
            timeUntilKickOfflinePlayer = configFile.getInt("timeUntilKickOfflinePlayer");
        }

        if (configFile.contains("timeToFinish")) {
            timeToFinish = configFile.getInt("timeToFinish");
        }

        /* Dungeon Requirements */
        if (configFile.contains("requirements")) {
            if (requirements == null) {
                requirements = new ArrayList<>();
            }

            ConfigurationSection requirementSection = configFile.getConfigurationSection("requirements");
            for (String identifier : configFile.getConfigurationSection("requirements").getKeys(false)) {
                Requirement requirement = Requirement.create(DungeonsXL.getInstance().getRequirementTypes().getByIdentifier(identifier));
                requirement.setup(requirementSection);
                requirements.add(requirement);
            }
        }

        if (configFile.contains("mustFinishOne")) {
            finishedOne = configFile.getStringList("mustFinishOne");
        } else {
            finishedOne = new ArrayList<>();
        }

        if (configFile.contains("mustFinishAll")) {
            finishedAll = configFile.getStringList("mustFinishAll");
        } else {
            finishedAll = new ArrayList<>();
        }

        if (configFile.contains("timeLastPlayed")) {
            timeLastPlayed = configFile.getInt("timeLastPlayed");
        }

        if (configFile.contains("gameCommandWhitelist")) {
            gameCommandWhitelist = configFile.getStringList("gameCommandWhitelist");
        }

        if (configFile.contains("gamePermissions")) {
            gamePermissions = configFile.getStringList("gamePermissions");
        }

        if (configFile.contains("forcedGameType")) {
            forcedGameType = DungeonsXL.getInstance().getGameTypes().getByName(configFile.getString("forcedGameType"));
        }

        if (configFile.contains("title.title")) {
            title = configFile.getString("title.title");
        }

        if (configFile.contains("title.subtitle")) {
            subtitle = configFile.getString("title.subtitle");
        }

        if (configFile.contains("title.actionBar")) {
            actionBar = configFile.getString("title.actionBar");
        }

        if (configFile.contains("title.chat")) {
            chat = configFile.getString("title.chat");
        }

        if (configFile.contains("title.fadeIn")) {
            titleFadeIn = (int) configFile.getDouble("title.fadeIn") * 20;
        }

        if (configFile.contains("title.fadeOut")) {
            titleFadeOut = (int) configFile.getDouble("title.fadeOut") * 20;
        }

        if (configFile.contains("title.show")) {
            titleShow = (int) configFile.getDouble("title.show") * 20;
        }

        if (configFile.contains("groupTagEnabled")) {
            groupTagEnabled = configFile.getBoolean("groupTagEnabled");
        }
    }

    public void save() {
        if (file == null) {
            return;
        }
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        // Messages
        if (msgs != null) {
            for (int msgs : this.msgs.keySet()) {
                configFile.set("message." + msgs, this.msgs.get(msgs));
            }
        }

        List<String> secureObjectIds = new ArrayList<>();

        for (ItemStack item : getSecureObjects()) {
            secureObjectIds.add(CaliburnAPI.getInstance().getItems().getCustomItemId(item));
        }

        configFile.set("secureObjects", secureObjects);

        // Invited Players
        configFile.set("invitedPlayers", invitedPlayers);

        try {
            configFile.save(file);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @return the UUIDs or names of the players invited to edit the map
     */
    public CopyOnWriteArrayList<String> getInvitedPlayers() {
        CopyOnWriteArrayList<String> tmpInvitedPlayers = new CopyOnWriteArrayList<>();
        tmpInvitedPlayers.addAll(invitedPlayers);
        return tmpInvitedPlayers;
    }

    /**
     * @param uuid
     * the player's unique ID
     */
    public void addInvitedPlayer(String uuid) {
        if (!invitedPlayers.contains(uuid)) {
            invitedPlayers.add(uuid);
        }
    }

    /**
     * @param uuid
     * the player's unique ID
     * @param name
     * the player's name
     */
    public void removeInvitedPlayers(String uuid, String name) {
        invitedPlayers.remove(uuid);
        // remove player from a 0.9.1 and lower file
        invitedPlayers.remove(name);
    }

    /**
     * @return the forcedGameType
     */
    public GameType getForcedGameType() {
        return forcedGameType;
    }

    /**
     * @param forcedGameType
     * the forcedGameType to set
     */
    public void setForcedGameType(GameType forcedGameType) {
        this.forcedGameType = forcedGameType;
    }

}
