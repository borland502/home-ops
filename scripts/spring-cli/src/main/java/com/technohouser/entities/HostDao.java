package com.technohouser.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hosts")
public class HostDao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "host_version") // Changed from system_version to host_version
  private String version;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "manufacturer", column = @Column(name = "system_manufacturer")),
      @AttributeOverride(name = "model", column = @Column(name = "system_model")),
      @AttributeOverride(name = "version", column = @Column(name = "system_version")),
      @AttributeOverride(name = "serial", column = @Column(name = "system_serial")),
      @AttributeOverride(name = "uuid", column = @Column(name = "system_uuid")),
      @AttributeOverride(name = "sku", column = @Column(name = "system_sku")),
      @AttributeOverride(name = "virtual", column = @Column(name = "system_virtual")),
      @AttributeOverride(name = "type", column = @Column(name = "system_type"))
  })
  private SystemDao system;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "vendor", column = @Column(name = "bios_vendor")),
      @AttributeOverride(name = "version", column = @Column(name = "bios_version")),
      @AttributeOverride(name = "releaseDate", column = @Column(name = "bios_release_date")),
      @AttributeOverride(name = "revision", column = @Column(name = "bios_revision"))
  })
  private BiosDao bios;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "manufacturer", column = @Column(name = "baseboard_manufacturer")),
      @AttributeOverride(name = "model", column = @Column(name = "baseboard_model")),
      @AttributeOverride(name = "version", column = @Column(name = "baseboard_version")),
      @AttributeOverride(name = "serial", column = @Column(name = "baseboard_serial")),
      @AttributeOverride(name = "assetTag", column = @Column(name = "baseboard_asset_tag")),
      @AttributeOverride(name = "memMax", column = @Column(name = "baseboard_mem_max")),
      @AttributeOverride(name = "memSlots", column = @Column(name = "baseboard_mem_slots"))
  })
  private BaseboardDao baseboard;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "manufacturer", column = @Column(name = "chassis_manufacturer")),
      @AttributeOverride(name = "model", column = @Column(name = "chassis_model")),
      @AttributeOverride(name = "type", column = @Column(name = "chassis_type")),
      @AttributeOverride(name = "version", column = @Column(name = "chassis_version")),
      @AttributeOverride(name = "serial", column = @Column(name = "chassis_serial")),
      @AttributeOverride(name = "assetTag", column = @Column(name = "chassis_asset_tag")),
      @AttributeOverride(name = "sku", column = @Column(name = "chassis_sku"))
  })
  private ChassisDao chassis;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "platform", column = @Column(name = "os_platform")),
      @AttributeOverride(name = "distro", column = @Column(name = "os_distro")),
      @AttributeOverride(name = "release", column = @Column(name = "os_release")),
      @AttributeOverride(name = "codename", column = @Column(name = "os_codename")),
      @AttributeOverride(name = "kernel", column = @Column(name = "os_kernel")),
      @AttributeOverride(name = "arch", column = @Column(name = "os_arch")),
      @AttributeOverride(name = "hostname", column = @Column(name = "os_hostname")),
      @AttributeOverride(name = "fqdn", column = @Column(name = "os_fqdn")),
      @AttributeOverride(name = "codepage", column = @Column(name = "os_codepage")),
      @AttributeOverride(name = "logofile", column = @Column(name = "os_logofile")),
      @AttributeOverride(name = "serial", column = @Column(name = "os_serial")),
      @AttributeOverride(name = "build", column = @Column(name = "os_build")),
      @AttributeOverride(name = "servicepack", column = @Column(name = "os_servicepack")),
      @AttributeOverride(name = "uefi", column = @Column(name = "os_uefi"))
  })
  private OSDao os;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "os", column = @Column(name = "uuid_os")),
      @AttributeOverride(name = "hardware", column = @Column(name = "uuid_hardware")),
      @AttributeOverride(name = "macs", column = @Column(name = "uuid_macs"))
  })
  private UUIDDao uuid;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "kernel", column = @Column(name = "versions_kernel")),
      @AttributeOverride(name = "openssl", column = @Column(name = "versions_openssl")),
      @AttributeOverride(name = "systemOpenssl", column = @Column(name = "versions_system_openssl")),
      @AttributeOverride(name = "systemOpensslLib", column = @Column(name = "versions_system_openssl_lib")),
      @AttributeOverride(name = "python", column = @Column(name = "versions_python")),
      @AttributeOverride(name = "systemPython", column = @Column(name = "versions_system_python")),
      @AttributeOverride(name = "systemPythonLib", column = @Column(name = "versions_system_python_lib")),
      @AttributeOverride(name = "pip", column = @Column(name = "versions_pip")),
      @AttributeOverride(name = "pip3", column = @Column(name = "versions_pip3")),
      @AttributeOverride(name = "systemPip", column = @Column(name = "versions_system_pip")),
      @AttributeOverride(name = "systemPip3", column = @Column(name = "versions_system_pip3")),
      @AttributeOverride(name = "virtualenv", column = @Column(name = "versions_virtualenv")),
      @AttributeOverride(name = "systemVirtualenv", column = @Column(name = "versions_system_virtualenv")),
      @AttributeOverride(name = "systemSitePackages", column = @Column(name = "versions_system_site_packages")),
      @AttributeOverride(name = "systemSitePackages3", column = @Column(name = "versions_system_site_packages3")),
      @AttributeOverride(name = "systemSitePackagesPip", column = @Column(name = "versions_system_site_packages_pip")),
      @AttributeOverride(name = "systemSitePackagesPip3", column = @Column(name = "versions_system_site_packages_pip3")),
      @AttributeOverride(name = "distro", column = @Column(name = "versions_distro")),
      @AttributeOverride(name = "distroCPE", column = @Column(name = "versions_distro_cpe"))
  })
  private VersionsDao versions;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "vendor", column = @Column(name = "cpu_vendor")),
      @AttributeOverride(name = "family", column = @Column(name = "cpu_family")),
      @AttributeOverride(name = "model", column = @Column(name = "cpu_model")),
      @AttributeOverride(name = "stepping", column = @Column(name = "cpu_stepping")),
      @AttributeOverride(name = "revision", column = @Column(name = "cpu_revision")),
      @AttributeOverride(name = "voltage", column = @Column(name = "cpu_voltage")),
      @AttributeOverride(name = "speed", column = @Column(name = "cpu_speed")),
      @AttributeOverride(name = "speedMin", column = @Column(name = "cpu_speed_min")),
      @AttributeOverride(name = "speedMax", column = @Column(name = "cpu_speed_max")),
      @AttributeOverride(name = "governor", column = @Column(name = "cpu_governor")),
      @AttributeOverride(name = "cores", column = @Column(name = "cpu_cores")),
      @AttributeOverride(name = "physicalCores", column = @Column(name = "cpu_physical_cores")),
      @AttributeOverride(name = "processors", column = @Column(name = "cpu_processors")),
      @AttributeOverride(name = "socket", column = @Column(name = "cpu_socket")),
      @AttributeOverride(name = "cacheL1d", column = @Column(name = "cpu_cache_l1d")),
      @AttributeOverride(name = "cacheL1i", column = @Column(name = "cpu_cache_l1i")),
      @AttributeOverride(name = "cacheL2", column = @Column(name = "cpu_cache_l2")),
      @AttributeOverride(name = "cacheL3", column = @Column(name = "cpu_cache_l3"))
  })
  private CPUDao cpu;

  @OneToMany(mappedBy = "host")
  private List<GraphicsDao> graphics;

  @OneToMany(mappedBy = "host")
  private List<NetDao> net;

  @OneToMany(mappedBy = "host")
  private List<MemLayoutDao> memLayout;

  @OneToMany(mappedBy = "host")
  private List<DiskLayoutDao> diskLayout;

  @Embedded
  private TimeDao time;

  @Embeddable
  @Getter
  @Setter
  public static class SystemDao {
    private String manufacturer;
    private String model;
    private String version;
    private String serial;
    private String uuid;
    private String sku;
    private boolean virtual;
    private String type;
  }

  @Embeddable
  @Getter
  @Setter
  public static class BiosDao {

    private String vendor;
    private String version;
    private String releaseDate;
    private String revision;
  }

  @Embeddable
  @Getter
  @Setter
  public static class BaseboardDao {

    private String manufacturer;
    private String model;
    private String version;
    private String serial;
    private String assetTag;
    private long memMax;
    private int memSlots;
  }

  @Embeddable
  @Getter
  @Setter
  public static class ChassisDao {

    private String manufacturer;
    private String model;
    private String type;
    private String version;
    private String serial;
    private String assetTag;
    private String sku;
  }

  @Embeddable
  @Getter
  @Setter
  public static class OSDao {

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

  @Embeddable
  @Getter
  @Setter
  public static class UUIDDao {

    private String os;
    private String hardware;
    private List<String> macs;
  }

  @Embeddable
  @Getter
  @Setter
  public static class VersionsDao {

    private String kernel;
    private String openssl;
    private String systemOpenssl;
    private String systemOpensslLib;
    private String python;
    private String systemPython;
    private String systemPythonLib;
    private String pip;
    private String pip3;
    private String systemPip;
    private String systemPip3;
    private String virtualenv;
    private String systemVirtualenv;
    private String systemSitePackages;
    private String systemSitePackages3;
    private String systemSitePackagesPip;
    private String systemSitePackagesPip3;
    private String distro;
    private String distroCPE;
  }

  @Embeddable
  @Getter
  @Setter
  public static class CPUDao {

    private String vendor;
    private String family;
    private String model;
    private String stepping;
    private String revision;
    private String voltage;
    private String speed;
    private String speedMin;
    private String speedMax;
    private String governor;
    private String cores;
    private String physicalCores;
    private String processors;
    private String socket;
    private String cacheL1d;
    private String cacheL1i;
    private String cacheL2;
    private String cacheL3;
  }

  @Entity
  @Getter
  @Setter
  public static class NetDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private HostDao host;

    private String iface;
    private String ifaceName;
    private boolean isDefault;
    private String ipv4;
    private String ipv4subnet;
    private String ipv6;
    private String ipv6subnet;
    private String name;
    private String mac;
    private boolean internal;
    private boolean virtual;
    private String operstate;
    private String type;
    private String duplex;
    private int mtu;
    private int speed;
    private boolean dhcp;
    private String dnsSuffix;
    private String ieee8021xAuth;
    private String ieee8021xState;
    private int carrierChanges;
  }

  @Entity
  @Getter
  @Setter
  public static class MemLayoutDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private HostDao host;

    private String bank;
    private String type;
    private String size;
    private String formFactor;
    private String manufacturer;
    private String serial;
    private String partNum;
    private String clockSpeed;
    private String voltageConfigured;
    private String voltageMin;
    private String voltageMax;
    private boolean ecc;
  }

  @Entity
  @Getter
  @Setter
  public static class DiskLayoutDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private HostDao host;

    private String device;
    private String type;
    private String name;
    private String vendor;
    private long size;
    private String serialNum;
    private String bytesPerSector;
    private String totalCylinders;
    private String totalHeads;
    private String totalSectors;
    private String totalTracks;
    private String tracksPerCylinder;
    private String sectorsPerTrack;
    private String firmwareRevision;
    private String interfaceType;
    private String smartStatus;
    private String temperature;
    private String serialNumber;
  }

  @Embeddable
  @Getter
  @Setter
  public static class TimeDao {

    private long current;
    private long uptime;
    private String timezone;
    private String timezoneName;
  }
}
