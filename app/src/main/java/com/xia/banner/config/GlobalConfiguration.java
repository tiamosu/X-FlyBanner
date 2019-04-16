package com.xia.banner.config;

import android.app.Application;
import android.content.Context;

import com.xia.fly.base.delegate.AppLifecycles;
import com.xia.fly.di.module.GlobalConfigModule;
import com.xia.fly.imageloader.GlideImageLoaderStrategy;
import com.xia.fly.integration.ConfigModule;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.fragment.app.FragmentManager;

/**
 * @author weixia
 * @date 2019/4/16.
 */
public class GlobalConfiguration implements ConfigModule {
    @Override
    public void applyOptions(@NotNull Context context, @NotNull GlobalConfigModule.Builder builder) {
        builder.imageLoaderStrategy(new GlideImageLoaderStrategy());
    }

    @Override
    public void injectActivityLifecycle(@NotNull Context context, @NotNull List<Application.ActivityLifecycleCallbacks> list) {
    }

    @Override
    public void injectAppLifecycle(@NotNull Context context, @NotNull List<AppLifecycles> list) {
    }

    @Override
    public void injectFragmentLifecycle(@NotNull Context context, @NotNull List<FragmentManager.FragmentLifecycleCallbacks> list) {
    }
}
