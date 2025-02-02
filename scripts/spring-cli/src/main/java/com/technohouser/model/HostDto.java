package com.technohouser.model;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;

import com.technohouser.entities.HostDao;
import com.technohouser.entities.HostDao.BaseboardDao;
import com.technohouser.entities.HostDao.BiosDao;
import com.technohouser.entities.HostDao.CPUDao;
import com.technohouser.entities.HostDao.ChassisDao;
import com.technohouser.entities.HostDao.DiskLayoutDao;
import com.technohouser.entities.HostDao.NetDao;
import com.technohouser.entities.HostDao.OSDao;
import com.technohouser.entities.HostDao.SystemDao;
import com.technohouser.entities.HostDao.TimeDao;
import com.technohouser.entities.HostDao.UUIDDao;
import com.technohouser.entities.HostDao.VersionsDao;
import com.technohouser.entities.HostDao.MemLayoutDao;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Host model represents the machine information on which the application is
 * running.
 */
@Getter
@Setter
@NoArgsConstructor
public class HostDto {

  private Long id;
  @JsonProperty("version")
  private String hostVersion; // Changed from systemVersion to hostVersion
  private System system;
  private Bios bios;
  private Baseboard baseboard;
  private Chassis chassis;
  private OS os;
  private UUID uuid;
  private Versions versions;
  private CPU cpu;
  @JsonDeserialize(using = GraphicsDeserializer.class)
  private List<Graphics> graphics;
  private List<Net> net;
  private List<MemLayout> memLayout;
  private List<DiskLayout> diskLayout;
  private Time time;

  public static HostDto createFromHostDao(HostDao hostDao) {
    return new HostDto(hostDao);
  }

