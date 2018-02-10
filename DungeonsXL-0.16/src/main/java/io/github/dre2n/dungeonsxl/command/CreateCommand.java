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
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import java.io.File;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class CreateCommand extends DRECommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public CreateCommand() {
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("create");
        setHelp(DMessage.HELP_CMD_CREATE.getMessage());
        setPermission(DPermission.CREATE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String name = args[1];

        if (new File(DungeonsXL.MAPS, name).exists()) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_IN_USE.getMessage(name));
            return;
        }

        if (name.length() > 15) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_TO_LONG.getMessage());
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            // Msg create
            MessageUtil.log(plugin, DMessage.LOG_NEW_MAP.getMessage());
            MessageUtil.log(plugin, DMessage.LOG_GENERATE_NEW_WORLD.getMessage());

            // Create World
            DResourceWorld resource = new DResourceWorld(plugin.getDWorlds(), name);
            plugin.getDWorlds().addResource(resource);
            DEditWorld editWorld = resource.generate();
            editWorld.save();
            editWorld.delete();

            // MSG Done
            MessageUtil.log(plugin, DMessage.LOG_WORLD_GENERATION_FINISHED.getMessage());

        } else if (sender instanceof Player) {
            Player player = (Player) sender;

            if (DGamePlayer.getByPlayer(player) != null) {
                MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
                return;
            }

            // Msg create
            MessageUtil.log(plugin, DMessage.LOG_NEW_MAP.getMessage());
            MessageUtil.log(plugin, DMessage.LOG_GENERATE_NEW_WORLD.getMessage());

            // Create World
            DResourceWorld resource = new DResourceWorld(plugin.getDWorlds(), name);
            plugin.getDWorlds().addResource(resource);
            DEditWorld editWorld = resource.generate();

            // MSG Done
            MessageUtil.log(plugin, DMessage.LOG_WORLD_GENERATION_FINISHED.getMessage());

            // Tp Player
            DEditPlayer.create(player, editWorld);
        }
    }

}
