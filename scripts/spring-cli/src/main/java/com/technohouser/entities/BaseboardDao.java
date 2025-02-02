package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "baseboards")
@Data
@NoArgsConstructor
public class BaseboardDao {
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

  @Column(name = "asset_tag")
  private String assetTag;

  @Column(name = "mem_max")
  private long memMax;

  @Column(name = "mem_slots")
  private int memSlots;
}
