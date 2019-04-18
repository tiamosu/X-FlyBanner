package com.xia.banner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xia.banner.option.img.BannerCreator;
import com.xia.banner.option.text.NoticeCreator;
import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FlyBanner mFlyBanner, mNoticeView, mNoticeView1;
    private AppCompatButton mLoopControlBtn, mSetCurrentItemPosBtn,
            mRefreshDataBtn, mIndicatorOrientationBtn;
    private AppCompatTextView mLoopStatusTv, mCurrentItemPosTv,
            mDataSizeTv, mIndicatorOrientationTv;

    private final ArrayList<Integer> mLocalImages = new ArrayList<>();
    private boolean mIsHorizontal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
        refreshData();
        start();
        notice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        if (mFlyBanner != null) {
            mFlyBanner.startTurning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        if (mFlyBanner != null) {
            mFlyBanner.stopTurning();
        }
    }

    private void initView() {
        mFlyBanner = findViewById(R.id.main_banner);
        mNoticeView = findViewById(R.id.main_notice);
        mNoticeView1 = findViewById(R.id.main_notice1);
        mLoopControlBtn = findViewById(R.id.main_loop_control_btn);
        mLoopStatusTv = findViewById(R.id.main_loop_status_tv);
        mSetCurrentItemPosBtn = findViewById(R.id.main_set_current_item_pos_btn);
        mCurrentItemPosTv = findViewById(R.id.main_current_item_position_tv);
        mRefreshDataBtn = findViewById(R.id.main_refresh_data_btn);
        mDataSizeTv = findViewById(R.id.main_data_size_tv);
        mIndicatorOrientationBtn = findViewById(R.id.main_indicator_orientation_btn);
        mIndicatorOrientationTv = findViewById(R.id.main_indicator_orientation_tv);
    }

    private void initEvent() {
        mLoopControlBtn.setOnClickListener(this);
        mSetCurrentItemPosBtn.setOnClickListener(this);
        mRefreshDataBtn.setOnClickListener(this);
        mIndicatorOrientationBtn.setOnClickListener(this);
    }

    private void refreshData() {
        mLocalImages.clear();
        final int min = 1;
        final int max = 7;
        final int randomNum = new Random().nextInt(max - min) + min;

        //本地图片集合
        for (int position = 0; position < randomNum; position++) {
            final int imgResId = getResId("ic_test_" + position, R.drawable.class);
            if (imgResId != -1) {
                mLocalImages.add(imgResId);
            }
        }
    }

    private void start() {
        BannerCreator.setDefault(mFlyBanner, mLocalImages, mIsHorizontal, position ->
                        Toast.makeText(MainActivity.this, "onItemClick: " + position, Toast.LENGTH_SHORT).show(),
                new OnPageChangeListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    }

                    @Override
                    public void onPageSelected(int index, boolean isLastPage) {
                        Toast.makeText(MainActivity.this, "onPageSelected: " + index, Toast.LENGTH_SHORT).show();
                        setCurrentItemPosition(index, isLastPage);
                    }
                });

        setLoopStatus();
        setCurrentItemPosition(0, mLocalImages.size() <= 1);
        setDataSize(mLocalImages.size());
        setIndicatorOrientation();
    }

    private void notice() {
        final List<String> list = new ArrayList<>();
        list.add("大促销下单拆福袋，亿万新年红包随便拿");
        list.add("家电五折团，抢十亿无门槛现金红包");
        list.add("星球大战剃须刀首发送200元代金券");
        NoticeCreator.setDefault(mNoticeView, list, false, position -> {
            final String text = list.get(position);
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }, null);

        NoticeCreator.setDefault(mNoticeView1, list, true, position -> {
            final String text = list.get(position);
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }, null);
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     */
    @SuppressWarnings("SameParameterValue")
    private int getResId(String variableName, Class<?> cls) {
        try {
            final Field idField = cls.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setLoopStatus() {
        final String text = "是否自动翻页：" + (mFlyBanner.isCanLoop() ? "是" : "否");
        mLoopStatusTv.setText(text);
    }

    private void setCurrentItemPosition(final int index, final boolean isLastPage) {
        final String text = "position：" + index + "，最后一页：" + (isLastPage ? "true" : "false");
        mCurrentItemPosTv.setText(text);
    }

    private void setDataSize(final int dataSize) {
        final String text = "总数据条数：" + dataSize;
        mDataSizeTv.setText(text);
    }

    private void setIndicatorOrientation() {
        final String text = "翻页方向：" + (mIsHorizontal ? "横向" : "竖向");
        mIndicatorOrientationTv.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v == mLoopControlBtn) {
            final boolean isCanLoop = mFlyBanner.isCanLoop();
            mFlyBanner.setCanLoop(!isCanLoop);
            setLoopStatus();
            return;
        }
        if (v == mSetCurrentItemPosBtn) {
            final int min = 0;
            final int max = mLocalImages.size();
            final int randomNum = new Random().nextInt(max - min) + min;
            mFlyBanner.setCurrentItem(randomNum);
            return;
        }
        if (v == mRefreshDataBtn) {
            refreshData();
            start();
            Toast.makeText(this, "数据已刷新", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v == mIndicatorOrientationBtn) {
            mIsHorizontal = !mIsHorizontal;
            start();
        }
    }
}
