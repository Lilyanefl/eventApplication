package com.eventi.eventiApplication.controller;

import com.eventi.eventiApplication.model.Event;
import com.eventi.eventiApplication.model.UserDB;
import com.eventi.eventiApplication.security.JwtUtil;
import com.eventi.eventiApplication.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.findEventById(id);
        return ResponseEntity.ok(event);
    }


    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.extractRole(token);

        if (!"ORGANIZER".equals(role)) {
            throw new SecurityException("Accesso Negato: Solo gli organizzatori possono creare eventi.");
        }

        Event createdEvent = eventService.saveEvent(event, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Event existingEvent = eventService.findEventById(id);

        if (!existingEvent.getOrganizerUsername().equals(username)) {
            throw new SecurityException("Accesso Negato: Solo gli organizzatori possono modificare gli eventi.");
        }

        existingEvent.setTitle(eventDetails.getTitle());
        existingEvent.setDescription(eventDetails.getDescription());
        existingEvent.setDate(eventDetails.getDate());
        existingEvent.setLocation(eventDetails.getLocation());
        existingEvent.setAvailableSeats(eventDetails.getAvailableSeats());

        return ResponseEntity.ok(eventService.saveEvent(existingEvent, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Event existingEvent = eventService.findEventById(id);

        if (!existingEvent.getOrganizerUsername().equals(username)) {
            throw new SecurityException("Accesso Negato: Solo gli organizzatori possono eliminare l'evento.");
        }

        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<Event> registerForEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if ("ORGANIZER".equals(role)) {
            throw new SecurityException("Gli organizzatori non possono registrarsi agli eventi.");
        }

        Event updatedEvent = eventService.addAttendeeToEvent(id, username);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{id}/unregister")
    public ResponseEntity<Event> unregisterFromEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Event updatedEvent = eventService.removeAttendeeFromEvent(id, username);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<Event>> getMyEvents(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        List<Event> events = eventService.findEventsByUser(username);
        return ResponseEntity.ok(events);
    }
}
