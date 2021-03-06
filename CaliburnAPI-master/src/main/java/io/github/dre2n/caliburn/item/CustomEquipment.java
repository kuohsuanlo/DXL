/*
 * Copyright (C) 2015-2017 Daniel Saukel
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
package io.github.dre2n.caliburn.item;

import io.github.dre2n.caliburn.CaliburnAPI;
import io.github.dre2n.caliburn.util.CaliConfiguration;
import io.github.dre2n.commons.item.ItemUtil;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomEquipment extends CustomItem {

    private boolean unbreakable;

    public CustomEquipment(Map<String, Object> args) {
        super(args);

        Object unbreakable = args.get("unbreakable");
        if (unbreakable instanceof Boolean) {
            this.unbreakable = (Boolean) unbreakable;
        }
    }

    public CustomEquipment(CaliburnAPI api, String id, Material material, short durability) {
        super(api, id, material, durability);
    }

    public CustomEquipment(CaliburnAPI api, String id, CaliConfiguration config) {
        this(config.getArgs());

        this.api = api;
        this.id = id;
        this.config = config;
    }

    /* Getters and setters */
    /**
     * @return
     * if the item is unbreakable
     */
    public boolean isUnbreakable() {
        return unbreakable;
    }

    /**
     * @param unbreakable
     * set the item (un-) breakable
     */
    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    /* Actions */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> config = super.serialize();
        // TO DO
        return config;
    }

    /**
     * @return
     * the CustomEquipment as org.bukkit.inventory.ItemStack
     */
    @Override
    public ItemStack toItemStack(int amount) {
        ItemStack itemStack = super.toItemStack(amount);

        if (unbreakable) {
            itemStack = ItemUtil.setUnbreakable(itemStack, true);
        }

        return itemStack;
    }

}
