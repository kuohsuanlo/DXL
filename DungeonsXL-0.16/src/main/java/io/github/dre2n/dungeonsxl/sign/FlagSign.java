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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.block.TeamFlag;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public class FlagSign extends DSign {

    private DSignType type = DSignTypeDefault.FLAG;

    private int team;

    public FlagSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters */
    @Override
    public DSignType getType() {
        return type;
    }

    /* Actions */
    @Override
    public boolean check() {
        return NumberUtil.parseInt(lines[1], -1) != -1;
    }

    @Override
    public void onInit() {
        this.team = NumberUtil.parseInt(lines[1]);
        if (getGame().getDGroups().size() > team) {
            getGameWorld().addGameBlock(new TeamFlag(getSign().getBlock(), getGame().getDGroups().get(team)));
        }
    }

}
