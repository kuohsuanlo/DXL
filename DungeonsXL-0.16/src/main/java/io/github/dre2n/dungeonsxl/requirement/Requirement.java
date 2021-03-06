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
package io.github.dre2n.dungeonsxl.requirement;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementRegistrationEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Extend this to create a custom Requirement.
 *
 * @author Daniel Saukel
 */
public abstract class Requirement {

    public static Requirement create(RequirementType type) {
        Requirement requirement = null;

        try {
            Constructor<? extends Requirement> constructor = type.getHandler().getConstructor();
            requirement = constructor.newInstance();

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            MessageUtil.log("An error occurred while accessing the handler class of the requirement " + type.getIdentifier() + ": " + exception.getClass().getSimpleName());
            if (!(type instanceof RequirementTypeDefault)) {
                MessageUtil.log("Please note that this requirement is an unsupported feature added by an addon!");
            }
        }

        RequirementRegistrationEvent event = new RequirementRegistrationEvent(requirement);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return requirement;
    }

    /* Abstracts */
    public abstract void setup(ConfigurationSection config);

    public abstract boolean check(Player player);

    public abstract void demand(Player player);

    public abstract RequirementType getType();

}