  private HostDto(HostDao hostDao) {
    this.id = hostDao.getId();
    this.hostVersion = hostDao.getVersion();
    this.system = new System(
        hostDao.getSystem().getManufacturer(),
        hostDao.getSystem().getModel(),
        hostDao.getSystem().getVersion(),
        hostDao.getSystem().getSerial(),
        hostDao.getSystem().getUuid(),
        hostDao.getSystem().getSku(),
        hostDao.getSystem().isVirtual(),
        hostDao.getSystem().getType());
    this.bios = new Bios(
        hostDao.getBios().getVendor(),
        hostDao.getBios().getVersion(),
        hostDao.getBios().getReleaseDate(),
        hostDao.getBios().getRevision());
    this.baseboard = new Baseboard(
        hostDao.getBaseboard().getManufacturer(),
        hostDao.getBaseboard().getModel(),
        hostDao.getBaseboard().getVersion(),
        hostDao.getBaseboard().getSerial(),
        hostDao.getBaseboard().getAssetTag(),
        hostDao.getBaseboard().getMemMax(),
        hostDao.getBaseboard().getMemSlots());
    this.chassis = new Chassis(
        hostDao.getChassis().getManufacturer(),
        hostDao.getChassis().getModel(),
        hostDao.getChassis().getType(),
        hostDao.getChassis().getVersion(),
        hostDao.getChassis().getSerial(),
        hostDao.getChassis().getAssetTag(),
        hostDao.getChassis().getSku());
    this.os = new OS(
        hostDao.getOs().getPlatform(),
        hostDao.getOs().getDistro(),
        hostDao.getOs().getRelease(),
        hostDao.getOs().getCodename(),
        hostDao.getOs().getKernel(),
        hostDao.getOs().getArch(),
        hostDao.getOs().getHostname(),
        hostDao.getOs().getFqdn(),
        hostDao.getOs().getCodepage(),
        hostDao.getOs().getLogofile(),
        hostDao.getOs().getSerial(),
        hostDao.getOs().getBuild(),
        hostDao.getOs().getServicepack(),
        hostDao.getOs().isUefi());
    this.uuid = new UUID(
        hostDao.getUuid().getOs(),
        hostDao.getUuid().getHardware(),
        hostDao.getUuid().getMacs());
    this.versions = new Versions(
        hostDao.getVersions().getKernel(),
        hostDao.getVersions().getOpenssl(),
        hostDao.getVersions().getSystemOpenssl(),
        hostDao.getVersions().getSystemOpensslLib(),
        hostDao.getVersions().getPython(),
        hostDao.getVersions().getSystemPython(),
        hostDao.getVersions().getSystemPythonLib(),
        hostDao.getVersions().getPip(),
        hostDao.getVersions().getPip3(),
        hostDao.getVersions().getSystemPip(),
        hostDao.getVersions().getSystemPip3(),
        hostDao.getVersions().getVirtualenv(),
        hostDao.getVersions().getSystemVirtualenv(),
        hostDao.getVersions().getSystemSitePackages(),
        hostDao.getVersions().getSystemSitePackages3(),
        hostDao.getVersions().getSystemSitePackagesPip(),
        hostDao.getVersions().getSystemSitePackagesPip3(),
        hostDao.getVersions().getDistro(),
        hostDao.getVersions().getDistroCPE());
    this.cpu = new CPU(
        hostDao.getCpu().getVendor(),
        hostDao.getCpu().getFamily(),
        hostDao.getCpu().getModel(),
        hostDao.getCpu().getStepping(),
        hostDao.getCpu().getRevision(),
        hostDao.getCpu().getVoltage(),
        hostDao.getCpu().getSpeed(),
        hostDao.getCpu().getSpeedMin(),
        hostDao.getCpu().getSpeedMax(),
        hostDao.getCpu().getGovernor(),
        hostDao.getCpu().getCores(),
        hostDao.getCpu().getPhysicalCores(),
        hostDao.getCpu().getProcessors(),
        hostDao.getCpu().getSocket(),
        hostDao.getCpu().getCacheL1d(),
        hostDao.getCpu().getCacheL1i(),
        hostDao.getCpu().getCacheL2(),
        hostDao.getCpu().getCacheL3());
    this.graphics = hostDao.getGraphics().stream().map(graphics -> new Graphics(
        graphics.getId(),
        graphics.getControllers().stream().map(controller -> new Controller(
            controller.getModel(),
            controller.getVendor(),
            controller.getBus(),
            controller.getVram(),
            controller.isVramDynamic(),
            controller.getDeviceId(),
            controller.getVendorId(),
            controller.isExternal(),
            controller.getCores(),
            controller.getMetalVersion())).collect(Collectors.toList()),
        graphics.getDisplays().stream().map(display -> new Display(
            display.getVendor(),
            display.getModel(),
            display.getDeviceName(),
            display.getSerial(),
            display.getConnection(),
            display.getPixelDepth(),
            display.getResolutionX(),
            display.getResolutionY(),
            display.getSizeX(),
            display.getSizeY(),
            display.getCurrentResX(),
            display.getCurrentResY(),
            display.getRefreshRate(),
            display.getDisplayId(),
            display.getVendorId(),
            display.getProductionYear(),
            display.isMain(),
            display.getPositionX(),
            display.getPositionY(),
            display.getCurrentRefreshRate(),
            display.isBuiltin())).collect(Collectors.toList())))
        .collect(Collectors.toList());
    this.net = hostDao.getNet().stream().map(net -> new Net(
        net.getIface(),
        net.getIfaceName(),
        net.isDefault(),
        net.getIpv4(),
        net.getIpv4subnet(),
        net.getIpv6(),
        net.getIpv6subnet(),
        net.getName(),
        net.getMac(),
        net.isInternal(),
        net.isVirtual(),
        net.getOperstate(),
        net.getType(),
        net.getDuplex(),
        net.getMtu(),
        net.getSpeed(),
        net.isDhcp(),
        net.getDnsSuffix(),
        net.getIeee8021xAuth(),
        net.getIeee8021xState(),
        net.getCarrierChanges())).collect(Collectors.toList());
    this.memLayout = hostDao.getMemLayout().stream().map(memLayout -> new MemLayout(
        memLayout.getBank(),
        memLayout.getType(),
        memLayout.getSize(),
        memLayout.getFormFactor(),
        memLayout.getManufacturer(),
        memLayout.getSerial(),
        memLayout.getPartNum(),
        memLayout.getClockSpeed(),
        memLayout.getVoltageConfigured(),
        memLayout.getVoltageMin(),
        memLayout.getVoltageMax(),
        memLayout.isEcc())).collect(Collectors.toList());
    this.diskLayout = hostDao.getDiskLayout().stream().map(diskLayout -> new DiskLayout(
        diskLayout.getDevice(),
        diskLayout.getType(),
        diskLayout.getName(),
        diskLayout.getVendor(),
        diskLayout.getSize(),
        diskLayout.getSerialNum(),
        diskLayout.getBytesPerSector(),
        diskLayout.getTotalCylinders(),
        diskLayout.getTotalHeads(),
        diskLayout.getTotalSectors(),
        diskLayout.getTotalTracks(),
        diskLayout.getTracksPerCylinder(),
        diskLayout.getSectorsPerTrack(),
        diskLayout.getFirmwareRevision(),
        diskLayout.getInterfaceType(),
        diskLayout.getSmartStatus(),
        diskLayout.getTemperature(),
        diskLayout.getSerialNumber())).collect(Collectors.toList());
    this.time = new Time(
        hostDao.getTime().getCurrent(),
        hostDao.getTime().getUptime(),
        hostDao.getTime().getTimezone(),
        hostDao.getTime().getTimezoneName());
  }

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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Versions(
      String kernel,
      String openssl,
      String systemOpenssl,
      String systemOpensslLib,
      String python,
      String systemPython,
      String systemPythonLib,
      String pip,
      String pip3,
      String systemPip,
      String systemPip3,
      String virtualenv,
      String systemVirtualenv,
      String systemSitePackages,
      String systemSitePackages3,
      String systemSitePackagesPip,
      String systemSitePackagesPip3,
      String distro,
      String distroCPE) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record CPU(
      String vendor,
      String family,
      String model,
      String stepping,
      String revision,
      String voltage,
      String speed,
      String speedMin,
      String speedMax,
      String governor,
      String cores,
      String physicalCores,
      String processors,
      String socket,
      String cacheL1d,
      String cacheL1i,
      String cacheL2,
      String cacheL3) {
  }

