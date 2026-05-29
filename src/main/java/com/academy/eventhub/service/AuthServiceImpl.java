package com.academy.eventhub.service;

import com.academy.eventhub.dto.RegisterRequest;
import com.academy.eventhub.entity.Profile;
import com.academy.eventhub.entity.Role;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.ProfileRepository;
import com.academy.eventhub.repository.RoleRepository;
import com.academy.eventhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService
{
    // Dipendenze costanti iniettate tramite costruttore
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ProfileRepository profileRepository)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest)
    {
        // Controllo di sicurezza per impedire la registrazione di account duplicati
        if (userRepository.existsByUsername(registerRequest.getUsername()))
        {
            throw new IllegalStateException("Username already exists");
        }


        // Costruzione e popolamento della nuova entità User
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        // Punto 7 Step 2: Cifratura della password in formato HASH tramite BCrypt
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEnabled(true); // Account attivo di default per Spring Security
        newUser.setBanned(false); // Stato iniziale di sblocco


        Profile newProfile = new Profile();
        newProfile.setFirstName(registerRequest.getFirstName());
        newProfile.setLastName(registerRequest.getLastName());
        newProfile.setCity(registerRequest.getCity());
        // bio e avatarUrl rimangono inizialmente null (o stringhe vuote) e modificabili in seguito su /me

        // Configurazione del legame bidirezionale 1-1
        newProfile.setUser(newUser);   // Lato proprietario (scrive la FK user_id)
        newUser.setProfile(newProfile); // Lato inverso (gestito grazie a CascadeType.ALL)

        // Salvataggio utente con rispettivo id automatico nel DB
        userRepository.save(newUser);


        // Creazione e assegnazione del ruolo predifinito per il primo accesso
        Role defaulRole = new Role();
        defaulRole.setUsername(newUser.getUsername());
        defaulRole.setAuthority("ROLE_USER"); // Prefisso standard obbligatorio per il framework
        roleRepository.save(defaulRole);


        return newUser;

    }
}
