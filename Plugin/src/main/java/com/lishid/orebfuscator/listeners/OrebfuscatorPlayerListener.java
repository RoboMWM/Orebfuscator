/*
 * Copyright (C) 2011-2014 lishid.  All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.lishid.orebfuscator.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.lishid.orebfuscator.Orebfuscator;
import com.lishid.orebfuscator.OrebfuscatorConfig;
import com.lishid.orebfuscator.hithack.BlockHitManager;
import com.lishid.orebfuscator.obfuscation.BlockUpdate;
import com.lishid.orebfuscator.obfuscation.ProximityHider;

public class OrebfuscatorPlayerListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (OrebfuscatorConfig.LoginNotification) {
            if (OrebfuscatorConfig.playerBypassOp(player)) {
                Orebfuscator.message(player, "Orebfuscator bypassed because you are OP.");
            } else if (OrebfuscatorConfig.playerBypassPerms(player)) {
                Orebfuscator.message(player, "Orebfuscator bypassed because you have permission.");
            }
        }
        
        if (OrebfuscatorConfig.UseProximityHider) {
            ProximityHider.addPlayerToCheck(event.getPlayer(), null);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BlockHitManager.clearHistory(event.getPlayer());
        if (OrebfuscatorConfig.UseProximityHider) {
            ProximityHider.clearPlayer(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.useInteractedBlock() == Result.DENY)
            return;

        //For using a hoe for farming
        if (event.getItem() != null &&
                event.getItem().getType() != null &&
                (event.getMaterial() == Material.DIRT || event.getMaterial() == Material.GRASS) &&
                ((event.getItem().getType() == Material.WOOD_HOE) ||
                        (event.getItem().getType() == Material.IRON_HOE) ||
                        (event.getItem().getType() == Material.GOLD_HOE) ||
                        (event.getItem().getType() == Material.DIAMOND_HOE)))
        {
            BlockUpdate.Update(event.getClickedBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        BlockHitManager.clearHistory(event.getPlayer());
        
        if (OrebfuscatorConfig.UseProximityHider) {
            ProximityHider.clearBlocksForOldWorld(event.getPlayer());
            ProximityHider.addPlayerToCheck(event.getPlayer(), null);
        }
    }
    
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (OrebfuscatorConfig.UseProximityHider) {
			if(event.getCause() != TeleportCause.END_PORTAL
					&& event.getCause() != TeleportCause.NETHER_PORTAL
					)
			{
				ProximityHider.addPlayerToCheck(event.getPlayer(), null);
			}
		}
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (OrebfuscatorConfig.UseProximityHider) {
            ProximityHider.addPlayerToCheck(event.getPlayer(), event.getFrom());
        }
    }
}
