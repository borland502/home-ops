// Entry.java
package com.technohouser.entities.secrets.keepassxc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Table(name = "entries")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false)
  private UUID uuid;

}
