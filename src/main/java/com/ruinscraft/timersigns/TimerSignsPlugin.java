package com.ruinscraft.timersigns;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TimerSignsPlugin extends JavaPlugin implements Listener {

	private static Set<Location> signLocations = new HashSet<>();
	
	private static final long END_TIME = 1531256400000L;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		getServer().getScheduler().runTaskTimer(this, () -> {
			
			for (Location location : signLocations) {
				if (!(location.getBlock().getState() instanceof Sign)) {
					continue;
				}
				
				Sign sign = (Sign) location.getBlock().getState();
				
				sign.setLine(1, getDays());
				sign.setLine(2, getHours());
				sign.setLine(3, getMins() + " " + getSecs());
				
				sign.update();
			}
			
		}, 20L, 20L);
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		if (block == null) {
			return;
		}
		
		if (block.getState() == null) {
			return;
		}
		
		if (!(block.getState() instanceof Sign)) {
			return;
		}
		
		Sign sign = (Sign) block.getState();
		
		for (String line : sign.getLines()) {
			if (line.contains("Time Left:")) {
				signLocations.add(sign.getLocation());
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		for (BlockState blockState : event.getChunk().getTileEntities()) {
			if (!(blockState instanceof Sign)) {
				continue;
			}
			
			Sign sign = (Sign) blockState;
			
			for (String line : sign.getLines()) {
				if (line.contains("Time Left:")) {
					signLocations.add(sign.getLocation());
				}
			}
		}
	}
	
	public static String[] getFullTime() {
		long elapsedTime = END_TIME - System.currentTimeMillis();

		return DurationFormatUtils.formatDuration(elapsedTime, "d'd':H'h':m'm':s's'", false).split(":");
	}
	
	public static String getDays() {
		return getFullTime()[0];
	}
	
	public static String getHours() {
		return getFullTime()[1];
	}
	
	public static String getMins() {
		return getFullTime()[2];
	}
	
	public static String getSecs() {
		return getFullTime()[3];
	}
	
}
