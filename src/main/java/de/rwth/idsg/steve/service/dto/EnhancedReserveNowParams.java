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
package de.rwth.idsg.steve.service.dto;

import de.rwth.idsg.steve.web.dto.ocpp.DelegateChargePointSelection;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import lombok.Getter;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 09.03.18
 */
@Getter
public class EnhancedReserveNowParams extends DelegateChargePointSelection<ReserveNowParams> {

    private final int reservationId;
    private final String parentIdTag;

    public EnhancedReserveNowParams(ReserveNowParams delegate, int reservationId, String parentIdTag) {
        super(delegate);
        this.reservationId = reservationId;
        this.parentIdTag = parentIdTag;
    }
}
