/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2021 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web.dto.ocpp;

import com.github.f4b6a3.uuid.UuidCreator;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public interface ChargePointSelection {

    /**
     * @return message ID sent to the charge point
     */
    default @NotNull String getMessageId() {
        return UuidCreator.getTimeOrdered().toString();
    }

    /**
     * @return list of infos for contacting charge points
     */
    @NotNull List<ChargePointSelect> getChargePointSelectList();
}
