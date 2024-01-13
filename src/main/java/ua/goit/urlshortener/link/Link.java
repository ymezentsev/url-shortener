package ua.goit.urlshortener.link;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.goit.urlshortener.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "links")
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    private static final int VALID_DAYS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String shortUrl;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private int visitCount;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.expirationDate = this.createdDate.plusDays(VALID_DAYS);
    }
}
