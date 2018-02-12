package io.github.kuohsuanlo.claim.utils;


import java.util.ArrayList;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ClaimUtils {

	public static int pNumber = 10;
	public static double radius = 0.3;
	public static GriefPrevention gp = GriefPrevention.instance;
	
	public static ArrayList<String> accessTrust = new ArrayList<String>();
	public static ArrayList<String> containerTrust = new ArrayList<String>();
	public static ArrayList<String> buildTrust = new ArrayList<String>();
	public static ArrayList<String> managerTrust = new ArrayList<String>();
	
	public static ArrayList<String> getOwnerUUID(Claim claim){
		ArrayList<String> trustUUIDs = new ArrayList<String>();
		
		if(claim.getOwnerName()!=null){
			Player ofp = Bukkit.getPlayer(claim.getOwnerName());
			if(ofp!=null){
				trustUUIDs.add( ofp.getUniqueId().toString());
			}
		}
		return trustUUIDs;
	}
	private static void clearTrustList(){
		accessTrust.clear();
		containerTrust.clear();
		buildTrust.clear();
		managerTrust.clear();
	}
	public static boolean isClaimed(Claim c){
		if(c==null){
			
			return false;
		}
		return true;
	}
	public static Claim getClaimFromLocation(Location location){
		Claim claim = gp.dataStore.getClaimAt(location, true, null);
		//no one's land
		if(claim==null){
			
			return null;
		}
		return claim;
	}
	public static boolean isGPLoaded(){
		if(gp==null){
    		
    		return false;
    	}
		return true;
	}
	public static boolean isPublicClaim(Claim claim){
		if(claim==null) return false;
		clearTrustList();
		claim.getPermissions(buildTrust, containerTrust, accessTrust , managerTrust);
		return(buildTrust.contains("public"));
	}
	public static boolean isPlayerInClaim(Player player, Claim claim){
		return claim.contains(player.getLocation(), true,false);
	}
	public static boolean isOwnerOfClaim(Player player, Claim claim){
		if(claim.ownerID==null) return false;
		return claim.ownerID.toString().equals(player.getUniqueId().toString());
	}
	public static ArrayList<String> getAllTrustUUID(Location location){
		
    	if(!isGPLoaded()) return new ArrayList<String>();
    	
		Claim claim = getClaimFromLocation(location);
		if(!isClaimed(claim)) return new ArrayList<String>();
		
		ArrayList<String> trustUUID = getOwnerUUID(claim);
		
		clearTrustList();
		claim.getPermissions(buildTrust, containerTrust, accessTrust , managerTrust);
		
		for(String uuid: accessTrust){
			trustUUID.add( uuid );
		}
		for(String uuid: containerTrust){
			trustUUID.add( uuid );
		}
		for(String uuid: buildTrust){
			trustUUID.add( uuid );
		}
		for(String uuid: managerTrust){
			trustUUID.add( uuid );
		}
		
    	return trustUUID;
    	
	}


}