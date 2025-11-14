package com.moura.bitroute.repository;

import com.moura.bitroute.model.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasteRepository extends JpaRepository<Paste, String> {
    
    boolean existsByShortlink(String shortlink);
}