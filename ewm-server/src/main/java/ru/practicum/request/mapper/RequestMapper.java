package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.enums.Status;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestResultUpdateDto;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constant.DATE_TIME;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto makeDtoOut(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(DATE_TIME))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> makeListDtoOut(List<Request> requests) {
        return requests.stream().map(RequestMapper::makeDtoOut).collect(Collectors.toList());
    }

    public RequestResultUpdateDto makeRequestResultUpdateDto(List<Request> requests) {
        return RequestResultUpdateDto.builder()
                .confirmedRequests(requests.stream().filter(request -> request.getStatus().equals(Status.CONFIRMED))
                        .map(RequestMapper::makeDtoOut).collect(Collectors.toList()))
                .rejectedRequests(requests.stream().filter(request -> request.getStatus().equals(Status.REJECTED))
                        .map(RequestMapper::makeDtoOut).collect(Collectors.toList()))
                .build();
    }
}
