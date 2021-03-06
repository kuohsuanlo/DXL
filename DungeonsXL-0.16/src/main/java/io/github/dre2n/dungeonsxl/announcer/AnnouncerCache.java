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
package io.github.dre2n.dungeonsxl.announcer;

import io.github.dre2n.commons.misc.FileUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

/**
 * Announcer instance manager.
 *
 * @author Daniel Saukel
 */
public class AnnouncerCache {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private BukkitTask announcerTask;

    private List<Announcer> announcers = new ArrayList<>();

    public AnnouncerCache(File file) {
        if (file.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(file)) {
                announcers.add(new Announcer(script));
            }
        }
        startAnnouncerTask(plugin.getMainConfig().getAnnouncmentInterval());
        Bukkit.getPluginManager().registerEvents(new AnnouncerListener(), plugin);
    }

    /**
     * @return the announcer that has the name
     */
    public Announcer getByName(String name) {
        for (Announcer announcer : announcers) {
            if (announcer.getName().equalsIgnoreCase(name)) {
                return announcer;
            }
        }

        return null;
    }

    /**
     * @return the announcer that has the GUI
     */
    public Announcer getByGUI(Inventory gui) {
        for (Announcer announcer : announcers) {
            if ((ChatColor.DARK_RED + announcer.getName()).equals(gui.getTitle())) {
                return announcer;
            }
        }

        return null;
    }

    /**
     * @return the announcers
     */
    public List<Announcer> getAnnouncers() {
        return announcers;
    }

    /**
     * @param announcer
     * the Announcer to add
     */
    public void addAnnouncer(Announcer announcer) {
        announcers.add(announcer);
    }

    /**
     * @param announcer
     * the Announcer to remove
     */
    public void removeAnnouncer(Announcer announcer) {
        announcers.remove(announcer);
    }

    /**
     * @return the AnnouncerTask
     */
    public BukkitTask getAnnouncerTask() {
        return announcerTask;
    }

    /**
     * start a new AnnouncerTask
     */
    public void startAnnouncerTask(long period) {
        if (!announcers.isEmpty()) {
            announcerTask = new AnnouncerTask(this).runTaskTimer(plugin, period, period);
        }
    }

}
