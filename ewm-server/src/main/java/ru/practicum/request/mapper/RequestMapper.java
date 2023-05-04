package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.enums.Status;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestResultUpdateDto;
import ru.practicum.request.model.Request;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto makeDtoOut(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> makeListDtoOut(List<Request> requests) {
        return requests.stream().map(RequestMapper::makeDtoOut).collect(Collectors.toList());
    }

    public static RequestResultUpdateDto makeRequestResultUpdateDto(List<Request> requests) {
        return RequestResultUpdateDto.builder()
                .confirmedRequests(requests.stream().filter(request -> request.getStatus().equals(Status.CONFIRMED))
                        .map(RequestMapper::makeDtoOut).collect(Collectors.toList()))
                .rejectedRequests(requests.stream().filter(request -> request.getStatus().equals(Status.REJECTED))
                        .map(RequestMapper::makeDtoOut).collect(Collectors.toList()))
                .build();
    }
}
