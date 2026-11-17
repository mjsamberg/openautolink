package com.openautolink.app.cluster

/** Persisted user intent, not negotiated capability or an output/session lease. */
enum class ClusterDisplayMode { OFF, NATIVE_ICONS, MAP_VIDEO }

data class ClusterOutputSelection(
    val mode: ClusterDisplayMode = ClusterDisplayMode.NATIVE_ICONS,
    // Compatibility shadows: preserve old stored values; active protocol now follows mode.
    val androidAuto: Boolean = true,
    val carPlay: Boolean = true,
    val nativeIconsWithMap: Boolean = false,
    // Compatibility shadow only. Video always uses the admitted direct window.
    val directVideoWindow: Boolean = false,
)

enum class ClusterProjectionProtocol { ANDROID_AUTO, CARPLAY }

/**
 * User intent and GM native/video exclusion gates. True does not establish a host, codec, surface, source lease,
 * negotiated offer or active navigation. Callers must separately enforce those live boundaries.
 */
object ClusterOutputPolicy {
    /**
     * Known GM AAOS head-unit products. Policy membership only: it does not select a geometry profile
     * or calibrated default, which stay tied to [ClusterDisplayProfiles.AEGEAN_ORANGE_239].
     * - `aegean_orange`: Chevrolet Blazer EV (qualified profile).
     * - `burmese_orange`: Chevrolet Equinox EV, where the same native-guidance/video exclusion was
     *   confirmed on the vehicle.
     */
    val GM_PRODUCTS: Set<String> = setOf(ClusterDisplayProfiles.AEGEAN_ORANGE_239.vehicle.product, "burmese_orange")

    /** Known GM product policy, independent of observer readiness or stored user intent. */
    fun isGmVehicle(product: String? = android.os.Build.PRODUCT): Boolean = product in GM_PRODUCTS
    fun shouldOfferMapVideo(selection: ClusterOutputSelection, protocol: ClusterProjectionProtocol): Boolean =
        selection.mode == ClusterDisplayMode.MAP_VIDEO

    /** Map coexistence never synthesizes a route or starts navigation without sourced guidance. */
    fun shouldPublishNativeIcons(
        selection: ClusterOutputSelection,
        protocol: ClusterProjectionProtocol,
        hasSourcedManeuver: Boolean,
    ): Boolean = hasSourcedManeuver &&
        (selection.mode == ClusterDisplayMode.NATIVE_ICONS ||
            (selection.mode == ClusterDisplayMode.MAP_VIDEO && selection.nativeIconsWithMap && !isGmVehicle()))
        // Preserve the saved choice, but GM Video cannot also admit native driver/HUD guidance.
}
