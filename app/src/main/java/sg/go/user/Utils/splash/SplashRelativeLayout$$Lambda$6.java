package sg.go.user.Utils.splash;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.widget.ImageView;

final /* synthetic */ class SplashRelativeLayout$$Lambda$6 implements AnimatorUpdateListener {
    private final ImageView arg$1;

    private SplashRelativeLayout$$Lambda$6(ImageView imageView) {
        this.arg$1 = imageView;
    }

    public static AnimatorUpdateListener lambdaFactory$(ImageView imageView) {
        return new SplashRelativeLayout$$Lambda$6(imageView);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}
