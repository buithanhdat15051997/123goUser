package sg.go.user.Utils.splash;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class SplashRelativeLayout$$Lambda$7 implements AnimatorUpdateListener {
    private final RouteLayerView arg$1;
    private final float arg$2;

    private SplashRelativeLayout$$Lambda$7(RouteLayerView routeLayerView, float f) {
        this.arg$1 = routeLayerView;
        this.arg$2 = f;
    }

    public static AnimatorUpdateListener lambdaFactory$(RouteLayerView routeLayerView, float f) {
        return new SplashRelativeLayout$$Lambda$7(routeLayerView, f);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        SplashRelativeLayout.lambda$createRouteDisappearingAnimation$6(this.arg$1, this.arg$2, valueAnimator);
    }
}