  @Data
  @NoArgsConstructor
  public static class Graphics {
    private Long id;
    private List<Controller> controllers;
    private List<Display> displays;

    public Graphics(Long id, List<Controller> controllers, List<Display> displays) {
      this.id = id;
      this.controllers = controllers;
      this.displays = displays;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Controller {
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

    public Controller(String model, String vendor, String bus, String vram, boolean vramDynamic, String deviceId,
        String vendorId, boolean external, int cores, String metalVersion) {
      this.model = model;
      this.vendor = vendor;
      this.bus = bus;
      this.vram = vram;
      this.vramDynamic = vramDynamic;
      this.deviceId = deviceId;
      this.vendorId = vendorId;
      this.external = external;
      this.cores = cores;
      this.metalVersion = metalVersion;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Display {
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
    private boolean builtin;

    public Display(String vendor, String model, String deviceName, String serial, String connection, int pixelDepth,
        int resolutionX, int resolutionY, int sizeX, int sizeY, int currentResX, int currentResY, String refreshRate,
        String displayId, String vendorId, String productionYear, boolean main, int positionX, int positionY,
        int currentRefreshRate, boolean builtin) {
      this.vendor = vendor;
      this.model = model;
      this.deviceName = deviceName;
      this.serial = serial;
      this.connection = connection;
      this.pixelDepth = pixelDepth;
      this.resolutionX = resolutionX;
      this.resolutionY = resolutionY;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.currentResX = currentResX;
      this.currentResY = currentResY;
      this.refreshRate = refreshRate;
      this.displayId = displayId;
      this.vendorId = vendorId;
      this.productionYear = productionYear;
      this.main = main;
      this.positionX = positionX;
      this.positionY = positionY;
      this.currentRefreshRate = currentRefreshRate;
      this.builtin = builtin;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Net(
      String iface,
      String ifaceName,
      boolean isDefault,
      String ipv4,
      String ipv4subnet,
      String ipv6,
      String ipv6subnet,
      String name,
      String mac,
      boolean internal,
      boolean virtual,
      String operstate,
      String type,
      String duplex,
      int mtu,
      int speed,
      boolean dhcp,
      String dnsSuffix,
      String ieee8021xAuth,
      String ieee8021xState,
      int carrierChanges) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record MemLayout(
      String bank,
      String type,
      String size,
      String formFactor,
      String manufacturer,
      String serial,
      String partNum,
      String clockSpeed,
      String voltageConfigured,
      String voltageMin,
      String voltageMax,
      boolean ecc) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record DiskLayout(
      String device,
      String type,
      String name,
      String vendor,
      long size,
      String serialNum,
      String bytesPerSector,
      String totalCylinders,
      String totalHeads,
      String totalSectors,
      String totalTracks,
      String tracksPerCylinder,
      String sectorsPerTrack,
      String firmwareRevision,
      String interfaceType,
      String smartStatus,
      String temperature,
      String serialNumber) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Time(
      long current,
      long uptime,
      String timezone,
      String timezoneName) {
  }

  public static class GraphicsDeserializer extends JsonDeserializer<List<Graphics>> {
    @Override
    public List<Graphics> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.currentToken() == JsonToken.START_OBJECT) {
        Graphics graphics = p.readValueAs(Graphics.class);
        return Collections.singletonList(graphics);
      }
      return p.readValueAs(new TypeReference<List<Graphics>>() {
      });
    }
  }

  public HostDao toDao() {
    HostDao dao = new HostDao();
    dao.setId(this.id);
    dao.setVersion(this.hostVersion);

    // System
    SystemDao sysDao = new SystemDao();
    sysDao.setManufacturer(this.system.manufacturer());
    sysDao.setModel(this.system.model());
    sysDao.setVersion(this.system.version());
    sysDao.setSerial(this.system.serial());
    sysDao.setUuid(this.system.uuid());
    sysDao.setSku(this.system.sku());
    sysDao.setVirtual(this.system.virtual());
    sysDao.setType(this.system.type());
    dao.setSystem(sysDao);

    // Bios
    BiosDao biosDao = new BiosDao();
    biosDao.setVendor(this.bios.vendor());
    biosDao.setVersion(this.bios.version());
    biosDao.setReleaseDate(this.bios.releaseDate());
    biosDao.setRevision(this.bios.revision());
    dao.setBios(biosDao);

    // Baseboard
    BaseboardDao baseboardDao = new BaseboardDao();
    baseboardDao.setManufacturer(this.baseboard.manufacturer());
    baseboardDao.setModel(this.baseboard.model());
    baseboardDao.setVersion(this.baseboard.version());
    baseboardDao.setSerial(this.baseboard.serial());
    baseboardDao.setAssetTag(this.baseboard.assetTag());
    baseboardDao.setMemMax(this.baseboard.memMax());
    baseboardDao.setMemSlots(this.baseboard.memSlots());
    dao.setBaseboard(baseboardDao);

    // Graphics
    List<com.technohouser.entities.GraphicsDao> graphicsDaoList = this.graphics.stream()
        .map(g -> {
          com.technohouser.entities.GraphicsDao graphicsDao = new com.technohouser.entities.GraphicsDao();
          graphicsDao.setId(g.getId());

          // Controllers
          List<com.technohouser.entities.ControllerDao> controllerDaoList = g.getControllers().stream()
              .map(c -> {
                com.technohouser.entities.ControllerDao controllerDao = new com.technohouser.entities.ControllerDao();
                controllerDao.setModel(c.getModel());
                controllerDao.setVendor(c.getVendor());
                controllerDao.setBus(c.getBus());
                controllerDao.setVram(c.getVram());
                controllerDao.setVramDynamic(c.isVramDynamic());
                controllerDao.setDeviceId(c.getDeviceId());
                controllerDao.setVendorId(c.getVendorId());
                controllerDao.setExternal(c.isExternal());
                controllerDao.setCores(c.getCores());
                controllerDao.setMetalVersion(c.getMetalVersion());
                return controllerDao;
              })
              .collect(Collectors.toList());
          graphicsDao.setControllers(controllerDaoList);

          // Displays
          List<com.technohouser.entities.DisplayDao> displayDaoList = g.getDisplays().stream()
              .map(d -> {
                com.technohouser.entities.DisplayDao displayDao = new com.technohouser.entities.DisplayDao();
                displayDao.setVendor(d.getVendor());
                displayDao.setModel(d.getModel());
                displayDao.setDeviceName(d.getDeviceName());
                displayDao.setSerial(d.getSerial());
                displayDao.setConnection(d.getConnection());
                displayDao.setPixelDepth(d.getPixelDepth());
                displayDao.setResolutionX(d.getResolutionX());
                displayDao.setResolutionY(d.getResolutionY());
                displayDao.setSizeX(d.getSizeX());
                displayDao.setSizeY(d.getSizeY());
                displayDao.setCurrentResX(d.getCurrentResX());
                displayDao.setCurrentResY(d.getCurrentResY());
                displayDao.setRefreshRate(d.getRefreshRate());
                displayDao.setDisplayId(d.getDisplayId());
                displayDao.setVendorId(d.getVendorId());
                displayDao.setProductionYear(d.getProductionYear());
                displayDao.setMain(d.isMain());
                displayDao.setPositionX(d.getPositionX());
                displayDao.setPositionY(d.getPositionY());
                displayDao.setCurrentRefreshRate(d.getCurrentRefreshRate());
                displayDao.setBuiltin(d.isBuiltin());
                return displayDao;
              })
              .collect(Collectors.toList());
          graphicsDao.setDisplays(displayDaoList);

          return graphicsDao;
        })
        .collect(Collectors.toList());
    dao.setGraphics(graphicsDaoList);

    // Chassis
    ChassisDao chassisDao = new ChassisDao();
    chassisDao.setManufacturer(this.chassis.manufacturer());
    chassisDao.setModel(this.chassis.model());
    chassisDao.setType(this.chassis.type());
    chassisDao.setVersion(this.chassis.version());
    chassisDao.setSerial(this.chassis.serial());
    chassisDao.setAssetTag(this.chassis.assetTag());
    chassisDao.setSku(this.chassis.sku());
    dao.setChassis(chassisDao);

    // OS
    OSDao osDao = new OSDao();
    osDao.setPlatform(this.os.platform());
    osDao.setDistro(this.os.distro());
    osDao.setRelease(this.os.release());
    osDao.setCodename(this.os.codename());
    osDao.setKernel(this.os.kernel());
    osDao.setArch(this.os.arch());
    osDao.setHostname(this.os.hostname());
    osDao.setFqdn(this.os.fqdn());
    osDao.setCodepage(this.os.codepage());
    osDao.setLogofile(this.os.logofile());
    osDao.setSerial(this.os.serial());
    osDao.setBuild(this.os.build());
    osDao.setServicepack(this.os.servicepack());
    osDao.setUefi(this.os.uefi());
    dao.setOs(osDao);

    // UUID
    UUIDDao uuidDao = new UUIDDao();
    uuidDao.setOs(this.uuid.os());
    uuidDao.setHardware(this.uuid.hardware());
    uuidDao.setMacs(this.uuid.macs());
    dao.setUuid(uuidDao);

    // Versions
    VersionsDao versionsDao = new VersionsDao();
    versionsDao.setKernel(this.versions.kernel());
    versionsDao.setOpenssl(this.versions.openssl());
    versionsDao.setSystemOpenssl(this.versions.systemOpenssl());
    versionsDao.setSystemOpensslLib(this.versions.systemOpensslLib());
    versionsDao.setPython(this.versions.python());
    versionsDao.setSystemPython(this.versions.systemPython());
    versionsDao.setSystemPythonLib(this.versions.systemPythonLib());
    versionsDao.setPip(this.versions.pip());
    versionsDao.setPip3(this.versions.pip3());
    versionsDao.setSystemPip(this.versions.systemPip());
    versionsDao.setSystemPip3(this.versions.systemPip3());
    versionsDao.setVirtualenv(this.versions.virtualenv());
    versionsDao.setSystemVirtualenv(this.versions.systemVirtualenv());
    versionsDao.setSystemSitePackages(this.versions.systemSitePackages());
    versionsDao.setSystemSitePackages3(this.versions.systemSitePackages3());
    versionsDao.setSystemSitePackagesPip(this.versions.systemSitePackagesPip());
    versionsDao.setSystemSitePackagesPip3(this.versions.systemSitePackagesPip3());
    versionsDao.setDistro(this.versions.distro());
    versionsDao.setDistroCPE(this.versions.distroCPE());
    dao.setVersions(versionsDao);

    // CPU
    CPUDao cpuDao = new CPUDao();
    cpuDao.setVendor(this.cpu.vendor());
    cpuDao.setFamily(this.cpu.family());
    cpuDao.setModel(this.cpu.model());
    cpuDao.setStepping(this.cpu.stepping());
    cpuDao.setRevision(this.cpu.revision());
    cpuDao.setVoltage(this.cpu.voltage());
    cpuDao.setSpeed(this.cpu.speed());
    cpuDao.setSpeedMin(this.cpu.speedMin());
    cpuDao.setSpeedMax(this.cpu.speedMax());
    cpuDao.setGovernor(this.cpu.governor());
    cpuDao.setCores(this.cpu.cores());
    cpuDao.setPhysicalCores(this.cpu.physicalCores());
    cpuDao.setProcessors(this.cpu.processors());
    cpuDao.setSocket(this.cpu.socket());
    cpuDao.setCacheL1d(this.cpu.cacheL1d());
    cpuDao.setCacheL1i(this.cpu.cacheL1i());
    cpuDao.setCacheL2(this.cpu.cacheL2());
    cpuDao.setCacheL3(this.cpu.cacheL3());
    dao.setCpu(cpuDao);

    // Net
    List<NetDao> netDaoList = this.net.stream()
        .map(n -> {
          NetDao netDao = new NetDao();
          netDao.setIface(n.iface());
          netDao.setIfaceName(n.ifaceName());
          netDao.setDefault(n.isDefault());
          netDao.setIpv4(n.ipv4());
          netDao.setIpv4subnet(n.ipv4subnet());
          netDao.setIpv6(n.ipv6());
          netDao.setIpv6subnet(n.ipv6subnet());
          netDao.setName(n.name());
          netDao.setMac(n.mac());
          netDao.setInternal(n.internal());
          netDao.setVirtual(n.virtual());
          netDao.setOperstate(n.operstate());
          netDao.setType(n.type());
          netDao.setDuplex(n.duplex());
          netDao.setMtu(n.mtu());
          netDao.setSpeed(n.speed());
          netDao.setDhcp(n.dhcp());
          netDao.setDnsSuffix(n.dnsSuffix());
          netDao.setIeee8021xAuth(n.ieee8021xAuth());
          netDao.setIeee8021xState(n.ieee8021xState());
          netDao.setCarrierChanges(n.carrierChanges());
          return netDao;
        })
        .collect(Collectors.toList());
    dao.setNet(netDaoList);

    // MemLayout
    List<MemLayoutDao> memLayoutDaoList = this.memLayout.stream()
        .map(m -> {
          MemLayoutDao memLayoutDao = new MemLayoutDao();
          memLayoutDao.setBank(m.bank());
          memLayoutDao.setType(m.type());
          memLayoutDao.setSize(m.size());
          memLayoutDao.setFormFactor(m.formFactor());
          memLayoutDao.setManufacturer(m.manufacturer());
          memLayoutDao.setSerial(m.serial());
          memLayoutDao.setPartNum(m.partNum());
          memLayoutDao.setClockSpeed(m.clockSpeed());
          memLayoutDao.setVoltageConfigured(m.voltageConfigured());
          memLayoutDao.setVoltageMin(m.voltageMin());
          memLayoutDao.setVoltageMax(m.voltageMax());
          memLayoutDao.setEcc(m.ecc());
          return memLayoutDao;
        })
        .collect(Collectors.toList());
    dao.setMemLayout(memLayoutDaoList);

    // DiskLayout
    List<DiskLayoutDao> diskLayoutDaoList = this.diskLayout.stream()
        .map(d -> {
          DiskLayoutDao diskLayoutDao = new DiskLayoutDao();
          diskLayoutDao.setDevice(d.device());
          diskLayoutDao.setType(d.type());
          diskLayoutDao.setName(d.name());
          diskLayoutDao.setVendor(d.vendor());
          diskLayoutDao.setSize(d.size());
          diskLayoutDao.setSerialNum(d.serialNum());
          diskLayoutDao.setBytesPerSector(d.bytesPerSector());
          diskLayoutDao.setTotalCylinders(d.totalCylinders());
          diskLayoutDao.setTotalHeads(d.totalHeads());
          diskLayoutDao.setTotalSectors(d.totalSectors());
          diskLayoutDao.setTotalTracks(d.totalTracks());
          diskLayoutDao.setTracksPerCylinder(d.tracksPerCylinder());
          diskLayoutDao.setSectorsPerTrack(d.sectorsPerTrack());
          diskLayoutDao.setFirmwareRevision(d.firmwareRevision());
          diskLayoutDao.setInterfaceType(d.interfaceType());
          diskLayoutDao.setSmartStatus(d.smartStatus());
          diskLayoutDao.setTemperature(d.temperature());
          diskLayoutDao.setSerialNumber(d.serialNumber());
          return diskLayoutDao;
        })
        .collect(Collectors.toList());
    dao.setDiskLayout(diskLayoutDaoList);

    // Time
    TimeDao timeDao = new TimeDao();
    timeDao.setCurrent(this.time.current());
    timeDao.setUptime(this.time.uptime());
    timeDao.setTimezone(this.time.timezone());
    timeDao.setTimezoneName(this.time.timezoneName());
    dao.setTime(timeDao);

    return dao;
  }
}
