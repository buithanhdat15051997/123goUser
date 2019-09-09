package sg.go.user.Utils.splash;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class SplashRelativeLayout$$Lambda$3 implements AnimatorUpdateListener {
    private final Route arg$1;
    private final AnimationVehicle arg$2;

    private SplashRelativeLayout$$Lambda$3(Route route, AnimationVehicle animationVehicle) {
        this.arg$1 = route;
        this.arg$2 = animationVehicle;
    }

    public static AnimatorUpdateListener lambdaFactory$(Route route, AnimationVehicle animationVehicle) {
        return new SplashRelativeLayout$$Lambda$3(route, animationVehicle);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        SplashRelativeLayout.lambda$createVehicleMovementAnimation$2(this.arg$1, this.arg$2, valueAnimator);
    }
}
