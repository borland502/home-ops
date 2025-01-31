package com.technohouser.repository;

import com.technohouser.entities.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface HostRepository extends JpaRepository<Host, Long> {

}
