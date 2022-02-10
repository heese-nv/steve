package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.StatusResponseCallback;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This service allows performing actions on a charge box without knowledge about communication protocol,
 * supported OCPP standard, and primary key of the charge box. Furthermore, it will raise events to allow
 * processing of the response.
 *
 * @author ralf.heese
 */
@Slf4j
@Service
public class ChargePointService {

    private final ChargePointRepository repository;
    private final ChargePointService12_Client client12;
    private final ChargePointService15_Client client15;
    private final ChargePointService16_Client client16;

    public ChargePointService(ChargePointRepository repository,
                              @Qualifier("ChargePointService12_Client") ChargePointService12_Client client12,
                              @Qualifier("ChargePointService15_Client") ChargePointService15_Client client15,
                              @Qualifier("ChargePointService16_Client") ChargePointService16_Client client16) {
        this.repository = repository;
        this.client12 = client12;
        this.client15 = client15;
        this.client16 = client16;
    }

    public void clearCache(@NotNull ChargeBoxRecord chargeBox, @Nullable StatusResponseCallback... callbacks) {
        OcppProtocol protocol = OcppProtocol.fromCompositeValue(chargeBox.getOcppProtocol());

        ChargePointSelect chargePointSelect = new ChargePointSelect(protocol.getTransport(), chargeBox.getChargeBoxId(), chargeBox.getEndpointAddress());
        MultipleChargePointSelect params = new MultipleChargePointSelect();
        params.setChargePointSelectList(List.of(chargePointSelect));

        ClearCacheTask task = new ClearCacheTask(protocol.getVersion(), params);
        getClient(protocol.getVersion()).executeTask(task, Arrays.asList(callbacks));
    }

    /**
     * Retrieve the record of the charge box identified by {@code id}.
     *
     * @param chargeBoxId
     *         charge box ID (not the primary key)
     * @return charge box record
     */
    @NotNull
    public Optional<ChargeBoxRecord> findChargeBoxById(@NotNull String chargeBoxId) {
        ChargeBoxRecord record = repository.findByChargeBoxId(chargeBoxId);
        return record == null ? Optional.empty() : Optional.of(record);
    }


    /**
     * @param version
     *         OCPP version
     * @return client for the specified version
     */
    @NotNull
    private ChargePointServiceClient getClient(@NotNull OcppVersion version) {
        switch (version) {
            case V_12:
                return client12;
            case V_15:
                return client15;
            case V_16:
                return client16;
            default:
                throw new SteveException("Unsupported OCPP version: " + version);
        }
    }
}
