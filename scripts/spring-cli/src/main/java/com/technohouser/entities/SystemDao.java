package com.technohouser.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "systems")
@Data
@NoArgsConstructor
public class SystemDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "manufacturer")
  private String manufacturer;

  @Column(name = "model")
  private String model;

  @Column(name = "version")
  private String version;

  @Column(name = "serial")
  private String serial;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "sku")
  private String sku;

  @Column(name = "virtual")
  private boolean virtual;

  @Column(name = "type")
  private String type;
}
