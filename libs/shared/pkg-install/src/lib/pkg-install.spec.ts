import { pkgInstall } from "./pkg-install.mjs";

describe("pkgInstall", () => {
  it("should work", () => {
    expect(pkgInstall()).toEqual("pkg-install");
  });
});
