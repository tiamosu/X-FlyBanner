# X-FlyBanner
## 本项目fork自([Bigkoo/Android-ConvenientBanner](https://github.com/Bigkoo/Android-ConvenientBanner))，在此基础上进行了相关优化及实现项目中需求。

## Download

Gradle:
```groovy
allprojects {
    repositories {
        google()
        jcenter()
        //添加maven地址
        maven { url 'https://dl.bintray.com/weixia/maven' }
    }
}
```

### fly-banner
[ ![Download](https://api.bintray.com/packages/weixia/maven/x-flybanner/images/download.svg) ](https://bintray.com/weixia/maven/x-flybanner/_latestVersion)
```groovy
compile 'me.xia:x-flybanner:1.0.4'
```

### 效果图：
![](screenshot/GIF_1.gif)

### 配置：
```java
    public static void setDefault(final FlyBanner flyBanner,
                                  final List datas,
                                  final boolean isHorizontal,
                                  final boolean isGuidePage,
                                  final OnItemClickListener onItemClickListener,
                                  final OnPageChangeListener onPageChangeListener) {

        final int dataSize = datas.size();
        final int indicatorAlign = isHorizontal ? PageIndicatorAlign.ALIGN_RIGHT_BOTTOM : PageIndicatorAlign.ALIGN_RIGHT_CENTER;
        final int indicatorOrientation = isHorizontal ? PageIndicatorOrientation.HORIZONTAL : PageIndicatorOrientation.VERTICAL;
        final int orientation = isHorizontal ? PageOrientation.HORIZONTAL : PageOrientation.VERTICAL;

        flyBanner
                //设置 banner 视图数据初始化
                .setPages(new HolderCreator(), datas)
                //设置 banner 翻页方向
                .setOrientation(orientation)
                //是否为引导页
                .setGuidePage(isGuidePage)
                //banner 生成
                .pageBuild()
                //设置指示器样式
                .setIndicatorId(new int[]{R.drawable.indicator_gray_radius, R.drawable.indicator_white_radius})
                //设置指示器位置，默认为右下角
                .setIndicatorAlign(indicatorAlign)
                //设置指示器方向，默认为横向
                .setIndicatorOrientation(indicatorOrientation)
                //设置指示器偏移
                .setIndicatorMargin(30)
                //指示器生成
                .indicatorBuild(dataSize > 1)
                //设置 viewPager 圆角
                .setRadius(50)
                //设置自动轮播时间
                .start(3000)
                //设置是否进行自动轮播
                .setCanLoop(dataSize > 1 && !isGuidePage)
                //设置点击事件监听
                .setOnItemClickListener(onItemClickListener)
                //设置页面切换事件监听
                .setOnPageChangeListener(onPageChangeListener);
    }
```

### 详情请见 demo

*感谢原作者的贡献*