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
    private lateinit var mFlyBanner: FlyBanner<Any>
    private lateinit var mNoticeView: FlyBanner<Any>
    private lateinit var mLoopControlBtn: AppCompatButton
    private lateinit var mSetCurrentItemPosBtn: AppCompatButton
    private lateinit var mRefreshDataBtn: AppCompatButton
    private lateinit var mIndicatorOrientationBtn: AppCompatButton
    private lateinit var mGuidePageBtn: AppCompatButton
    private lateinit var mScaleCardViewBtn: AppCompatButton
    private lateinit var mLoopStatusTv: AppCompatTextView
    private lateinit var mCurrentItemPosTv: AppCompatTextView
    private lateinit var mDataSizeTv: AppCompatTextView
    private lateinit var mIndicatorOrientationTv: AppCompatTextView
    private lateinit var mGuidePageTv: AppCompatTextView
    private lateinit var mScaleCardViewTv: AppCompatTextView
    private lateinit var mBlurIv: AppCompatImageView

    private val mLocalImages = ArrayList<Int>()
    private var mIsHorizontal = true
    private var mIsGuidePage: Boolean = false
    private var mIsScaleCardView: Boolean = false
    private var mBlurRunnable: Runnable? = null

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
        mFlyBanner.startTurning()
    }

    override fun onPause() {
        super.onPause()
        //停止翻页
        mFlyBanner.stopTurning()
    }

    private fun initView() {
        mFlyBanner = findViewById(R.id.main_banner)
        mNoticeView = findViewById(R.id.main_notice)
        mLoopControlBtn = findViewById(R.id.main_loop_control_btn)
        mLoopStatusTv = findViewById(R.id.main_loop_status_tv)
        mSetCurrentItemPosBtn = findViewById(R.id.main_set_current_item_pos_btn)
        mCurrentItemPosTv = findViewById(R.id.main_current_item_position_tv)
        mRefreshDataBtn = findViewById(R.id.main_refresh_data_btn)
        mDataSizeTv = findViewById(R.id.main_data_size_tv)
        mIndicatorOrientationBtn = findViewById(R.id.main_indicator_orientation_btn)
        mIndicatorOrientationTv = findViewById(R.id.main_indicator_orientation_tv)
        mBlurIv = findViewById(R.id.main_blur_view)
        mGuidePageBtn = findViewById(R.id.main_guide_page_btn)
        mGuidePageTv = findViewById(R.id.main_guide_page_tv)
        mScaleCardViewBtn = findViewById(R.id.main_scale_card_view_btn)
        mScaleCardViewTv = findViewById(R.id.main_scale_card_view_tv)
    }

    private fun initEvent() {
        mLoopControlBtn.setOnClickListener(this)
        mSetCurrentItemPosBtn.setOnClickListener(this)
        mRefreshDataBtn.setOnClickListener(this)
        mIndicatorOrientationBtn.setOnClickListener(this)
        mGuidePageBtn.setOnClickListener(this)
        mScaleCardViewBtn.setOnClickListener(this)
    }

    private fun refreshData() {
        mLocalImages.clear()
        val min = 1
        val max = 7
        val randomNum = Random().nextInt(max - min) + min

        //本地图片集合
        for (position in 0 until randomNum) {
            val imgResId = getResId("ic_test_$position", R.drawable::class.java)
            if (imgResId != -1) {
                mLocalImages.add(imgResId)
            }
        }
    }

    private fun start() {
        BannerCreator.setDefault(mFlyBanner, mLocalImages, mIsHorizontal,
                mIsGuidePage, mIsScaleCardView, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                showToast("onItemClick: $position")
            }
        }, object : OnPageChangeListener {
            override fun onPageSelected(index: Int, isLastPage: Boolean) {
                notifyBackgroundChange(index)
                showToast("onPageSelected: " + index + "   currentItem:" + mFlyBanner.getCurrentItem())
                setPosition(index, isLastPage)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }
        })

        notifyBackgroundChange(0)
        setLoopStatus()
        setPosition(0, mLocalImages.size <= 1)
        setDataSize(mLocalImages.size)
        setIndicatorOrientation()
        setGuidePageStatus()
        setScaleCardViewStatus()
    }

    private fun notice() {
        val list = ArrayList<String>()
        list.add("大促销下单拆福袋，亿万新年红包随便拿")
        list.add("家电五折团，抢十亿无门槛现金红包")
        list.add("星球大战剃须刀首发送200元代金券")
        NoticeCreator.setDefault(mNoticeView, list, false, object : OnItemClickListener {
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
        val text = "是否自动翻页：" + mFlyBanner.isCanLoop()
        mLoopStatusTv.text = text
    }

    private fun setPosition(index: Int, isLastPage: Boolean) {
        val text = "position：$index，最后一页：$isLastPage"
        mCurrentItemPosTv.text = text
    }

    private fun setDataSize(dataSize: Int) {
        val text = "总数据条数：$dataSize"
        mDataSizeTv.text = text
    }

    private fun setIndicatorOrientation() {
        val text = "翻页方向：" + if (mIsHorizontal) "横向" else "竖向"
        mIndicatorOrientationTv.text = text
    }

    private fun setGuidePageStatus() {
        val text = "是否为引导页：$mIsGuidePage"
        mGuidePageTv.text = text
    }

    private fun setScaleCardViewStatus() {
        val text = "是否为卡片式缩放视图：$mIsScaleCardView"
        mScaleCardViewTv.text = text
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun notifyBackgroundChange(position: Int) {
        val resId = mLocalImages[position]
        mBlurIv.removeCallbacks(mBlurRunnable)
        mBlurRunnable = Runnable {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            val newBitmap = BlurBitmapUtils.getBlurBitmap(this@MainActivity, bitmap, 15)
            ViewSwitchUtils.startSwitchBackgroundAnim(mBlurIv, newBitmap)
        }
        mBlurIv.postDelayed(mBlurRunnable, 500)
    }

    override fun onClick(v: View) {
        if (v === mLoopControlBtn) {
            val isCanLoop = mFlyBanner.isCanLoop()
            mFlyBanner.setCanLoop(!isCanLoop)
            setLoopStatus()
            return
        }
        if (v === mSetCurrentItemPosBtn) {
            val min = 0
            val max = mLocalImages.size
            val randomNum = Random().nextInt(max - min) + min
            mFlyBanner.setCurrentItem(randomNum)
            return
        }
        if (v === mRefreshDataBtn) {
            refreshData()
            start()
            showToast("数据已刷新")
            return
        }
        if (v === mIndicatorOrientationBtn) {
            mIsHorizontal = !mIsHorizontal
            start()
            return
        }
        if (v === mGuidePageBtn) {
            mIsGuidePage = !mIsGuidePage
            start()
            return
        }
        if (v === mScaleCardViewBtn) {
            mIsScaleCardView = !mIsScaleCardView
            start()
        }
    }
}
