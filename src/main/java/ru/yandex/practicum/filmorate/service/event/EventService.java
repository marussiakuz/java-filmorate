package ru.yandex.practicum.filmorate.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

@Slf4j
@Service
public class EventService {

    private final EventStorage eventStorage;

    public EventService(@Qualifier("eventDbStorage") EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }


}
