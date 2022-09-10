package com.lyni.app.lib.core.common.base.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.lyni.app.lib.core.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

/**
 * @author Liangyong Ni
 * @date 2022/3/4
 * description 刷新头
 */
@SuppressLint("RestrictedApi")
public class DefaultRefreshHeader extends FrameLayout implements RefreshHeader {
    private LottieAnimationView lottieAnimationView;
    private String assetsFileName;

    public DefaultRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable") TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.Refresh);
        assetsFileName = array.getString(R.styleable.Refresh_assetsName);
        array.recycle();
        this.lottieAnimationView = new LottieAnimationView(context);
        this.lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        this.addView(this.lottieAnimationView);
    }

    @NonNull
    public View getView() {
        return this;
    }

    @NonNull
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    public void setPrimaryColors(int... colors) {
    }

    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
    }

    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
    }

    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    }

    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    }

    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        this.lottieAnimationView.cancelAnimation();
        return 0;
    }

    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    public boolean isSupportHorizontalDrag() {
        return false;
    }

    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
                this.lottieAnimationView.setFrame(0);
                this.lottieAnimationView.setProgress(0.0F);
                break;
            case PullDownToRefresh:
                this.lottieAnimationView.setVisibility(VISIBLE);
                break;
            case ReleaseToRefresh:
                if (!TextUtils.isEmpty(this.assetsFileName)) {
                    if (!lottieAnimationView.isAnimating()) {
                        this.lottieAnimationView.setAnimation(assetsFileName);
                        this.lottieAnimationView.playAnimation();
                    }
                }

            case Refreshing:
                this.lottieAnimationView.setVisibility(VISIBLE);
                break;
            case RefreshFinish:
                this.lottieAnimationView.setVisibility(GONE);
            default:
                break;

        }

    }

    public void setAssetsFileName(String assetsFileName) {
        this.assetsFileName = assetsFileName;
    }
}


