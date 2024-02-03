package ua.goit.urlshortener.url;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.goit.urlshortener.user.UserEntity;

import java.time.LocalDate;

@Data
@Builder
@Entity
@Table(name = "urls")
@NoArgsConstructor
@AllArgsConstructor
public class UrlEntity {

    public static final int VALID_DAYS = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String shortUrl;

    @Column(nullable = false)
    private String url;

    @Column
    private String description;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private int visitCount;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
        this.expirationDate = this.createdDate.plusDays(VALID_DAYS);
    }
}
