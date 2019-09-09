package sg.go.user.Utils.splash;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class SplashRelativeLayout$$Lambda$1 implements AnimatorUpdateListener {
    private final RouteLayerView arg$1;

    private SplashRelativeLayout$$Lambda$1(RouteLayerView routeLayerView) {
        this.arg$1 = routeLayerView;
    }

    public static AnimatorUpdateListener lambdaFactory$(RouteLayerView routeLayerView) {
        return new SplashRelativeLayout$$Lambda$1(routeLayerView);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        SplashRelativeLayout.lambda$createBaseRouteAnimation$0(this.arg$1, valueAnimator);
    }
}
