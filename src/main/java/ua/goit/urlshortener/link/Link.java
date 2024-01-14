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

    public static LinkDto buildLinkDto(Link link){
        return LinkDto.builder()
                .url(link.getUrl())
                .shortUrl(link.getShortUrl())
                .username(link.getUser().getUsername())
                .createdDate(link.getCreatedDate())
                .expirationDate(link.getExpirationDate())
                .visitCount(link.getVisitCount())
                .build();
    }
}
