package by.citech.biosurrounddemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random


class StartActivity: AppCompatActivity() {

    private var rvDevices: RecyclerView? = null
    private var btnStart: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        rvDevices = findViewById(R.id.rvDevices)
        btnStart = findViewById(R.id.btnStart)
        rvDevices?.setHasFixedSize(true)

        BleDeviceService.scan()
        BleDeviceService.data.observe(this) {
            rvDevices?.layoutManager = LinearLayoutManager(this)
            rvDevices?.adapter = BleDeviceAdapter(it)
        }

        btnStart?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

}