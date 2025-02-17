package ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance;

import ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance.dto.VehicleThirdPartyInsuranceRequest;
import ir.piana.boot.utils.endpointlimiter.operation.LimitationException;
import ir.piana.boot.utils.endpointlimiter.operation.RestClientOperationHandleable;
import ir.piana.boot.utils.endpointlimiter.operation.ServicePointOperation;
import ir.piana.boot.utils.errorprocessor.InternalServerErrorTypes;
import ir.piana.boot.utils.natsclient.MessageHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VehicleThirdPartyInsuranceServicePoint<R>
        implements ServicePointOperation<VehicleThirdPartyInsuranceRequest, R>,
        MessageHandler<VehicleThirdPartyInsuranceRequest, R> {
    private final List<RestClientOperationHandleable<VehicleThirdPartyInsuranceRequest, R>> handleables;

    @Override
    public R apply(VehicleThirdPartyInsuranceRequest request) {
        for (RestClientOperationHandleable<VehicleThirdPartyInsuranceRequest, R> handleable : handleables) {
            try {
                return handleable.apply(request);
            } catch (LimitationException limitationException) {
                //
            }
        }
        throw InternalServerErrorTypes.NO_ACTIVE_ENDPOINT.newException();
    }

    @Override
    public String subject() {
        return "piana.inquiry.insurance.vehicle.third-party";
    }

    @Override
    public Class dtoType() {
        return VehicleThirdPartyInsuranceRequest.class;
    }
}
