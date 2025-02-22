package ir.piana.boot.endpoint.data.service;

import ir.piana.boot.endpoint.data.tables.daos.EndpointCallLogDao;
import ir.piana.boot.endpoint.data.tables.pojos.EndpointCallLogEntity;
import ir.piana.boot.endpoint.core.service.EndpointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EndpointLogServiceImpl implements EndpointLogService {
    private final EndpointCallLogDao endpointCallLogDao;

    @Override
    public void beforeLog(long servicePointId, long endpointId, long merchantId, long requesterId,
                          String referenceId, String requestBody) {
        EndpointCallLogEntity endpointCallLog = new EndpointCallLogEntity(null,
                servicePointId, endpointId, referenceId, merchantId, requesterId,
                true, requestBody, null, null, null);
        endpointCallLogDao.insert(endpointCallLog);
    }

    @Override
    public void afterLog(long servicePointId, long endpointId, long merchantId, long requesterId,
                         String referenceId, int httpStatusCode, String responseBody) {
        EndpointCallLogEntity endpointCallLog = new EndpointCallLogEntity(null,
                servicePointId, endpointId, referenceId, merchantId, requesterId,
                false, null, httpStatusCode, responseBody, null);
        endpointCallLogDao.insert(endpointCallLog);
    }
}
