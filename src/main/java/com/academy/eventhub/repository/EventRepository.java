package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>
{
    // Eredita automaticamente il supporto alle query condizionali dinamiche (JpaSpecificationExecutor<Event>)
}
