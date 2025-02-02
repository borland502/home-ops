package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chassis")
@Data
@NoArgsConstructor
public class ChassisDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "manufacturer")
  private String manufacturer;

  @Column(name = "model")
  private String model;

  @Column(name = "type")
  private String type;

  @Column(name = "version")
  private String version;

  @Column(name = "serial")
  private String serial;

  @Column(name = "asset_tag")
  private String assetTag;

  @Column(name = "sku")
  private String sku;
}
