package com.technohouser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technohouser.entities.HostDao;
import com.technohouser.model.HostDto;
import com.technohouser.repository.HostRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class HostsService {

  private final HostRepository hostRepository;

  public HostsService(HostRepository hostRepository, ObjectMapper objectMapper) {
    this.hostRepository = hostRepository;
  }

  public List<HostDto> getAllHosts() {
    return hostRepository.findAll().stream().map(hostDaoToDto).collect(Collectors.toList());
  }

  public static Function<HostDto, HostDao> hostDtoToDao = dto -> dto.toDao();

  public static Function<HostDao, HostDto> hostDaoToDto = HostDto::createFromHostDao;

  public HostDao updateHostInfo(HostDto hostDto) {
    try {
      Optional<HostDao> hostDao = hostRepository.findByHostname(hostDto.getOs().hostname());
      if (hostDao.isPresent()) {
        HostDao dao = hostDao.get();
        return hostRepository.save(dao);
      } else {
        return hostRepository.save(hostDto.toDao());
      }
    } catch (Exception e) {
      log.error("Failed to update host information", e);
    }

    return null;
  }
}
