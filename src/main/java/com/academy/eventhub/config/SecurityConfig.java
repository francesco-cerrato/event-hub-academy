package com.academy.eventhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    // Configura il gestore delle credenziali basato sul database (Richiesto allo Step 2, punto 5)
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource)
    {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        /*
            Istruisce Spring Security su quale query SQL lanciare
            per verificare l'esistenza dell'utente al login
         */
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select username, password, enabled from users where username = ?");


        /*
            Istruisce Spring Security su come recuperare i ruoli associati a quell'utente
            per i controlli RBAC (Role-Based Access Control)
            Nota: Punta alla tabella 'authorities' per combaciare perfettamente con la Entity Java Role
         */
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select username, authority from authorities where username=?");

        return jdbcUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        /*
            Questo bean crea ed attua l'algoritmo BCrypt
            per criptare le password tramite hash
         */
        return new BCryptPasswordEncoder();
    }

    // Configura le regole di accesso agli URL e attiva l'autenticazione HTTP Basic
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception
    {
        // Disabilitazione CSRF (Cross-Site Request Forgery)
        http.csrf(csrf -> csrf.disable());

        /*
            Configurazione delle regole di autorizzazione per le chiamate HTTP
            le regole più specifiche vanno messe sempre prima di quelle più generiche
         */
        http.authorizeHttpRequests(auth -> auth
                // Permesso a chiunque, anche non loggato, di fare la registrazione
                .requestMatchers("/auth/signup").permitAll()
                // Permette l'accesso alla gestione degli errori di Spring per non nascondere i 404
                .requestMatchers("/error").permitAll()

                // Tutti possono leggere gli eventi (GET è pubblico)
                .requestMatchers(HttpMethod.GET,"/api/events/**").permitAll()

                /*
                    REGOLA AGGIUNTA PER LO STEP 7: Consente la prenotazione sia a USER che a ORGANIZER
                    Va inserita PRIMA della regola generica sui blocchi degli eventi, altrimenti viene sovrascritta!
                 */
                .requestMatchers(HttpMethod.POST, "/api/events/{id}/book").hasAnyRole("USER", "ORGANIZER", "ADMIN")


                // Regole specifiche per la gestione degli eventi (Solo per gli ORGANIZER)
                .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ORGANIZER")



                // PUNTO 6 STEP 3: Limita tutti i path che iniziano con /admin/ solo agli utenti ADMIN
                // Nota: Cerca il ruolo 'ROLE_ADMIN' nel DB, ma nel metodo si scrive solo 'ADMIN'
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasAnyRole("USER", "ORGANIZER", "ADMIN")




                // Qualsiasi altra richiesta dell'applicazione richiederà il login obbligatorio
                .anyRequest().authenticated());

        // Attivazione dell'autenticazione standard HTTP Basic (Username e Password nell'header)
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
