package com.eventi.eventiApplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate date;
    @NotBlank
    private String location;
    @Min(value = 1, message = "Imetti almeno un posto.")
    private int availableSeats;

    @JoinColumn(name = "organizer_username")
    private String organizerUsername;

    @ElementCollection
    @CollectionTable(name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "attendee_username")
    private Set<String> attendees = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getOrganizerUsername() {
        return organizerUsername;
    }

    public void setOrganizerUsername(String organizer) {
        this.organizerUsername = organizer;
    }

    public Set<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<String> attendees) {
        this.attendees = attendees;
    }
}
