package com.taiter.ce;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.List;

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

public class EffectManager {

    private static Constructor<?> effectConstructor;
    private static Object[] particles;

    public EffectManager() {
        try {
            effectConstructor = ReflectionHelper.getEffectPacketConstructor();
            particles = (Object[]) ReflectionHelper.loadEnumParticleValues();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Could not load particle effects. ERROR:");
            e.printStackTrace();

        }
    }

    public static void playSound(Location loc, Sound sound, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }

    public static void sendBlockEffect(List<Player> targets, Location loc, Vector offset, int blockID, float speed, int amount, byte data) {
        Object packet = null;
        try {
            packet = effectConstructor.newInstance(particles[5], true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) offset.getX(), (float) offset.getY(), (float) offset.getZ(),
                    speed, amount, new int[] { blockID, data });
        } catch (Exception e) {
        }

        if (packet != null)
            for (Player p : targets)
                ReflectionHelper.sendPacket(p, packet);
    }

    public static void sendEffect(List<Player> targets, Particle particle, Location loc, float speed, int amount) {
        sendEffect(targets, particle, loc, new Vector(Math.random(), Math.random(), Math.random()), speed, amount);
    }

    public static void sendEffect(List<Player> targets, Particle particle, Location loc, Vector offset, float speed, int amount) {
        for (Player p : targets) {
            p.spawnParticle(particle, loc, amount, offset.getX(), offset.getY(), offset.getZ(), speed);
        }
    }

}
