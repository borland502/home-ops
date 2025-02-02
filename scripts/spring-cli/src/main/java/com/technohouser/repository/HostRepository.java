package com.technohouser.repository;

import com.technohouser.entities.HostDao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface HostRepository extends JpaRepository<HostDao, Long> {
  @Query("SELECT h FROM HostDao h WHERE h.os.hostname = :hostname")
  Optional<HostDao> findByHostname(@Param("hostname") String hostname);
}
