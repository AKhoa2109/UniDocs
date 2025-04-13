package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

public class AnimUtils {
    private static final int ANIMATION_DURATION = 1000;
    private static boolean isAnimationStart;

    public static void translateAnimation(final ImageView viewAnimation, @NotNull final View startView, View endView,
                                          final Animation.AnimationListener animationListener, String image) {

        // 1. Load ảnh vào viewAnimation và đảm bảo nó đã được đo (nếu cần)
        Glide.with(viewAnimation.getContext())
                .load(image)
                .into(viewAnimation);

        // Đảm bảo viewAnimation đã có kích thước
        viewAnimation.post(() -> {
            // 2. Lấy toạ độ màn hình của startView và endView
            final int[] startPos = new int[2];
            startView.getLocationOnScreen(startPos);
            final int[] endPos = new int[2];
            endView.getLocationOnScreen(endPos);

            // 3. Tính toạ độ bắt đầu: center của startView
            float startX = startPos[0]
                    + startView.getWidth()  / 2f
                    - viewAnimation.getWidth()  / 2f;
            float startY = startPos[1]
                    + startView.getHeight() / 2f
                    - viewAnimation.getHeight() / 2f;

            // 4. Tính toạ độ kết thúc: bottom‑left corner của endView
            float endX = endPos[0]
                    - viewAnimation.getWidth()  / 2f;
            float endY = endPos[1]
                    + endView.getHeight()
                    - viewAnimation.getHeight() / 2f;

            // 5. Tạo TranslateAnimation với ABSOLUTE offsets
            TranslateAnimation translate = new TranslateAnimation(
                    Animation.ABSOLUTE, startX,
                    Animation.ABSOLUTE, endX,
                    Animation.ABSOLUTE, startY,
                    Animation.ABSOLUTE, endY
            );
            translate.setDuration(ANIMATION_DURATION);
            translate.setInterpolator(new AccelerateInterpolator());

            // 6. Thiết lập listener để show/hide viewAnimation đúng lúc
            translate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    viewAnimation.setVisibility(View.VISIBLE);
                    if (animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    viewAnimation.setVisibility(View.GONE);
                    if (animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat(animation);
                    }
                }
            });

            // 7. Bắt đầu animation
            viewAnimation.clearAnimation();
            viewAnimation.startAnimation(translate);
        });
    }
}
