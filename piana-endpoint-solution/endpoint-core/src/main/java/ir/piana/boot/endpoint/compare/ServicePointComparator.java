package ir.piana.boot.endpoint.compare;

import ir.piana.boot.endpoint.dto.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServicePointComparator {
    public static ChangedServicePointCollectionDto compare(
            ServicePointCollectionDto newServicePointCollectionDto,
            ServicePointCollectionDto oldServicePointCollectionDto) {
        List<ChangedServicePointDto> changedServicePointDtoList = new ArrayList<>();
        if ((oldServicePointCollectionDto == null ||
                oldServicePointCollectionDto.servicePoints() == null ||
                oldServicePointCollectionDto.servicePoints().isEmpty()) &&
                (newServicePointCollectionDto == null ||
                        newServicePointCollectionDto.servicePoints() == null ||
                        newServicePointCollectionDto.servicePoints().isEmpty())) {
            return new ChangedServicePointCollectionDto(Collections.emptyList());
        } else if (oldServicePointCollectionDto == null ||
                oldServicePointCollectionDto.servicePoints() == null ||
                oldServicePointCollectionDto.servicePoints().isEmpty()) {
            changedServicePointDtoList.addAll(newServicePointCollectionDto.servicePoints().stream()
                    .map(servicePointDto -> new ChangedServicePointDto(
                            servicePointDto.name(), false, true, Collections.emptyList())
                    ).toList());
            return new ChangedServicePointCollectionDto(changedServicePointDtoList);
        } else if (newServicePointCollectionDto == null ||
                newServicePointCollectionDto.servicePoints() == null ||
                newServicePointCollectionDto.servicePoints().isEmpty()) {
            changedServicePointDtoList.addAll(oldServicePointCollectionDto.servicePoints().stream()
                    .map(servicePointDto -> new ChangedServicePointDto(
                            servicePointDto.name(), true, false, Collections.emptyList())
                    ).toList());
            return new ChangedServicePointCollectionDto(changedServicePointDtoList);
        }

        List<String> newServicePointNameList = newServicePointCollectionDto.servicePoints()
                .stream().map(ServicePointDto::name).toList();
        List<String> oldServicePointNameList = oldServicePointCollectionDto.servicePoints()
                .stream().map(ServicePointDto::name).toList();

        changedServicePointDtoList.addAll(oldServicePointNameList.stream()
                .filter(name -> !newServicePointNameList.contains(name))
                .map(name -> new ChangedServicePointDto(
                        name, true, false, Collections.emptyList()))
                .toList());

        changedServicePointDtoList.addAll(newServicePointNameList.stream()
                .filter(name -> !oldServicePointNameList.contains(name))
                .map(name -> new ChangedServicePointDto(
                        name, false, true, Collections.emptyList()))
                .toList());

        Map<String, ServicePointDto> newServicePointMap = newServicePointCollectionDto.servicePoints()
                .stream()
                .filter(servicePointDto -> oldServicePointNameList.contains(servicePointDto.name()))
                .collect(Collectors.toMap(ServicePointDto::name, Function.identity()));
        Map<String, ServicePointDto> oldServicePointMap = oldServicePointCollectionDto.servicePoints()
                .stream()
                .filter(servicePointDto -> newServicePointNameList.contains(servicePointDto.name()))
                .collect(Collectors.toMap(ServicePointDto::name, Function.identity()));

        changedServicePointDtoList.addAll(newServicePointMap.entrySet().stream().map(newEntry -> {
                    ServicePointDto oldServicePointDto = oldServicePointMap.get(newEntry.getKey());
                    ServicePointDto newServicePointDto = newEntry.getValue();

                    List<ChangedEndpointDto> changedEndpointDtoList = new ArrayList<>();

                    List<String> newEndpointNameList = newServicePointDto.endpoints()
                            .stream().map(EndpointDto::name).toList();
                    List<String> oldEndpointNameList = oldServicePointDto.endpoints()
                            .stream().map(EndpointDto::name).toList();

                    changedEndpointDtoList.addAll(oldEndpointNameList.stream()
                            .filter(name -> !newEndpointNameList.contains(name))
                            .map(name -> new ChangedEndpointDto(
                                    name, true, false, false, false))
                            .toList());

                    changedEndpointDtoList.addAll(newEndpointNameList.stream()
                            .filter(name -> !oldEndpointNameList.contains(name))
                            .map(name -> new ChangedEndpointDto(
                                    name, false, true, false, false))
                            .toList());

                    Map<String, EndpointDto> newEndpointMap = newServicePointDto.endpoints()
                            .stream()
                            .filter(endpointDto -> oldEndpointNameList.contains(endpointDto.name()))
                            .collect(Collectors.toMap(EndpointDto::name, Function.identity()));
                    Map<String, EndpointDto> oldEndpointMap = oldServicePointDto.endpoints()
                            .stream()
                            .filter(endpointDto -> newEndpointNameList.contains(endpointDto.name()))
                            .collect(Collectors.toMap(EndpointDto::name, Function.identity()));

                    changedEndpointDtoList.addAll(
                            newEndpointMap.entrySet().stream().map(newEndpointEntry -> {
                                        EndpointDto oldEndpointDto = oldEndpointMap.get(newEndpointEntry.getKey());
                                        EndpointDto newEndpointDto = newEndpointEntry.getValue();

                                        boolean changedNetwork = oldEndpointDto.hashCode() !=
                                                newEndpointDto.hashCode();
                                        boolean changedLimitation =
                                                oldEndpointDto.limitationDto().hashCode() !=
                                                        newEndpointDto.limitationDto().hashCode();

                                        return new ChangedEndpointDto(
                                                newEndpointDto.name(), false, false,
                                                changedNetwork, changedLimitation);
                                    })
                                    .filter(changedEndpointDto -> changedEndpointDto.added() ||
                                            changedEndpointDto.deleted() ||
                                            changedEndpointDto.changedNetwork() ||
                                            changedEndpointDto.changedLimitation())
                                    .toList());
                    return new ChangedServicePointDto(
                            newEntry.getKey(), false, false, changedEndpointDtoList);
                }).filter(changedServicePointDto -> changedServicePointDto.added() ||
                        changedServicePointDto.deleted() ||
                        !changedServicePointDto.changedEndpoints().isEmpty())
                .toList());
        return new ChangedServicePointCollectionDto(changedServicePointDtoList);
    }
}
