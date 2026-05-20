package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>
{
}
