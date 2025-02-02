package com.technohouser.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "versions")
@Data
@NoArgsConstructor
public class VersionsDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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
