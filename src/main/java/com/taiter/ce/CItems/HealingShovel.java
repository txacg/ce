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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;

public class HealingShovel extends CItem {

    int Heal;

    public HealingShovel(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
        super(originalName, color, lDescription, lCooldown, mat);
        this.configEntries.put("Heal", 4);
        triggers.add(Trigger.DAMAGE_GIVEN);
    }

    @Override
    public boolean effect(Event event, Player player) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        if (e.getDamager() == player && e.getEntity() instanceof Player) {
            Player damaged = (Player) e.getEntity();
            e.setDamage(0);
            EffectManager.playSound(damaged.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.5f, 1f);
            EffectManager.playSound(damaged.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 2f);
            short currentDur = player.getInventory().getItemInMainHand().getDurability();
            double maxHealth = damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (damaged.getHealth() + Heal <= maxHealth) {
                damaged.setHealth(damaged.getHealth() + Heal);
            } else {
                damaged.setHealth(maxHealth);
            }

            if (currentDur + Heal < player.getInventory().getItemInMainHand().getType().getMaxDurability()) {
                player.getInventory().getItemInMainHand().setDurability((short) (currentDur + Heal));
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                EffectManager.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.1f, 0.3f);
            }
            return true;
        }
        return false;
    }

    @Override
    public void initConfigEntries() {

        Heal = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Heal"));

    }

}
