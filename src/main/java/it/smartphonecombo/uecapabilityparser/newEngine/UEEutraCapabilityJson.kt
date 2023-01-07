package it.smartphonecombo.uecapabilityparser.newEngine

import it.smartphonecombo.uecapabilityparser.Utility.getObject
import it.smartphonecombo.uecapabilityparser.Utility.getObjectAtPath
import it.smartphonecombo.uecapabilityparser.Utility.repeat
import kotlinx.serialization.json.JsonObject

class UEEutraCapabilityJson(val rootJson: JsonObject) {
    val eutraCapabilityV9e0 = rootJson.getObjectAtPath(
        "nonCriticalExtension.".repeat(2) + "lateNonCriticalExtension" + ".nonCriticalExtension".repeat(3)
    )

    val eutraCapabilityV10i0 = eutraCapabilityV9e0?.getObjectAtPath("nonCriticalExtension".repeat(4, "."))

    val eutraCapabilityV11d0 = eutraCapabilityV10i0?.getObject("nonCriticalExtension")

    val eutraCapabilityV1020 = rootJson.getObjectAtPath("nonCriticalExtension".repeat(3, "."))

    val eutraCapabilityV1060 = eutraCapabilityV1020?.getObject("nonCriticalExtension")

    val eutraCapabilityV1090 = eutraCapabilityV1060?.getObject("nonCriticalExtension")

    val eutraCapabilityV1170 = eutraCapabilityV1090?.getObjectAtPath("nonCriticalExtension".repeat(2, "."))

    val eutraCapabilityV1180 = eutraCapabilityV1170?.getObject("nonCriticalExtension")

    val eutraCapabilityV11a0 = eutraCapabilityV1180?.getObject("nonCriticalExtension")

    val eutraCapabilityV1250 = eutraCapabilityV11a0?.getObject("nonCriticalExtension")

    val eutraCapabilityV1260 = eutraCapabilityV1250?.getObject("nonCriticalExtension")

    val eutraCapabilityV1310 = eutraCapabilityV1260?.getObjectAtPath("nonCriticalExtension".repeat(3, "."))

    val eutraCapabilityV1330 = eutraCapabilityV1310?.getObjectAtPath("nonCriticalExtension".repeat(2, "."))

    val eutraCapabilityV1340 = eutraCapabilityV1330?.getObject("nonCriticalExtension")

    val eutraCapabilityV1350 = eutraCapabilityV1340?.getObject("nonCriticalExtension")

    val eutraCapabilityV1430 = eutraCapabilityV1350?.getObjectAtPath("nonCriticalExtension".repeat(2, "."))

    val eutraCapabilityV1450 = eutraCapabilityV1430?.getObjectAtPath("nonCriticalExtension".repeat(2, "."))

    val eutraCapabilityV1460 = eutraCapabilityV1450?.getObject("nonCriticalExtension")

    val eutraCapabilityV1510 = eutraCapabilityV1460?.getObject("nonCriticalExtension")

    val eutraCapabilityV1530 = eutraCapabilityV1510?.getObjectAtPath("nonCriticalExtension".repeat(2, "."))

    val eutraCapabilityV1540 = eutraCapabilityV1530?.getObject("nonCriticalExtension")

}