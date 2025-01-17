
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
import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.findAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Optional<Event> event = eventService.findEventById(id);
            if (event.isPresent()) {
                return ResponseEntity.ok(event.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante il recupero evento");
        }
    }


    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody Event event, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtUtil.extractRole(token);

        if (!"ORGANIZER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso Negato: Solo gli organizzatori possono creare eventi.");
        }

        Event createdEvent = eventService.saveEvent(event, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<Event> event = eventService.findEventById(id);
        if (event.isPresent()) {
            Event existingEvent = event.get();
            if (!existingEvent.getOrganizerUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso Negato: Solo gli organizzatori possono modificare gli eventi.");
            }
            existingEvent.setTitle(eventDetails.getTitle());
            existingEvent.setDescription(eventDetails.getDescription());
            existingEvent.setDate(eventDetails.getDate());
            existingEvent.setLocation(eventDetails.getLocation());
            existingEvent.setAvailableSeats(eventDetails.getAvailableSeats());
            return ResponseEntity.ok(eventService.saveEvent(existingEvent, token));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<Event> event = eventService.findEventById(id);
        if (event.isPresent()) {
            Event existingEvent = event.get();
            if (!existingEvent.getOrganizerUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access negato: Solo gli organizzatori possono eliminare l'evento.");
            }
            eventService.deleteEvent(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
        }
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerForEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        if ("ORGANIZER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Gli organizzatori non possono registrarsi agli eventi.");
        }
        Optional<Event> eventOptional = eventService.findEventById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getAttendees().contains(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sei già registrato a questo evento");
            }

            try {
                Event updatedEvent = eventService.addAttendeeToEvent(id, username);
                return ResponseEntity.ok(updatedEvent);
            }catch(RuntimeException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
        }
    }

    @PostMapping("/{id}/unregister")
    public ResponseEntity<?> unregisterFromEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Event updatedEvent = eventService.removeAttendeeFromEvent(id, username);
        return ResponseEntity.ok(updatedEvent);
    }


    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        List<Event> events = eventService.findEventsByUser(username);
        return ResponseEntity.ok(events);
    }
}
