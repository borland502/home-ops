package com.technohouser.config.properties.json;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Host properties bound to the json output of the <a
 * href=https://www.npmjs.com/package/systeminformation>systeminformation</a>
 * nodejs library.
 */
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "host")
public class HostProperties {

  private String version;
  private SystemInfo system;
  private BiosInfo bios;
  private BaseboardInfo baseboard;
  private ChassisInfo chassis;
  private OsInfo os;
  private UuidInfo uuid;
  private VersionsInfo versions;
  private CpuInfo cpu;
  private GraphicsInfo graphics;
  private List<NetInfo> net;
  private List<MemLayoutInfo> memLayout;
  private List<DiskLayoutInfo> diskLayout;
  private TimeInfo time;

  @Data
  @NoArgsConstructor
  public static class SystemInfo {
    private String manufacturer;
    private String model;
    private String version;
    private String serial;
    private String uuid;
    private String sku;
    private boolean virtual;
    private String type;
  }

  @Data
  @NoArgsConstructor
  public static class BiosInfo {
    private String vendor;
    private String version;
    private String releaseDate;
    private String revision;
  }

  @Data
  @NoArgsConstructor
  public static class BaseboardInfo {
    private String manufacturer;
    private String model;
    private String version;
    private String serial;
    private String assetTag;
    private Long memMax;
    private Integer memSlots;
  }

  @Data
  @NoArgsConstructor
  public static class ChassisInfo {
    private String manufacturer;
    private String model;
    private String type;
    private String version;
    private String serial;
    private String assetTag;
    private String sku;
  }

  @Data
  @NoArgsConstructor
  public static class OsInfo {
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

  @Data
  @NoArgsConstructor
  public static class UuidInfo {
    private String os;
    private String hardware;
    private List<String> macs;
  }

  @Data
  @NoArgsConstructor
  public static class VersionsInfo {
    private String kernel;
    private String apache;
    private String bash;
    private String docker;
    private String gcc;
    private String git;
    private String homebrew;
    private String java;
    private String node;
    private String npm;
    private String openssl;
    private String perl;
    private String python3;
    private String systemOpenssl;
    private String systemOpensslLib;
    private String tsc;
    private String v8;
    private String yarn;
    private String zsh;
  }

  @Data
  @NoArgsConstructor
  public static class CpuInfo {
    private String manufacturer;
    private String brand;
    private String vendor;
    private String family;
    private String model;
    private String stepping;
    private String revision;
    private String voltage;
    private Double speed;
    private Double speedMin;
    private Double speedMax;
    private String governor;
    private Integer cores;
    private Integer physicalCores;
    private Integer performanceCores;
    private Integer efficiencyCores;
    private Integer processors;
    private String socket;
    private String flags;
    private boolean virtualization;
    private CpuCache cache;

    @Data
    @NoArgsConstructor
    public static class CpuCache {
      private Integer l1d;
      private Integer l1i;
      private Integer l2;
      private Integer l3;
    }
  }

  @Data
  @NoArgsConstructor
  public static class GraphicsInfo {
    private List<Controller> controllers;
    private List<Display> displays;

    @Data
    @NoArgsConstructor
    public static class Controller {
      private String vendor;
      private String model;
      private String bus;
      private boolean vramDynamic;
      private Integer vram;
      private String deviceId;
      private String vendorId;
      private boolean external;
      private String cores;
      private String metalVersion;
    }

    @Data
    @NoArgsConstructor
    public static class Display {
      private String vendor;
      private String vendorId;
      private String model;
      private String productionYear;
      private String serial;
      private String displayId;
      private boolean main;
      private boolean builtin;
      private String connection;
      private Integer sizeX;
      private Integer sizeY;
      private Integer pixelDepth;
      private Integer resolutionX;
      private Integer resolutionY;
      private Integer currentResX;
      private Integer currentResY;
      private Integer positionX;
      private Integer positionY;
      private Integer currentRefreshRate;
    }
  }

  @Data
  @NoArgsConstructor
  public static class NetInfo {
    private String iface;
    private String ifaceName;
    private boolean isDefault;
    private String ip4;
    private String ip4subnet;
    private String ip6;
    private String ip6subnet;
    private String mac;
    private boolean internal;
    private boolean virtual;
    private String operstate;
    private String type;
    private String duplex;
    private Integer mtu;
    private Double speed;
    private boolean dhcp;
    private String dnsSuffix;
    private String ieee8021xAuth;
    private String ieee8021xState;
    private Integer carrierChanges;
  }

  @Data
  @NoArgsConstructor
  public static class MemLayoutInfo {
    private Long size;
    private String bank;
    private String type;
    private boolean ecc;
    private Integer clockSpeed;
    private String formFactor;
    private String manufacturer;
    private String partNum;
    private String serialNum;
    private Integer voltageConfigured;
    private Integer voltageMin;
    private Integer voltageMax;
  }

  @Data
  @NoArgsConstructor
  public static class DiskLayoutInfo {
    private String device;
    private String type;
    private String name;
    private String vendor;
    private Long size;
    private Integer bytesPerSector;
    private Integer totalCylinders;
    private Integer totalHeads;
    private Integer totalSectors;
    private Integer totalTracks;
    private Integer tracksPerCylinder;
    private Integer sectorsPerTrack;
    private String firmwareRevision;
    private String serialNum;
    private String interfaceType;
    private String smartStatus;
    private Integer temperature;
  }

  @Data
  @NoArgsConstructor
  public static class TimeInfo {
    private Long current;
    private Integer uptime;
    private String timezone;
    private String timezoneName;
  }

}
