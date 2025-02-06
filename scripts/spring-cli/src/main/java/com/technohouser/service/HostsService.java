package com.technohouser.service;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.technohouser.entities.HostDao;
import com.technohouser.model.HostDto;
import com.technohouser.repository.HostRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
@DependsOnDatabaseInitialization
public class HostsService implements InitializingBean {

  private final HostRepository hostRepository;
  private FileConfig fileConfig;

  @Value("${assets.hosts.path}")
  private String staticHostPath;

  public HostsService(HostRepository hostRepository) {
    this.hostRepository = hostRepository;
  }

  public List<HostDto> getAllHosts() {
    return hostRepository.findAll().stream().map(hostDaoToDto).collect(Collectors.toList());
  }

  public static Function<HostDto, HostDao> hostDtoToDao = dto -> dto.toDao();

  public static Function<HostDao, HostDto> hostDaoToDto = HostDto::createFromHostDao;

  public HostDto getHostInfo(String hostname) {
    Optional<HostDao> hostDao = hostRepository.findByHostname(hostname);
    return hostDao.map(HostDto::createFromHostDao).orElse(null);
  }

  public void writeHosts(List<HostDao> hosts) {
    try {
      fileConfig.add("all", hosts);
    } catch (Exception e) {
      log.error("Failed to write hosts", e);
    }
  }

  public void updateHostInfo(HostDto hostDto) {
    try {
      Optional<HostDao> hostDao = hostRepository.findByHostname(hostDto.getOs().hostname());
      if (hostDao.isPresent()) {
        HostDao dao = hostDao.get();
        hostRepository.save(dao);
      } else {
        hostRepository.save(hostDto.toDao());
      }
    } catch (Exception e) {
      log.error("Failed to update host information", e);
    }

  }

  @Override
  public void afterPropertiesSet() throws Exception {
    fileConfig = FileConfig.builder(staticHostPath).autosave().build();
  }
}
