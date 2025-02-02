package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "uuids")
@Data
@NoArgsConstructor
public class UUIDDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String os;
  private String hardware;

  @ElementCollection
  @CollectionTable(name = "uuid_macs", joinColumns = @JoinColumn(name = "uuid_id"))
  @Column(name = "mac")
  private List<String> macs;
}
