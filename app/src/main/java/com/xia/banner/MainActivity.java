package com.xia.banner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xia.banner.option.BannerCreator;
import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity {
    private final ArrayList<Integer> mLocalImages = new ArrayList<>();

    private FlyBanner mFlyBanner;
    private AppCompatTextView mLoopStatusTv;
    private AppCompatTextView mCurrentItemPosTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loadData();
    }

    private void initView() {
        mFlyBanner = findViewById(R.id.banner);
        mLoopStatusTv = findViewById(R.id.main_loop_status_tv);
        mCurrentItemPosTv = findViewById(R.id.main_current_item_position_tv);
    }

    private void loadData() {
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

        BannerCreator.setDefault(mFlyBanner, mLocalImages, position ->
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
                        setCurItemPos(index, isLastPage);
                    }
                });

        setLoopStatus();
        setCurItemPos(0, mLocalImages.size() <= 1);
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

    private void setCurItemPos(final int index, final boolean isLastPage) {
        final String text = "当前页position：" + index + "，是否处于最后一页：" + (isLastPage ? "true" : "false");
        mCurrentItemPosTv.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        mFlyBanner.startTurning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        mFlyBanner.stopTurning();
    }

    public void loopControl(View view) {
        final boolean isCanLoop = mFlyBanner.isCanLoop();
        mFlyBanner.setCanLoop(!isCanLoop);
        setLoopStatus();
    }

    public void refreshData(View view) {
        loadData();
        Toast.makeText(this, "数据已刷新", Toast.LENGTH_SHORT).show();
    }

    public void setCurrentItem(View view) {
        final int min = 0;
        final int max = mLocalImages.size();
        final int randomNum = new Random().nextInt(max - min) + min;
        Log.e("weixi", "setCurrentItem: " + randomNum);
        mFlyBanner.setCurrentItem(randomNum);
    }
}
