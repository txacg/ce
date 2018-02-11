package com.taiter.ce.CItems;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/



import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;



public class Powergloves extends CItem {

	/* 投擲速度倍率 */
	int	ThrowSpeedMultiplier;
	/* 舉起後多久可以投擲 */
	int	ThrowDelayAfterGrab;
	/* 最大舉起時間 */
	int	MaxGrabtime;

	public Powergloves(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("ThrowSpeedMultiplier", 60);
		this.configEntries.put("ThrowDelayAfterGrab", 20);
		this.configEntries.put("MaxGrabtime", 10);
		triggers.add(Trigger.INTERACT_RIGHT);
		triggers.add(Trigger.INTERACT_ENTITY);
	}

	@Override
	public boolean effect(Event event, final Player player) {

		if(event instanceof PlayerInteractEntityEvent) {
			PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
			e.setCancelled(true);
			final Entity clicked = e.getRightClicked();

			//if(cooldown.contains(player) && player.getPassengers().isEmpty()) player.removeMetadata("ce." + getOriginalName(), main);

			/* 假如玩家沒有Powergloves的Metadata */
			if(!player.hasMetadata("ce." + getOriginalName()))

				/* 假如目標 [ 是生物實體 & 不是死的 & 自己不是騎乘者 & 沒有騎乘者 ] */
				if(clicked instanceof LivingEntity && !clicked.isDead() && !clicked.getPassengers().contains(player) && player.getPassengers().isEmpty()) {
					player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, false));

					player.addPassenger(clicked);

					player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_CHEW_IRON_DOOR, 10);

					new BukkitRunnable() {

						@Override
						public void run() {
							player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 10);
							player.setMetadata("ce." + getOriginalName(), new FixedMetadataValue(main, true));
							if (!player.getPassengers().isEmpty()) {
								if (player.getPassengers().get(0).getCustomName() == null) {
									player.sendMessage("You catched " + player.getPassengers().get(0).getName() + "! Right click to throw it!");
								} else {
									player.sendMessage("You catched " + player.getPassengers().get(0).getCustomName() + "! Right click to throw it!");
								}
							}
							this.cancel();
						}
					}.runTaskLater(main, ThrowDelayAfterGrab);

					new BukkitRunnable() {

						int			GrabTime	= MaxGrabtime;
						//ItemStack	current		= player.getInventory().getItemInMainHand();

						@Override
						public void run() {
							if(player.isOnline() && !player.isDead() && !player.getPassengers().isEmpty()) {

								if(GrabTime > 0) {
									if(!player.hasMetadata("ce." + getOriginalName())) {
										this.cancel();
									}
									GrabTime--;
								} else if(GrabTime <= 0) {
									if(player.hasMetadata("ce." + getOriginalName())) {
										player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
										player.removeMetadata("ce." + getOriginalName(), main);
										generateCooldown(player, getCooldown());
										if (!player.getPassengers().isEmpty()) {
											if (player.getPassengers().get(0).getCustomName() == null) {
												player.sendMessage("§4Oh!The §f" + player.getPassengers().get(0).getName() + " §4has run off!");
											} else {
												player.sendMessage("§4Oh!The §f" + player.getPassengers().get(0).getCustomName() + " §4has run off!");
											}
										}
									}
									if (clicked.isValid() && player.getPassengers().contains(clicked)) {
										clicked.leaveVehicle();
									}
									this.cancel();
								}
						  	} else if(!player.isOnline() || player.isDead() || player.getPassengers().isEmpty()){
								if(player.hasMetadata("ce." + getOriginalName())) {
									player.removeMetadata("ce." + getOriginalName(), main);
									generateCooldown(player, getCooldown());
									clicked.leaveVehicle();
									this.cancel();
								}
						  	}
						}
					}.runTaskTimer(main, 0l, 10l);
				}
		} else if(event instanceof PlayerInteractEvent) {
			if(player.hasMetadata("ce." + getOriginalName()) && player.getMetadata("ce." + getOriginalName()).get(0).asBoolean()) {
				if (!player.getPassengers().isEmpty()) {
					for (Entity passenger : player.getPassengers()) {
						passenger.leaveVehicle();
						passenger.setVelocity(player.getLocation().getDirection().multiply(ThrowSpeedMultiplier));
						player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
						player.removeMetadata("ce." + getOriginalName(), main);
					}
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void initConfigEntries() {
		ThrowDelayAfterGrab = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowDelayAfterGrab"));
		MaxGrabtime = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaxGrabtime"));
		ThrowSpeedMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowSpeedMultiplier"));
	}

}
