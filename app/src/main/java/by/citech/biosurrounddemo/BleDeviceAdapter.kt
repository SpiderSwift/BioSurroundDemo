package by.citech.biosurrounddemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView

class BleDeviceAdapter(private val devices: List<BleDevice>): RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView
        val tvStatus: TextView
        val ivCheck: ImageView
        val clDevice: ConstraintLayout
        val ivBluetooth: ImageView

        init {
            tvName = view.findViewById(R.id.tvName)
            tvStatus = view.findViewById(R.id.tvStatus)
            ivCheck = view.findViewById(R.id.ivCheck)
            clDevice = view.findViewById(R.id.clDevice)
            ivBluetooth = view.findViewById(R.id.ivBluetooth)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ble_device_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val device = devices[position]
        viewHolder.tvName.text = device.name

        when (device.state) {
            BleState.NOT_CONNECTED -> {
                viewHolder.tvStatus.visibility = View.VISIBLE
                viewHolder.tvStatus.text = "Not connected"
                viewHolder.ivCheck.visibility = View.GONE
            }
            BleState.CONNECTING -> {
                viewHolder.tvStatus.visibility = View.VISIBLE
                viewHolder.tvStatus.text = "Connecting..."
                viewHolder.ivCheck.visibility = View.GONE
            }
            BleState.CONNECTED -> {
                viewHolder.tvStatus.visibility = View.VISIBLE
                viewHolder.tvStatus.text = "Device connected"
                viewHolder.ivCheck.visibility = View.VISIBLE
            }
        }

        viewHolder.clDevice.setOnClickListener {
            when (device.state) {
                BleState.NOT_CONNECTED -> {
                    BleDeviceService.connect(device)
                }
                BleState.CONNECTING -> {
                    BleDeviceService.disconnect(device)
                }
                BleState.CONNECTED -> {
                    BleDeviceService.disconnect(device)
                }
            }
        }

    }

    override fun getItemCount() = devices.size

}