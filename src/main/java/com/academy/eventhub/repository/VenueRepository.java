package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue,Long>
{
    /*
        Tutti i metodi CRUD standard (save, findById, findAll, deleteById) sono già inclusi (ereditati)
     */
}
