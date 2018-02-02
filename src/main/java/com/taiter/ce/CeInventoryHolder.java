package com.taiter.ce;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CeInventoryHolder implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, 54);
    }
}
