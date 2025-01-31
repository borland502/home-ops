package com.technohouser.entities.secrets.keepassxc;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.Map;

@Table(name = "bootstrap_index")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootstrapIndex {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToMany
  @JoinTable(
    name = "bootstrap_index_groups",
    joinColumns = @JoinColumn(name = "bootstrap_index_id"),
    inverseJoinColumns = @JoinColumn(name = "group_id")
  )
  private Set<Group> groups;

  @ManyToMany
  @JoinTable(
    name = "bootstrap_index_entries",
    joinColumns = @JoinColumn(name = "bootstrap_index_id"),
    inverseJoinColumns = @JoinColumn(name = "entry_id")
  )
  private Set<Entry> entries;

  @ElementCollection
  @CollectionTable(name = "bootstrap_index_binaries", joinColumns = @JoinColumn(name = "bootstrap_index_id"))
  @MapKeyJoinColumn(name = "binary_id")
  @Column(name = "version")
  private Map<Binary, Integer> binaries;

}
