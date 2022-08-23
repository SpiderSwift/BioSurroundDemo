package by.citech.biosurrounddemo

import kotlin.random.Random

class MockHealthProvider: HealthProvider {
    override fun get(): HealthChara {
        return HealthChara(Random.nextInt(60, 140))
    }
}