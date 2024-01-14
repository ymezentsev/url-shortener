package ua.goit.urlshortener.url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    List<Url> findByUserId(Long userId);

    @Query(nativeQuery = true, value = "select * from urls where user_id = :userId and expiration_date >= :now")
    List<Url> findUsersActiveUrls(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query(nativeQuery = true, value = "select * from urls where user_id = :userId and expiration_date < :now")
    List<Url> findUsersNotActiveUrls(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}
