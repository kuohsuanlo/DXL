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
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.DungeonConfig;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import io.github.dre2n.dungeonsxl.world.DWorldCache;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ListCommand extends DRECommand {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DWorldCache worlds = plugin.getDWorlds();

    public ListCommand() {
        setCommand("list");
        setMinArgs(0);
        setMaxArgs(3);
        setHelp(DMessage.HELP_CMD_LIST.getMessage());
        setPermission(DPermission.LIST.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        ArrayList<String> dungeonList = new ArrayList<>();
        for (Dungeon dungeon : plugin.getDungeons().getDungeons()) {
            dungeonList.add(dungeon.getName());
        }
        ArrayList<String> mapList = new ArrayList<>();
        for (File file : DungeonsXL.MAPS.listFiles()) {
            if (!file.equals(DWorldCache.RAW)) {
                mapList.add(file.getName());
            }
        }
        ArrayList<String> loadedList = new ArrayList<>();
        for (DEditWorld editWorld : worlds.getEditWorlds()) {
            loadedList.add(editWorld.getWorld().getWorldFolder().getName() + " / " + editWorld.getName());
        }
        for (DGameWorld gameWorld : worlds.getGameWorlds()) {
            loadedList.add(gameWorld.getWorld().getWorldFolder().getName() + " / " + gameWorld.getName());
        }
        ArrayList<String> toSend = new ArrayList<>();

        ArrayList<String> stringList = mapList;
        boolean specified = false;
        byte listType = 0;
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("dungeons") || args[1].equalsIgnoreCase("d")) {
                if (args.length >= 3) {
                    Dungeon dungeon = plugin.getDungeons().getByName(args[2]);
                    if (dungeon != null) {
                        MessageUtil.sendPluginTag(sender, plugin);
                        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + dungeon.getName() + " &4&l]");
                        MessageUtil.sendMessage(sender, "&eFloors: &o" + dungeon.getConfig().getFloors());
                        MessageUtil.sendMessage(sender, "&estartFloor: &o[" + dungeon.getConfig().getStartFloor() + "]");
                        MessageUtil.sendMessage(sender, "&eendFloor: &o[" + dungeon.getConfig().getEndFloor() + "]");
                        MessageUtil.sendMessage(sender, "&efloorCount: &o[" + dungeon.getConfig().getFloorCount() + "]");
                        MessageUtil.sendMessage(sender, "&eremoveWhenPlayed: &o[" + dungeon.getConfig().getRemoveWhenPlayed() + "]");
                        return;
                    }
                }
                specified = true;
                stringList = dungeonList;
                listType = 1;

            } else if (args[1].equalsIgnoreCase("maps") || args[1].equalsIgnoreCase("m")) {
                specified = true;

            } else if (args[1].equalsIgnoreCase("loaded") || args[1].equalsIgnoreCase("l")) {
                specified = true;
                stringList = loadedList;
                listType = 2;
            }
        }

        int page = 1;
        if (args.length == 3) {
            page = NumberUtil.parseInt(args[2], 1);

        } else if (args.length == 2 & !specified) {
            page = NumberUtil.parseInt(args[1], 1);
        }

        int send = 0;
        int max = 0;
        int min = 0;
        for (String string : stringList) {
            send++;
            if (send >= page * 5 - 4 && send <= page * 5) {
                min = page * 5 - 4;
                max = page * 5;
                toSend.add(string);
            }
        }

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + min + "-" + max + " &4/&6 " + send + " &4|&6 " + page + " &4&l]");

        switch (listType) {
            case 0:
                MessageUtil.sendMessage(sender, "&4Map&7 | &eInvited");
                for (String map : toSend) {
                    boolean invited = false;
                    if (sender instanceof Player) {
                        DResourceWorld resource = worlds.getResourceByName(map);
                        if (resource != null) {
                            invited = resource.isInvitedPlayer((Player) sender);
                        }
                    }

                    MessageUtil.sendMessage(sender, "&b" + map + "&7 | &e" + invited);
                }
                break;
            case 1:
                MessageUtil.sendMessage(sender, "&4Dungeon&7 | &eMap count");
                for (String dungeon : toSend) {
                    DungeonConfig dungeonConfig = new DungeonConfig(new File(DungeonsXL.DUNGEONS, dungeon + ".yml"));
                    int count = dungeonConfig.getFloors().size() + 2;
                    MessageUtil.sendMessage(sender, "&b" + dungeon + "&7 | &e" + count);
                }
                break;
            case 2:
                MessageUtil.sendMessage(sender, "&4Loaded map");
                for (String map : toSend) {
                    MessageUtil.sendMessage(sender, "&b" + map);
                }
                break;
            default:
                break;
        }
    }

}
