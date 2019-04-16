package com.xia.banner;

import android.os.Bundle;
import android.util.Log;

import com.xia.banner.option.BannerCreator;
import com.xia.flybanner.FlyBanner;
import com.xia.flybanner.listener.OnItemClickListener;
import com.xia.flybanner.listener.OnPageChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private FlyBanner mFlyBanner;
    private ArrayList<Integer> mLocalImages = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlyBanner = findViewById(R.id.banner);

        loadTestDatas();
        BannerCreator.setDefault(mFlyBanner, mLocalImages, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("weixi", "onItemClick: " + position);
            }
        }, new OnPageChangeListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onPageSelected(int index) {
                Log.e("weixi", "onPageSelected: " + index);
            }
        });
    }

    private void loadTestDatas() {
        //本地图片集合
        for (int position = 0; position < 7; position++) {
            mLocalImages.add(getResId("ic_test_" + position, R.drawable.class));
        }
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     */
    private int getResId(String variableName, Class<?> cls) {
        try {
            final Field idField = cls.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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
}
