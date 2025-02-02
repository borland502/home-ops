package com.technohouser.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ansible_group_cd")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnsibleGroup {

  @Id
  @Column(name = "group_cd")
  private String groupCd;

  @Column(name = "group_name")
  private String groupName;

}
