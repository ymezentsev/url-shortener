package ua.goit.urlshortener.url;

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
@Table(name = "urls")
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    private static final int VALID_DAYS = 30;

    @Id
    private String shortUrl;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public void incrementVisitCount(){
        this.visitCount++;
    }

    public static UrlDto buildUrlDto(Url url){
        return UrlDto.builder()
                .url(url.getUrl())
                .shortUrl(url.getShortUrl())
                .username(url.getUser().getUsername())
                .createdDate(url.getCreatedDate())
                .expirationDate(url.getExpirationDate())
                .visitCount(url.getVisitCount())
                .build();
    }
}
