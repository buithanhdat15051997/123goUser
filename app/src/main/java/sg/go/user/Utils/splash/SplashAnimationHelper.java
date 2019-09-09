package sg.go.user.Utils.splash;

import android.content.Context;
import android.os.Build.VERSION;
import android.widget.RelativeLayout;

public class SplashAnimationHelper {
    public static final int BUTTON_BAR_SIZE_PX = 60;
    public static final int MARKER_APPEAR_ANIMATION = 150;
    public static final int ROUTE_APPEAR_ANIMATION = 500;
    public static final int VEHICLE_ANIMATION_DURATION = 2000;

    public enum RoutePosition {
        TOP,
        BOT
    }

    public class SplashRouteAnimation {
        private SplashRelativeLayout splashRelativeLayout;
        private RelativeLayout wrapperLayout;

        public SplashRouteAnimation(Context context) {
            this.splashRelativeLayout = new SplashRelativeLayout(context);
        }

        public void startAnimation(RelativeLayout relativeLayout) {
            if (VERSION.SDK_INT >= 21) {
                this.wrapperLayout = relativeLayout;
                relativeLayout.addView(this.splashRelativeLayout);
                this.splashRelativeLayout.startAnimation();
            }
        }

        public void stopAnimation() {
           // Timber.m833e("stopAnimation: ", new Object[0]);
            if (!(this.splashRelativeLayout == null || this.wrapperLayout == null)) {
                this.splashRelativeLayout.stopAnimation();
                this.splashRelativeLayout.removeAllViews();
            }
            if (this.wrapperLayout != null) {
                this.wrapperLayout.removeView(this.splashRelativeLayout);
            }
        }
    }

    public SplashRouteAnimation createSplashAnimation(Context context) {
        return new SplashRouteAnimation(context);
    }
}
