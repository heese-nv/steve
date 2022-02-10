package de.rwth.idsg.steve.api.controller;

import de.rwth.idsg.steve.api.contract.ChargePointOperationContract;
import de.rwth.idsg.steve.api.contract.ChargePointOperationResponseContract;
import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.message.AbstractOperationRequest;
import de.rwth.idsg.steve.mq.message.ClearCacheRequest;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ralf.heese
 */
@RestController
@OpenAPIDefinition(info = @Info(title = "Charge Point", version = "1.0"), tags = @Tag(name = "Operations"))
@RequestMapping(path = ApiPaths.API_V1)
public class ChargePointApiController {

    private final KafkaChargePointProducerService service;

    public ChargePointApiController(KafkaChargePointProducerService service) {
        this.service = service;
    }

    @PostMapping(path = "charge_points/send")
    public ResponseEntity<ChargePointOperationResponseContract> test(@RequestBody() ChargePointOperationContract operation) {

        AbstractOperationRequest message;

        switch (operation.getType()) {
            case "clear_cache":
                message = ClearCacheRequest.builder()
                                           .chargePointId(operation.getChargePointId())
                                           .build();
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        String requestId = service.send(message);

        ChargePointOperationResponseContract response = ChargePointOperationResponseContract.builder()
                                                                                            .requestId(requestId)
                                                                                            .chargePointId(operation.getChargePointId())
                                                                                            .status("success")
                                                                                            .build();
        return ResponseEntity.ok(response);
    }
}
