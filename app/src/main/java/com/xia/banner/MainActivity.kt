package com.xia.banner

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xia.banner.option.img.BannerCreator
import com.xia.banner.option.text.NoticeCreator
import com.xia.banner.utils.BlurBitmapUtils
import com.xia.banner.utils.ViewSwitchUtils
import com.xia.flybanner.FlyBanner
import com.xia.flybanner.listener.OnItemClickListener
import com.xia.flybanner.listener.OnPageChangeListener
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var flyBanner: FlyBanner<Any>
    private lateinit var noticeView: FlyBanner<Any>
    private lateinit var loopControlBtn: AppCompatButton
    private lateinit var setCurrentItemPosBtn: AppCompatButton
    private lateinit var refreshDataBtn: AppCompatButton
    private lateinit var indicatorOrientationBtn: AppCompatButton
    private lateinit var loopModeBtn: AppCompatButton
    private lateinit var scaleCardViewBtn: AppCompatButton
    private lateinit var loopStatusTv: AppCompatTextView
    private lateinit var currentItemPosTv: AppCompatTextView
    private lateinit var dataSizeTv: AppCompatTextView
    private lateinit var indicatorOrientationTv: AppCompatTextView
    private lateinit var loopModeTv: AppCompatTextView
    private lateinit var scaleCardViewTv: AppCompatTextView
    private lateinit var blurIv: AppCompatImageView

    private val localImages = ArrayList<Int>()
    private var isHorizontal = true
    private var isLoopMode: Boolean = false
    private var isScaleCardView: Boolean = false
    private var blurRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initEvent()
        refreshData()
        start()
        notice()
    }

    override fun onResume() {
        super.onResume()
        //开始自动翻页
        flyBanner.startTurning()
    }

    override fun onPause() {
        super.onPause()
        //停止翻页
        flyBanner.stopTurning()
    }

    private fun initView() {
        flyBanner = findViewById(R.id.main_banner)
        noticeView = findViewById(R.id.main_notice)
        loopControlBtn = findViewById(R.id.main_loop_control_btn)
        loopStatusTv = findViewById(R.id.main_loop_status_tv)
        setCurrentItemPosBtn = findViewById(R.id.main_set_current_item_pos_btn)
        currentItemPosTv = findViewById(R.id.main_current_item_position_tv)
        refreshDataBtn = findViewById(R.id.main_refresh_data_btn)
        dataSizeTv = findViewById(R.id.main_data_size_tv)
        indicatorOrientationBtn = findViewById(R.id.main_indicator_orientation_btn)
        indicatorOrientationTv = findViewById(R.id.main_indicator_orientation_tv)
        blurIv = findViewById(R.id.main_blur_view)
        loopModeBtn = findViewById(R.id.main_loop_mode_btn)
        loopModeTv = findViewById(R.id.main_loop_mode_tv)
        scaleCardViewBtn = findViewById(R.id.main_scale_card_view_btn)
        scaleCardViewTv = findViewById(R.id.main_scale_card_view_tv)
    }

    private fun initEvent() {
        loopControlBtn.setOnClickListener(this)
        setCurrentItemPosBtn.setOnClickListener(this)
        refreshDataBtn.setOnClickListener(this)
        indicatorOrientationBtn.setOnClickListener(this)
        loopModeBtn.setOnClickListener(this)
        scaleCardViewBtn.setOnClickListener(this)
    }

    private fun refreshData() {
        localImages.clear()
        val min = 1
        val max = 7
        val randomNum = Random().nextInt(max - min) + min

        //本地图片集合
        for (position in 0 until randomNum) {
            val imgResId = getResId("ic_test_$position", R.drawable::class.java)
            if (imgResId != -1) {
                localImages.add(imgResId)
            }
        }
    }

    private fun start() {
        BannerCreator.setDefault(flyBanner, localImages, isHorizontal,
                isLoopMode, isScaleCardView, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                showToast("onItemClick: $position")
            }
        }, object : OnPageChangeListener {
            override fun onPageSelected(index: Int, isLastPage: Boolean) {
                notifyBackgroundChange(index)
                showToast("onPageSelected: " + index + "   currentItem:" + flyBanner.getCurrentItem())
                setPosition(index, isLastPage)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }
        })

        notifyBackgroundChange(0)
        setLoopStatus()
        setPosition(0, localImages.size <= 1)
        setDataSize(localImages.size)
        setIndicatorOrientation()
        setGuidePageStatus()
        setScaleCardViewStatus()
    }

    private fun notice() {
        val list = ArrayList<String>()
        list.add("大促销下单拆福袋，亿万新年红包随便拿")
        list.add("家电五折团，抢十亿无门槛现金红包")
        list.add("星球大战剃须刀首发送200元代金券")
        NoticeCreator.setDefault(noticeView, list, false, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                showToast(list[position])
            }
        }, null)
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     */
    private fun getResId(variableName: String, cls: Class<*>): Int {
        return try {
            val idField = cls.getDeclaredField(variableName)
            idField.getInt(idField)
        } catch (ignore: Exception) {
            -1
        }
    }

    private fun setLoopStatus() {
        val text = "是否自动翻页：" + flyBanner.isAutoPlay()
        loopStatusTv.text = text
    }

    private fun setPosition(index: Int, isLastPage: Boolean) {
        val text = "position：$index，最后一页：$isLastPage"
        currentItemPosTv.text = text
    }

    private fun setDataSize(dataSize: Int) {
        val text = "总数据条数：$dataSize"
        dataSizeTv.text = text
    }

    private fun setIndicatorOrientation() {
        val text = "翻页方向：" + if (isHorizontal) "横向" else "竖向"
        indicatorOrientationTv.text = text
    }

    private fun setGuidePageStatus() {
        val text = "是否无限循环：$isLoopMode"
        loopModeTv.text = text
    }

    private fun setScaleCardViewStatus() {
        val text = "是否为卡片式缩放视图：$isScaleCardView"
        scaleCardViewTv.text = text
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun notifyBackgroundChange(position: Int) {
        val resId = localImages[position]
        blurIv.removeCallbacks(blurRunnable)
        blurRunnable = Runnable {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            val newBitmap = BlurBitmapUtils.getBlurBitmap(this@MainActivity, bitmap, 15)
            ViewSwitchUtils.startSwitchBackgroundAnim(blurIv, newBitmap)
        }
        blurIv.postDelayed(blurRunnable, 500)
    }

    override fun onClick(v: View) {
        if (v === loopControlBtn) {
            val isCanLoop = flyBanner.isAutoPlay()
            flyBanner.setAutoPlay(!isCanLoop)
            setLoopStatus()
            return
        }
        if (v === setCurrentItemPosBtn) {
            val min = 0
            val max = localImages.size
            val randomNum = Random().nextInt(max - min) + min
            flyBanner.setCurrentItem(randomNum)
            return
        }
        if (v === refreshDataBtn) {
            refreshData()
            start()
            showToast("数据已刷新")
            return
        }
        if (v === indicatorOrientationBtn) {
            isHorizontal = !isHorizontal
            start()
            return
        }
        if (v === loopModeBtn) {
            isLoopMode = !isLoopMode
            start()
            return
        }
        if (v === scaleCardViewBtn) {
            isScaleCardView = !isScaleCardView
            start()
        }
    }
}
