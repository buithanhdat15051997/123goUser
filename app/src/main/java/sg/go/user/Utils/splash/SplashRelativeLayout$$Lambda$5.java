package sg.go.user.Utils.splash;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class SplashRelativeLayout$$Lambda$5 implements AnimatorUpdateListener {
    private final SplashRelativeLayout arg$1;
    private final Route arg$2;

    private SplashRelativeLayout$$Lambda$5(SplashRelativeLayout splashRelativeLayout, Route route) {
        this.arg$1 = splashRelativeLayout;
        this.arg$2 = route;
    }

    public static AnimatorUpdateListener lambdaFactory$(SplashRelativeLayout splashRelativeLayout, Route route) {
        return new SplashRelativeLayout$$Lambda$5(splashRelativeLayout, route);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        SplashRelativeLayout.lambda$createVehicleMovementAnimation$4(this.arg$1, this.arg$2, valueAnimator);
    }
}
