package com.technohouser.entities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.Inet4Address;
import java.net.UnknownHostException;

@Converter(autoApply = true)
public class Inet4AddressConverter implements AttributeConverter<Inet4Address, String> {

  @Override
  public String convertToDatabaseColumn(Inet4Address address) {
    return address != null ? address.getHostAddress() : null;
  }

  @Override
  public Inet4Address convertToEntityAttribute(String dbData) {
    try {
      return dbData != null ? (Inet4Address) Inet4Address.getByName(dbData) : null;
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Invalid IPv4 address: " + dbData, e);
    }
  }
}
