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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerLeaveDGroupEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DWorldCache;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class LeaveForceCommand extends DRECommand {

    public LeaveForceCommand() {
        setCommand("leaveforce");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.HELP_CMD_LEAVE.getMessage());
        setPermission(DPermission.LEAVE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;


        /*
		List<Game> Gamelist= DungeonsXL.getInstance().getGames();	
        for (Game game : Gamelist) {
        	for(Player somePlayer : game.getPlayers()){
        		if(player.getName().equals(somePlayer.getName())){
        			game.delete();
        			System.out.println("Game delete : "+player.getName());
        		}
        	}
        }
        
        
		List<DGroup> dGroups = DungeonsXL.getInstance().getDGroups();
		for (DGroup dGroup : dGroups) {
			for (DGamePlayer dGamePlayer : dGroup.getDGamePlayers()) {
	        	if(dGamePlayer.getName().equals(player.getName())){
	        		dGroup.removePlayer(player);
	        		dGroup.delete();
	        		System.out.println("dGamePlayer remove : "+dGamePlayer.getName());
	        	}
	        }
        }
        */
        
        
		/*Proper order to remove all object*/
        List<DInstancePlayer> dPlayers = DungeonsXL.getInstance().getDPlayers().getDInstancePlayers();
        for (DInstancePlayer dPlayer : new ArrayList<DInstancePlayer>(dPlayers)) {
        	if(dPlayer.getName().equals(player.getName())){
        		dPlayers.remove(dPlayer);
        		System.out.println("dPlayers remove : "+dPlayer.getName());
        	}
        }
        

		List<DGlobalPlayer> dGPlayers = DungeonsXL.getInstance().getDPlayers().getDGlobalPlayers();
		for (DGlobalPlayer dGPlayer : new ArrayList<DGlobalPlayer>(dGPlayers)) {
        	if(dGPlayer.getName().equals(player.getName())){
        		dGPlayers.remove(dGPlayer);
        		System.out.println("dGPlayers remove : "+dGPlayer.getName());
        	}
        }
	        

        
        MessageUtil.sendMessage(player, DMessage.CMD_LEAVE_SUCCESS.getMessage());
    }

}
