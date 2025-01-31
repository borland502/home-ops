package com.technohouser.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

/**
 * The Host model represents the machine information on which the application is
 * running.
 */
@Getter
public class Host {

  private String version;
  private System system;
  private Bios bios;
  private Baseboard baseboard;
  private Chassis chassis;
  private OS os;
  private UUID uuid;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record System(
      String manufacturer,
      String model,
      String version,
      String serial,
      String uuid,
      String sku,
      boolean virtual,
      String type) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Bios(
      String vendor,
      String version,
      String releaseDate,
      String revision) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Baseboard(
      String manufacturer,
      String model,
      String version,
      String serial,
      String assetTag,
      long memMax,
      int memSlots) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Chassis(
      String manufacturer,
      String model,
      String type,
      String version,
      String serial,
      String assetTag,
      String sku) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record OS(
      String platform,
      String distro,
      String release,
      String codename,
      String kernel,
      String arch,
      String hostname,
      String fqdn,
      String codepage,
      String logofile,
      String serial,
      String build,
      String servicepack,
      boolean uefi) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record UUID(
      String os,
      String hardware,
      List<String> macs) {
  }

}
