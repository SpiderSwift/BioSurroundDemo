package by.citech.biosurrounddemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

object BleDeviceService {

    private val _data = MutableLiveData<List<BleDevice>>()
    val data : LiveData<List<BleDevice>> = _data

    private val list = mutableListOf<BleDevice>()

    fun connect(device: BleDevice) {
        list[list.indexOf(device)].state = BleState.CONNECTING
        _data.value = list

        CoroutineScope(Dispatchers.IO).launch {
            delay(1500)
            list[list.indexOf(device)].state = BleState.CONNECTED
            withContext(Dispatchers.Main) {
                _data.value = list
            }
        }

    }


    fun disconnect(device: BleDevice) {
        list[list.indexOf(device)].state = BleState.NOT_CONNECTED
        _data.value = list
    }

    fun scan() {
        list.clear()
        list.addAll(
            listOf(
                BleDevice("DEVICE 1", BleState.NOT_CONNECTED),
                BleDevice("DEVICE 2", BleState.NOT_CONNECTED),
                BleDevice("DEVICE 3", BleState.NOT_CONNECTED)
            )
        )
        _data.value = list
    }

}