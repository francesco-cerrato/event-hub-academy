package com.academy.eventhub.service;

import com.academy.eventhub.entity.Role;
import com.academy.eventhub.entity.User;
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

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(String username, String password)
    {
        // Controllo di sicurezza per impedire la registrazione di account duplicati
        if (userRepository.existsByUsername(username))
        {
            throw new RuntimeException("Username already exists");
            // Da sostituire con un'eccezione custom nello Step 3
        }


        // Costruzione e popolamento della nuova entità User
        User newUser = new User();
        newUser.setUsername(username);
        // Punto 7 Step 2: Cifratura della password in formato HASH tramite BCrypt
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEnabled(true); // Account attivo di default per Spring Security
        newUser.setBanned(false); // Stato iniziale di sblocco

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
