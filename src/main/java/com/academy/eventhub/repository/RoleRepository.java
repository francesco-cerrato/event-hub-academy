package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    /*
     Query personalizzata generata automaticamente da Spring Data JPA tramite "Query Method".
     "findBy": dice a Spring di generare una query di selezione (SELECT).
     "Username": indica che la ricerca deve filtrare per l'attributo 'username' dell'entità Role.
     Restituisce una List<Role> perché a un username possono essere associati più ruoli.

     SQL equivalente:
     SELECT * FROM role WHERE username = ?;
     */
    List<Role> findByUsername(String username);
}

