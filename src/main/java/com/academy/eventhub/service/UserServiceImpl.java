package com.academy.eventhub.service;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserRoleUpdateDto;
import com.academy.eventhub.dto.UserUpdateDto;
import com.academy.eventhub.entity.Role;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.RoleRepository;
import com.academy.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
     Costruttore per la Dependency Injection @Autowired
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

        // Restituisce il DTO pronto all'uso
        return userResponseDto;
    }
}
