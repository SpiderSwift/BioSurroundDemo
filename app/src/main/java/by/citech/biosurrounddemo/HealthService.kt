package by.citech.biosurrounddemo

interface HealthService {

    fun init()
    fun destroy()

    fun registerProvider(provider: HealthProvider)
    fun unregisterProvider(provider: HealthProvider)

    fun registerListener(listener: HealthListener)
    fun unregisterListener(listener: HealthListener)

}