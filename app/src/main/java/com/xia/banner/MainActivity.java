package com.xia.banner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xia.banner.option.BannerCreator;
import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity {
    private FlyBanner mFlyBanner;
    private AppCompatTextView mLoopStatusTv;
    private ArrayList<Integer> mLocalImages = new ArrayList<>();

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
    }

    private void loadData() {
        mLocalImages.clear();
        //本地图片集合
        for (int position = 0; position < 7; position++) {
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
                    }
                });
        setLoopStatus();
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
        mLoopStatusTv.setText(mFlyBanner.isCanLoop() ? "开" : "关");
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
    }
}
