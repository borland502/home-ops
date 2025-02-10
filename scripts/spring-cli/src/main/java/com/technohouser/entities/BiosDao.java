package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bios")
@Data
@NoArgsConstructor
public class BiosDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "vendor")
  private String vendor;

  @Column(name = "version")
  private String version;

  @Column(name = "release_date")
  private String releaseDate;

  @Column(name = "revision")
  private String revision;
}
