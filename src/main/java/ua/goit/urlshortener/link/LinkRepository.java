package ua.goit.urlshortener.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, String> {
    List<Link> findByUserId(Long userId);

    @Query(nativeQuery = true, value = "select * from links where user_id = :userId and expiration_date >= :now")
    List<Link> findUsersActiveLinks(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query(nativeQuery = true, value = "select * from links where user_id = :userId and expiration_date < :now")
    List<Link> findUsersNotActiveLinks(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}
