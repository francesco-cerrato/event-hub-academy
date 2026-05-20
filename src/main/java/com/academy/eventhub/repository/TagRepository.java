package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long>
{
    /*
        Visto che EventRequestDto riceverà una lista di tagIds,
        è necessario cercare i tag nel database per
        poterli associare all'evento nel Service.
     */
}
