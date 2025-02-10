package com.technohouser.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "displays")
public class DisplayDao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "graphics_id")
  private GraphicsDao graphics;

  private String vendor;
  private String model;
  private String deviceName;
  private String serial;
  private String connection;
  private int pixelDepth;
  private int resolutionX;
  private int resolutionY;
  private int sizeX;
  private int sizeY;
  private int currentResX;
  private int currentResY;
  private String refreshRate;
  private String displayId;
  private String vendorId;
  private String productionYear;
  private boolean main;
  private int positionX;
  private int positionY;
  private int currentRefreshRate;
  private boolean isBuiltin;
}
