package com.openautolink.app.cluster

import org.junit.Assert.*
import org.junit.Test

class GmVehicleDetectionTest {
    @Test fun blazerAndEquinoxEvAreGm() {
        assertTrue(ClusterOutputPolicy.isGmVehicle("aegean_orange"))
        assertTrue(ClusterOutputPolicy.isGmVehicle("burmese_orange"))
    }

    @Test fun otherProductsAreNotGm() {
        for (product in listOf(null, "", "sdk_car_x86_64", "Burmese_Orange", "burmese_orange ", "orange"))
            assertFalse("matched $product", ClusterOutputPolicy.isGmVehicle(product))
    }

    @Test fun equinoxIsNotGivenTheBlazerGeometryProfile() {
        assertEquals("aegean_orange", ClusterDisplayProfiles.AEGEAN_ORANGE_239.vehicle.product)
        assertFalse("burmese_orange" == ClusterDisplayProfiles.AEGEAN_ORANGE_239.vehicle.product)
    }
}
