package com.xia.flybanner.view;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author weixia
 * @date 2019/4/23.
 */
@SuppressWarnings("JavadocReference")
public class FBScaleLayoutManager extends LinearLayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    public FBScaleLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
    }

    /**
     * 次要方块的露出距离
     * 此处单位为px
     * 当{@link #mOrientation}为{@link HORIZONTAL}时，表示左右侧Item的露出距离
     * 当{@link #mOrientation}为{@link VERTICAL}时，表示上下方Item的露出距离
     */
    public int mSecondaryExposed = 0;

    /**
     * 次要方块的露出距离的权重
     * 注意，只有当{@link #mSecondaryExposed}为0时，才会使用此属性
     * 此权重为相对RecyclerView而言，并且分别针对左右两侧。
     * 即：当 mSecondaryExposedWeight = 0.1F,那么主Item的宽度为RecyclerView.width * 0.8F
     */
    public float mSecondaryExposedWeight = 0.1F;

    /**
     * 次item缩放量
     * 表示当Item位于次Item位置时，显示尺寸相对于完整尺寸的量
     */
    public float mScaleGap = 0.85F;

    /**
     * 偏移量，如果是纵向排版，那么代表Y，如果是横向排版，那么代表X
     */
    private int mOffset = 0;

    /**
     * 存放所有item的位置和尺寸
     */
    private final SparseArray<Rect> mItemsFrames = new SparseArray<>();

    /**
     * 记录item是否已经展示
     */
    private final SparseBooleanArray mItemsAttached = new SparseBooleanArray();

    /**
     * 对Item进行测量，布局
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果没有item,那么就清空所有的Item,并且结束
        if (getItemCount() == 0) {
            mOffset = 0;
            removeAndRecycleAllViews(recycler);
            return;
        }
        //将所有View移动到回收站
        detachAndScrapAttachedViews(recycler);

        //计算占用的尺寸，Item的测量时，将缩小相应的尺寸，因此将占用部分计算出来
        final int usedWidth = getUsedWidth();
        final int usedHeight = getUsedHeight();
        final View view = recycler.getViewForPosition(0);
        measureChildWithMargins(view, usedWidth, usedHeight);

        //view的尺寸
        final int viewWidth = getDecoratedMeasuredWidth(view);
        final int viewHeight = getDecoratedMeasuredHeight(view);
        //Item的默认位置偏移，默认第一个为居中位置
        int offsetX = (int) (usedWidth * 0.5F);
        int offsetY = (int) (usedHeight * 0.5F);
        for (int position = 0; position < getItemCount(); position++) {
            Rect frame = mItemsFrames.get(position);
            if (frame == null) {
                frame = new Rect();
            }
            frame.set(offsetX, offsetY, offsetX + viewWidth, offsetY + viewHeight);

            mItemsFrames.put(position, frame);
            mItemsAttached.put(position, false);

            if (getOrientation() == RecyclerView.VERTICAL) {
                offsetY += viewHeight;
            } else {
                offsetX += viewWidth;
            }
        }

        layoutItems(recycler, state);
    }

    /**
     * 摆放当前状态下要展示的item
     *
     * @param recycler Recycler to use for fetching potentially cached views for a
     *                 position
     * @param state    Transient state of RecyclerView
     */
    private void layoutItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) { // 跳过preLayout，preLayout主要用于支持动画
            return;
        }

        final int offsetX = getOrientation() == RecyclerView.HORIZONTAL ? mOffset : 0;
        final int offsetY = getOrientation() == RecyclerView.VERTICAL ? mOffset : 0;
        // 当前scroll offset状态下的显示区域
        final Rect displayFrame = new Rect(offsetX, offsetY,
                getHorizontalSpace() + offsetX, getVerticalSpace() + offsetY);

        //移除已显示的但在当前scroll offset状态下处于屏幕外的item
        final Rect childFrame = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            childFrame.left = getDecoratedLeft(child);
            childFrame.top = getDecoratedTop(child);
            childFrame.right = getDecoratedRight(child);
            childFrame.bottom = getDecoratedBottom(child);

            if (!Rect.intersects(displayFrame, childFrame)) {
                mItemsAttached.put(getPosition(child), false);
                removeAndRecycleView(child, recycler);
            }
        }

        //计算占用的尺寸，Item的测量时，将缩小相应的尺寸，因此将占用部分计算出来
        final int usedWidth = getUsedWidth();
        final int usedHeight = getUsedHeight();

        //摆放需要显示的item
        //由于RecyclerView实际上并没有scroll，也就是说RecyclerView容器的滑动效果是依赖于LayoutManager对item进行平移来实现的
        //故在放置item时要将item的计算位置平移到实际位置
        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayFrame, mItemsFrames.get(i))) {
                //在onLayoutChildren时由于移除了所有的item view，可以遍历全部item进行添加
                //在scroll时就不同了，由于scroll时会先将已显示的item view进行平移，
                //然后移除屏幕外的item view，此时仍然在屏幕内显示的item view就无需再次添加了
                if (!mItemsAttached.get(i)) {
                    final View scrap = recycler.getViewForPosition(i);
                    measureChildWithMargins(scrap, usedWidth, usedHeight);
                    addView(scrap);
                    final Rect frame = mItemsFrames.get(i);
                    // Important！布局到RecyclerView容器中，所有的计算都是为了得出任意position的item的边界来布局
                    layoutDecorated(scrap,
                            frame.left - offsetX,
                            frame.top - offsetY,
                            frame.right - offsetX,
                            frame.bottom - offsetY);
                    mItemsAttached.put(i, true);
                }
            }
        }

        final int borderX = usedWidth / 2;
        final int borderY = usedHeight / 2;
        displayFrame.set(displayFrame.left + borderX,
                displayFrame.top + borderY,
                displayFrame.right - borderX,
                displayFrame.bottom - borderX);

        //对非主要Item进行缩放处理
        for (int i = 0; i < getItemCount(); i++) {
            final View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            final int position = getPosition(child);
            final float weight = offsetWeight(displayFrame, mItemsFrames.get(position));
            final float scale = (1 - mScaleGap) * weight + mScaleGap;
            child.setScaleY(scale);
            child.setScaleX(scale);
        }
    }

    /**
     * 获取位置偏移的比例，以此来计算Item的缩放
     */
    private float offsetWeight(Rect displayFrame, Rect itemFrame) {
        if (getOrientation() == RecyclerView.HORIZONTAL) {
            return overlapLength(displayFrame.left, itemFrame.left,
                    displayFrame.right, itemFrame.right) * 1.0F / displayFrame.width();
        } else {
            return overlapLength(displayFrame.top, itemFrame.top,
                    displayFrame.bottom, itemFrame.bottom) * 1.0F / displayFrame.height();
        }
    }

    /**
     * 计算两个线段之间的重叠部分的长度
     */
    private int overlapLength(int start1, int start2, int end1, int end2) {
        return Math.min(end1, end2) - Math.max(start1, start2);
    }

    /**
     * 获取已使用的宽度，即不可用于Item的缩进宽度
     */
    private int getUsedWidth() {
        //计算占用的尺寸，Item的测量时，将缩小相应的尺寸，因此将占用部分计算出来
        if (getOrientation() == RecyclerView.VERTICAL) {
            return 0;
        }
        if (mSecondaryExposed == 0) {
            return (int) (getWidth() * (mSecondaryExposedWeight * 2));
        } else {
            return mSecondaryExposed * 2;
        }
    }

    /**
     * 获取已使用的高度，即不可用于Item的缩进高度
     */
    private int getUsedHeight() {
        if (getOrientation() == RecyclerView.HORIZONTAL) {
            return 0;
        }
        if (mSecondaryExposed == 0) {
            return (int) (getHeight() * (mSecondaryExposedWeight * 2));
        } else {
            return mSecondaryExposed * 2;
        }
    }

    /**
     * 容器去除padding后的宽度
     *
     * @return 实际可摆放item的空间
     */
    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    /**
     * 容器去除padding后的高度
     *
     * @return 实际可摆放item的空间
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 横向滑动时，回调的方法
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int tempX = mOffset + dx;
        final int horizontalSpace = getHorizontalSpace();
        final int usedWidth = getUsedWidth();
        final int minOffset = horizontalSpace / 2 * -1;
        final int maxOffset = getItemCount() * (horizontalSpace - usedWidth) + usedWidth - horizontalSpace / 2;

        int scrollLength = dx;
        if (tempX < minOffset) {
            scrollLength = minOffset - mOffset;
        } else if (tempX > maxOffset) {
            scrollLength = maxOffset - mOffset;
        }
        mOffset += scrollLength;

        offsetChildrenHorizontal(-scrollLength);
        if (recycler != null && state != null) {
            layoutItems(recycler, state);
        }

        return scrollLength;
    }

    /**
     * 获取当前位置到指定位置的距离间隔
     */
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int leftOff = getUsedWidth() / 2;
        final int topOff = getUsedHeight() / 2;
        final int itemHeight = getVerticalSpace() - getUsedHeight();
        final int itemWidth = getHorizontalSpace() - getUsedWidth();
        final float left = getOrientation() == RecyclerView.HORIZONTAL
                ? 1.0F * targetPosition * itemWidth + leftOff - mOffset : 0f;
        final float top = getOrientation() == RecyclerView.VERTICAL
                ? 1.0F * targetPosition * itemHeight + topOff - mOffset : 0f;
        return new PointF(left, top);
    }

    /**
     * 纵向滑动时，回调的方法
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int tempY = mOffset + dy;
        final int verticalSpace = getVerticalSpace();
        final int usedHeight = getUsedHeight();
        final int minOffset = verticalSpace / 2 * -1;
        final int maxOffset = getItemCount() * (verticalSpace - usedHeight) + usedHeight - verticalSpace / 2;

        int scrollLength = dy;
        if (tempY < minOffset) {
            scrollLength = minOffset - mOffset;
        } else if (tempY > maxOffset) {
            scrollLength = maxOffset - mOffset;
        }
        mOffset += scrollLength;

        offsetChildrenVertical(-scrollLength);
        if (recycler != null && state != null) {
            layoutItems(recycler, state);
        }

        return scrollLength;
    }

    /**
     * 滑动至指定为的方法
     */
    @Override
    public void scrollToPosition(int position) {
        final int pos = Math.min(Math.max(position, 0), getItemCount());
        mOffset = getOrientation() == RecyclerView.HORIZONTAL
                ? mItemsFrames.get(pos).left : mItemsFrames.get(pos).top;
        requestLayout();
    }

    /**
     * 以动画的形式，带有中间过程的滑动到指定位置
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final int pos = Math.min(Math.max(position, 0), getItemCount());
        final LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext());
        scroller.setTargetPosition(pos);
        startSmoothScroll(scroller);
    }
}
