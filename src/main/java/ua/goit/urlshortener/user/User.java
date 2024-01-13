package ua.goit.urlshortener.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import ua.goit.urlshortener.link.Link;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column()
    private boolean enabled;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Link> links = new ArrayList<>();
}
