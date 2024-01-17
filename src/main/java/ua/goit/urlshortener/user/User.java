package ua.goit.urlshortener.user;

import jakarta.persistence.*;
import lombok.*;
import ua.goit.urlshortener.url.Url;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table (name = "users")
@NoArgsConstructor
@AllArgsConstructor
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
    private List<Url> urls = new ArrayList<>();
}
