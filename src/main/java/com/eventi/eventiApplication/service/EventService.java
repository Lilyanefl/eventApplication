package com.eventi.eventiApplication.service;

import com.eventi.eventiApplication.model.Event;
import com.eventi.eventiApplication.repository.EventRepository;
import com.eventi.eventiApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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

    public Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento non trovato"));
    }

    public Event saveEvent(Event event, String token) {
        String username = jwtUtil.extractUsername(token);
        event.setOrganizerUsername(username);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = findEventById(id);
        eventRepository.delete(event);
    }

    public Event addAttendeeToEvent(Long eventId, String username) {
        Event event = findEventById(eventId);
        if (event.getAvailableSeats() <= 0) {
            throw new IllegalStateException("Posti finiti per questo evento");
        }
        if (event.getAttendees().contains(username)) {
            throw new IllegalStateException("Sei già registrato a questo evento");
        }
        event.getAttendees().add(username);
        event.setAvailableSeats(event.getAvailableSeats() - 1);
        return eventRepository.save(event);
    }

    public Event removeAttendeeFromEvent(Long eventId, String username) {
        Event event = findEventById(eventId);
        if (!event.getAttendees().contains(username)) {
            throw new IllegalStateException("L'utente non è registrato per questo evento");
        }
        event.getAttendees().remove(username);
        event.setAvailableSeats(event.getAvailableSeats() + 1);
        return eventRepository.save(event);
    }

    public List<Event> findEventsByUser(String username) {
        return eventRepository.findAll().stream()
                .filter(event -> event.getAttendees().contains(username))
                .collect(Collectors.toList());
    }
}
