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
@Table(name = "mem_layouts")
@Data
@NoArgsConstructor
public class MemLayoutDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bank")
  private String bank;

  @Column(name = "type")
  private String type;

  @Column(name = "size")
  private String size;

  @Column(name = "form_factor")
  private String formFactor;

  @Column(name = "manufacturer")
  private String manufacturer;

  @Column(name = "serial")
  private String serial;

  @Column(name = "part_num")
  private String partNum;

  @Column(name = "clock_speed")
  private String clockSpeed;

  @Column(name = "voltage_configured")
  private String voltageConfigured;

  @Column(name = "voltage_min")
  private String voltageMin;

  @Column(name = "voltage_max")
  private String voltageMax;

  @Column(name = "ecc")
  private boolean ecc;
}
