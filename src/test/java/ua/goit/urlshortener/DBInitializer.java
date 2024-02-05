package ua.goit.urlshortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.goit.urlshortener.url.UrlEntity;
import ua.goit.urlshortener.url.UrlRepository;
import ua.goit.urlshortener.user.Role;
import ua.goit.urlshortener.user.UserEntity;
import ua.goit.urlshortener.user.UserRepository;

import java.time.LocalDate;

@Component
public class DBInitializer {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UrlRepository urlRepository;

    public void cleanAndPopulateDb() {
        userRepository.deleteAll();

        userRepository.save(UserEntity.builder()
                .username("testadmin")
                .password("{noop}qwerTy12")
                .role(Role.ADMIN)
                .build());

        userRepository.save(UserEntity.builder()
                .username("testuser1")
                .password("{noop}qwerTy12")
                .role(Role.USER)
                .build());

        userRepository.save(UserEntity.builder()
                .username("testuser2")
                .password("{noop}qwerTy12")
                .role(Role.USER)
                .build());

        cleanAndPopulateUrlTable();
    }

    public void cleanAndPopulateUrlTable() {
        urlRepository.deleteAll();

        urlRepository.save(new UrlEntity(null, "testurl1", "https://google.com/",
                "for test only1", userRepository.findByUsername("testadmin").orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));

        urlRepository.save(new UrlEntity(null, "testurl2", "https://some_long_named_portal.com/",
                "for test only2", userRepository.findByUsername("testadmin").orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));

        urlRepository.save(new UrlEntity(null, "testurl3", "https://some_long_named_portal.com/",
                "for test only3", userRepository.findByUsername("testuser1").orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));

        urlRepository.save(new UrlEntity(null, "testurl4", "https://some_long_named_portal.com/",
                "for test only4", userRepository.findByUsername("testuser2").orElseThrow(),
                LocalDate.now(), LocalDate.now().plusDays(10), 1));
    }
}
