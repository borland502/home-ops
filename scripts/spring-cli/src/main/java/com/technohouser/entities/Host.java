package com.technohouser.entities;

import jakarta.persistence.Column;

import java.net.Inet4Address;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

import com.technohouser.entities.converters.Inet4AddressConverter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "hosts")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "ip")
  @Convert(converter = Inet4AddressConverter.class)
  private Inet4Address ip;

  @Column(name = "mac")
  private String mac;

  @Column(name = "date")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date date;

  @Column(name = "known")
  @Convert(converter = NumericBooleanConverter.class)
  private Boolean known;

  @Column(name = "now")
  @Convert(converter = NumericBooleanConverter.class)
  private Boolean now;

}
