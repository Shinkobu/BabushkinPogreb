package ru.relex.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relex.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    /**
     * Check if user is already present in DB
     */
    AppUser findAppUserByTelegramUserId(Long id); // method is implemented by Spring(?)
}
