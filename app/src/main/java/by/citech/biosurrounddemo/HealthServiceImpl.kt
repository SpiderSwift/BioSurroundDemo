package by.citech.biosurrounddemo

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

object HealthServiceImpl : HealthService {

    private val healthFlow = flow {
        while (true) {
            delay(300)
            val charaList = providers.map { it.get() }
            emit(charaList.first())
        }
    }

    private val providers = mutableListOf<HealthProvider>()
    private val jobMap = mutableMapOf<HealthListener, Job>()


    override fun init() {

    }

    override fun destroy() {
        jobMap.forEach { (_, job) ->
            job.cancel()
        }
    }

    override fun registerProvider(provider: HealthProvider) {
        providers.add(provider)
    }

    override fun unregisterProvider(provider: HealthProvider) {
        providers.remove(provider)
    }

    override fun registerListener(listener: HealthListener) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            healthFlow.collect {
                listener.onHealthChara(it)
            }
        }
        jobMap[listener] = job
    }

    override fun unregisterListener(listener: HealthListener) {
        jobMap[listener]?.cancel()
    }
}