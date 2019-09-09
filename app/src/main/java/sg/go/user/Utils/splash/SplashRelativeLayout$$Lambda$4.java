package sg.go.user.Utils.splash;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.widget.ImageView;

final /* synthetic */ class SplashRelativeLayout$$Lambda$4 implements AnimatorUpdateListener {
    private final SplashRelativeLayout arg$1;
    private final RouteLayerView arg$2;
    private final float arg$3;
    private final ImageView arg$4;

    private SplashRelativeLayout$$Lambda$4(SplashRelativeLayout splashRelativeLayout, RouteLayerView routeLayerView, float f, ImageView imageView) {
        this.arg$1 = splashRelativeLayout;
        this.arg$2 = routeLayerView;
        this.arg$3 = f;
        this.arg$4 = imageView;
    }

    public static AnimatorUpdateListener lambdaFactory$(SplashRelativeLayout splashRelativeLayout, RouteLayerView routeLayerView, float f, ImageView imageView) {
        return new SplashRelativeLayout$$Lambda$4(splashRelativeLayout, routeLayerView, f, imageView);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        SplashRelativeLayout.lambda$createVehicleMovementAnimation$3(this.arg$1, this.arg$2, this.arg$3, this.arg$4, valueAnimator);
    }
}
