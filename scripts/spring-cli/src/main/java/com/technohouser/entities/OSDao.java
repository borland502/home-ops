package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "os")
@Data
@NoArgsConstructor
public class OSDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String platform;
  private String distro;
  private String release;
  private String codename;
  private String kernel;
  private String arch;
  private String hostname;
  private String fqdn;
  private String codepage;
  private String logofile;
  private String serial;
  private String build;
  private String servicepack;
  private boolean uefi;
}
