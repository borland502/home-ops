import { $, initShell, info } from "@technohouser/shared";

(async () => {
    await initShell($);
    await info("Hello, World!");
})();
