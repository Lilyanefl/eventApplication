package com.eventi.eventiApplication.repository;

import com.eventi.eventiApplication.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}