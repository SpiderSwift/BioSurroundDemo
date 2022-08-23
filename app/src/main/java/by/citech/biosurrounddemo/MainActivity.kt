package by.citech.biosurrounddemo

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), HealthListener {

    private val healthService: HealthService = HealthServiceImpl

    private var initialized = false

    private var mpLowState: MediaPlayer? = null
    private var mpMediumState: MediaPlayer? = null
    private var mpHighState: MediaPlayer? = null
    private var viewLowState: View? = null
    private var viewMediumState: View? = null
    private var viewHighState: View? = null
    private var vvLowState: VideoView? = null
    private var vvMediumState: VideoView? = null
    private var vvHighState: VideoView? = null
    private var tvBpm: TextView? = null
    private var graph: GraphView? = null
    private val series: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf())
    private var graphIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        graph = findViewById(R.id.graph)
        graph?.viewport?.isXAxisBoundsManual = true
        graph?.viewport?.isYAxisBoundsManual = true
        graph?.viewport?.setMinX(0.0)
        graph?.viewport?.setMaxX(40.0)
        graph?.viewport?.setMinY(40.0)
        graph?.viewport?.setMaxY(160.0)
        graph?.gridLabelRenderer?.gridStyle = GridLabelRenderer.GridStyle.NONE
        graph?.gridLabelRenderer?.isHorizontalLabelsVisible = false
        graph?.gridLabelRenderer?.isVerticalLabelsVisible = false

        graph?.addSeries(series)

        healthService.init()
        healthService.registerProvider(MockHealthProvider())
        healthService.registerListener(this)

        viewLowState = findViewById(R.id.viewLowState)
        viewMediumState = findViewById(R.id.viewMediumState)
        viewHighState = findViewById(R.id.viewHighState)

        vvLowState = findViewById<View>(R.id.vvLowState) as VideoView
        vvMediumState = findViewById<View>(R.id.vvMediumState) as VideoView
        vvHighState = findViewById<View>(R.id.vvHighState) as VideoView

        tvBpm = findViewById<View>(R.id.tvBpm) as TextView

        vvLowState?.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.video_low_state}"))
        vvMediumState?.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.video_medium_state}"))
        vvHighState?.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.video_high_state}"))

        vvLowState?.start()
        vvMediumState?.start()
        vvHighState?.start()

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initialized = true
            mpLowState = MediaPlayer.create(baseContext, R.raw.audio_low_state)
            mpMediumState = MediaPlayer.create(baseContext, R.raw.audio_medium_state)
            mpHighState = MediaPlayer.create(baseContext, R.raw.audio_high_state)

            mpLowState?.start()
            mpMediumState?.start()
            mpMediumState?.setVolume(0f, 0f)
            mpHighState?.start()
            mpHighState?.setVolume(0f, 0f)
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            val time = vvLowState?.currentPosition ?: 0
            vvMediumState?.seekTo(time)
            mpLowState?.setVolume(1f, 1f)
            mpLowState?.seekTo(vvLowState?.currentPosition ?: 0)
            mpMediumState?.seekTo(vvLowState?.currentPosition ?: 0)
            mpMediumState?.setVolume(0f, 0f)
            mpHighState?.seekTo(vvLowState?.currentPosition ?: 0)
            mpHighState?.setVolume(0f, 0f)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            mpLowState?.release()
            mpMediumState?.release()
            mpHighState?.release()
            mpLowState = null
            mpMediumState = null
            mpHighState = null
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        if (initialized) {
            initialized = false
            mpLowState?.release()
            mpMediumState?.release()
            mpHighState?.release()
            mpLowState = null
            mpMediumState = null
            mpHighState = null
            finish()
        }
        super.onPause()
    }

    override fun onHealthChara(chara: HealthChara) {
        CoroutineScope(Dispatchers.Main).launch {
            series.appendData(DataPoint(graphIndex++.toDouble(), chara.bpm?.toDouble() ?: 0.0), true, 40)
            tvBpm?.text = chara.bpm.toString()
            when (chara.toHealthState()) {
                HealthState.BPM_LOW -> {
                    viewLowState?.visibility = View.VISIBLE
                    viewMediumState?.visibility = View.INVISIBLE
                    viewHighState?.visibility = View.INVISIBLE
                    mpLowState?.setVolume(1f,1f)
                    mpMediumState?.setVolume(0f,0f)
                    mpHighState?.setVolume(0f,0f)
                }
                HealthState.BPM_MEDIUM -> {
                    viewMediumState?.visibility = View.VISIBLE
                    viewLowState?.visibility = View.INVISIBLE
                    viewHighState?.visibility = View.INVISIBLE
                    mpLowState?.setVolume(0f,0f)
                    mpMediumState?.setVolume(1f,1f)
                    mpHighState?.setVolume(0f,0f)
                }
                HealthState.BPM_HIGH -> {
                    viewHighState?.visibility = View.VISIBLE
                    viewLowState?.visibility = View.INVISIBLE
                    viewMediumState?.visibility = View.INVISIBLE
                    mpLowState?.setVolume(0f,0f)
                    mpMediumState?.setVolume(0f,0f)
                    mpHighState?.setVolume(1f,1f)
                }
            }
        }
    }

}