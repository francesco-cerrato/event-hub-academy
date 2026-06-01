package com.academy.eventhub.service;

import com.academy.eventhub.dto.ProfileResponseDto;
import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserRoleUpdateDto;
import com.academy.eventhub.dto.UserUpdateDto;
import com.academy.eventhub.entity.Role;
import com.academy.eventhub.entity.Ticket;
import com.academy.eventhub.entity.TicketStatus;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.RoleRepository;
import com.academy.eventhub.repository.TicketRepository;
import com.academy.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
    Servizio di business logica che implementa le operazioni CRUD per l'entità User.
    Sfrutta l'approccio basato su DTO per scambiare dati in modo sicuro con l'esterno.
 */

@Service
public class UserServiceImpl implements UserService
{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TicketRepository ticketRepository;

    /*
     Costruttore per la Dependency Injection @Autowired
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           TicketRepository ticketRepository)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.ticketRepository = ticketRepository;
    }

    /*
        Recupera un singolo utente tramite il suo id
     */
    @Override
    public UserResponseDto getUserById(Long id)
    {
        // Cerca l'utente sul DB (in caso di assenza lancia un eccezione)
        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con id: " + id));
        // Converte l'entità trovata nel DTO di risposta
        return convertToResponseDto(foundUser);
    }

    /*
        Recupera l'elento completo di tutte gli utenti salvati nel DB
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> userList = userRepository.findAll();

        List<UserResponseDto> dtoList = new ArrayList<>();

        for (User user : userList)
        {
            UserResponseDto dto = convertToResponseDto(user);

            dtoList.add(dto);
        }

        return dtoList;
    }

    /*
        Aggiorna le informazioni anagrafiche di base
        (in questo step lo username) di un utente esistente.

        Si occupa anche della modifica dunque delle entity
        authorities (roles) per il rispettivo user
     */
    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto inputDto)
    {
        // Recupero utente dal DB tramite il suo id
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con id: " + id));

        String oldUsername = userToUpdate.getUsername();
        String newUsername = inputDto.getUsername();

        // Se lo username è effettivamente cambiato è necessario aggiornare la tabella authorities
        if (!oldUsername.equals(newUsername))
        {
            List<Role> rolesToUpdate = roleRepository.findByUsername(oldUsername);

            // Cambiamo lo username dentro ogni record di ruolo e salviamo le modifiche
            for (Role role : rolesToUpdate) {
                role.setUsername(newUsername);
                roleRepository.save(role); // Aggiorna la tabella authorities
            }
        }

        // aggiornamento username sull'utente principale in sicurezza
        userToUpdate.setUsername(newUsername);
        User updatedUser = userRepository.save(userToUpdate);

        // Conversione e restituizione (ora troverà i ruoli associati al nuovo username!)
        return convertToResponseDto(updatedUser);

    }

    /*
        Rimuove in modo permanente un utente dal sistema identificandolo tramite l'id.
     */
    @Override
    @Transactional
    public void deleteUser(Long id)
    {
        User foundUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con id: " + id));

        // Recupero di tutti i ruoli associati allo username di questo utente
        List<Role> rolesToDelete = roleRepository.findByUsername(foundUser.getUsername());

        // Eliminazione record corrispondenti dalla tabella authorities
        for (Role role : rolesToDelete) {
            roleRepository.delete(role);
        }

        // Recupero utente di sistema che PRE-INSERITO nel DB (es. ID 1 o via username)
        User systemUser = userRepository.findByUsername("SISTEMA_ANONIMO")
                .orElseThrow(() -> new IllegalStateException("Utente di sistema 'SISTEMA_ANONIMO' non configurato nel DB!"));


        // Annullamento automatico di tutti i biglietti attivi per eventi futuri
        // Recuperiamo tutti i ticket attivi dell'utente
        List<Ticket> activeTickets = ticketRepository.findByUserUsername(foundUser.getUsername());

        for (Ticket ticket : activeTickets) {
            // Se lo stato è ACTIVE e l'evento associato deve ancora iniziare (è nel futuro)
            if (ticket.getStatus() == TicketStatus.ACTIVE && ticket.getEvent().getEventDate().isAfter(LocalDateTime.now()))
            {
                ticket.setStatus(TicketStatus.CANCELLED);

                /*
                    MOTIVAZIONE TECNICA (Risoluzione TransientObjectException / NOT NULL Constraint):
                    Non possiamo impostare 'ticket.setUser(null)' perché la colonna 'user_id' nella
                    tabella 'ticket' ha un vincolo NOT NULL a livello di database.
                    Allo stesso tempo, non possiamo lasciare il ticket collegato a 'foundUser' poiché
                    l'utente sta per essere rimosso dal database alla fine del metodo.

                    SOLUZIONE: Riassegniamo la proprietà del ticket a un utente tecnico di sistema
                    ('SISTEMA_ANONIMO'). Questo permette di:
                    Mantenere lo storico dei ticket nel database con stato 'CANCELLED' (richiesto dal business).
                    Rispettare il vincolo NOT NULL del database relazionale.
                    Evitare che Hibernate lanci eccezioni di persistenza durante la cancellazione dell'utente reale.
                 */

                // SOLUZIONE: Sganciamo l'utente dal ticket per evitare il conflitto in cascata
                ticket.setUser(systemUser);

                // Salviamo esplicitamente il ticket modificato
                ticketRepository.save(ticket);

            }
        }

        userRepository.delete(foundUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserRole(Long id, UserRoleUpdateDto roleDto) {
        User foundUser = userRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Utente non trovato con id: " + id));

        // Recupero lista ruoli associati ad uno specifico username
        List<Role> currentRoles = roleRepository.findByUsername(foundUser.getUsername());

        // Cancellazione vecchi ruoli per evitare duplicati
        for (Role role : currentRoles) {
            roleRepository.delete(role);
        }

        Role newRole = new Role();
        newRole.setUsername(foundUser.getUsername());

        // Verifica che il ruolo abbia il prefisso "ROLE_" se l'input non lo contiene
        String formattedRole = roleDto.getRole().toUpperCase();
        if (!formattedRole.startsWith("ROLE_")) {
            formattedRole = "ROLE_" + formattedRole;
        }
        newRole.setAuthority(formattedRole);

        roleRepository.save(newRole);

        return convertToResponseDto(foundUser);
    }

    @Override
    @Transactional
    public UserResponseDto getUserByUsername(String username)
    {
        User foundUser = userRepository.findByUsername(username)
                .orElseThrow( () -> new ResourceNotFoundException("Utente non trovato con username: " + username) );

        return convertToResponseDto(foundUser);
    }





    /*
        Metodo di utilità interno (Helper) incaricato di mappare un'entità JPA User
        in un oggetto di trasporto sicuro UserResponseDto.
     */
    private UserResponseDto convertToResponseDto(User user)
    {
        // Interroga la tabella authorities tramite il RoleRepository usando lo username dell'utente
        List<Role> userRoles = roleRepository.findByUsername(user.getUsername());

        // Converte la lista di entità Role estraendo solo la stringa dell'authority (es. "ROLE_USER")
        Set<String> rolesSet = userRoles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());

        // Istanzia il DTO iniettando ID, Username (MANTENENDO LA PASSWORD SEGRETA) e i Ruoli raccolti
        UserResponseDto userResponseDto = new UserResponseDto(user.getId(),
                user.getUsername(), rolesSet);

        // Se l'utente ha un profilo associato, converte l'entità Profile nel relativo DTO
        if (user.getProfile() != null) {
            ProfileResponseDto profileDto = new ProfileResponseDto();
            profileDto.setId(user.getProfile().getId());
            profileDto.setFirstName(user.getProfile().getFirstName());
            profileDto.setLastName(user.getProfile().getLastName());
            profileDto.setUserId(user.getProfile().getUser().getId());
            profileDto.setBio(user.getProfile().getBio());
            profileDto.setCity(user.getProfile().getCity());
            profileDto.setAvatarUrl(user.getProfile().getAvatarUrl());
            userResponseDto.setProfile(profileDto);
        }

        // Restituisce il DTO pronto all'uso
        return userResponseDto;
    }
}
