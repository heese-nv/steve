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
package de.rwth.idsg.steve.api.controller;

import de.rwth.idsg.steve.api.contract.VersionContract;
import de.rwth.idsg.steve.repository.GenericRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author ralf.heese
 */
@RestController
@OpenAPIDefinition(info = @Info(title = "API Examples", version = "1.0"), tags = @Tag(name = "Operations"))
@RequestMapping(path = ApiPaths.API_V1)
public class VersionController {

    public static final String VERSION_PATH = "/version";

    private final VersionContract versionInfo;

    @Autowired
    public VersionController(GenericRepository genericRepository) {
        this.versionInfo = new VersionContract(CONFIG.getSteveVersion(), genericRepository.getDBVersion().getVersion());
    }

    @Operation(summary = "Get the version.")
    @GetMapping(path = VERSION_PATH)
    public VersionContract getVersion() {
        return versionInfo;
    }
}
