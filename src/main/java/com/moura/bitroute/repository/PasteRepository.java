package com.moura.bitroute.repository;

import com.moura.bitroute.model.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PasteRepository extends JpaRepository<Paste, String> {
    
    boolean existsByShortlink(String shortlink);
    
    @Query(value = "SELECT * FROM pastes WHERE expiration_length_in_minutes IS NOT NULL " +
           "AND DATE_ADD(created_at, INTERVAL expiration_length_in_minutes MINUTE) < :currentTime",
           nativeQuery = true)
    List<Paste> findExpiredPastes(@Param("currentTime") LocalDateTime currentTime);
}