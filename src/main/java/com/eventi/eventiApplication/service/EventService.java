package com.eventi.eventiApplication.service;

import com.eventi.eventiApplication.model.Event;
import com.eventi.eventiApplication.repository.EventRepository;
import com.eventi.eventiApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event, String token) {
        String username = jwtUtil.extractUsername(token);
        event.setOrganizerUsername(username);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event addAttendeeToEvent(Long eventId, String username) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if (event.getAvailableSeats() > 0) {
                event.getAttendees().add(username);
                event.setAvailableSeats(event.getAvailableSeats() - 1);
                return eventRepository.save(event);
            } else {
                throw new RuntimeException("Posti finiti per questo evento");
            }
        } else {
            throw new RuntimeException("Evento non trovato");
        }
    }

    public Event removeAttendeeFromEvent(Long eventId, String username) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if (event.getAttendees().contains(username)) {
                event.getAttendees().remove(username);
                event.setAvailableSeats(event.getAvailableSeats() + 1);
                return eventRepository.save(event);
            } else {
                throw new RuntimeException("L'utente non è registrato per questo evento");
            }
        } else {
            throw new RuntimeException("Evento non trovato");
        }
    }

    public List<Event> findEventsByUser(String username) {
        return eventRepository.findAll().stream()
                .filter(event -> event.getAttendees().contains(username))
                .collect(Collectors.toList());
    }
}
