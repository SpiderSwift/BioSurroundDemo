package by.citech.biosurrounddemo

data class HealthChara(
    val bpm: Int?
) {
    fun toHealthState() = when (bpm) {
        in 60..80 -> HealthState.BPM_LOW
        in 80..100 -> HealthState.BPM_MEDIUM
        in 100..200 -> HealthState.BPM_HIGH
        else -> HealthState.BPM_LOW
    }
}
