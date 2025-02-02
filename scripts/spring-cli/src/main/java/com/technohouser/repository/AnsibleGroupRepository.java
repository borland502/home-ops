package com.technohouser.repository;

import com.technohouser.entities.AnsibleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AnsibleGroupRepository extends JpaRepository<AnsibleGroup, String> {

}
