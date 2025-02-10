package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "controllers")
public class ControllerDao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "graphics_id")
  private GraphicsDao graphics;

  private String model;
  private String vendor;
  private String bus;
  private String vram;
  private boolean vramDynamic;
  private String deviceId;
  private String vendorId;
  private boolean external;
  private int cores;
  private String metalVersion;
}
