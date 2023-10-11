package ru.relex.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relex.entity.RawData;

/**
 * DAO is a layer for working with DB
 */

/**
 * entity type and primary key type
 */


public interface RawDataDAO extends JpaRepository<RawData, Long> // JpaRepository has many basic methods for working with DB
{
}
