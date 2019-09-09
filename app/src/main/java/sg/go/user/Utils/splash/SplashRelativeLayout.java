package sg.go.user.Utils.splash;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import sg.go.user.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.maxWidth;


public class SplashRelativeLayout extends RelativeLayout {
    private RouteLayerView botBaseRouteView;
    private RouteLayerView botOverlayRouteView;
    private ImageView botPickupMarkerView;
    private Route botRoute;
    private AnimatorSet botRouteAnimatorSet;
    private AnimationVehicle botVehicle;
    private Context context;
    private int pickupMarkerSize = 34;
    private RouteLayerView topBaseRouteView;
    private RouteLayerView topOverlayRouteView;
    private ImageView topPickupMarkerView;
    private Route topRoute;
    private AnimatorSet topRouteAnimatorSet;
    private AnimationVehicle topVehicle;
    public static final float DRIVER_MARKER_Z_INDEX = 1.0f;

    public SplashRelativeLayout(Context context) {
        super(context);
        this.context = context;
        createTopRouteAnimatorSet();
    }

    public void startAnimation() {
        this.topRouteAnimatorSet.start();
    }

    private void createTopRouteAnimatorSet() {
        createNewTopRoute();
        addTopViewsToLayout();
        List<Animator> animatorList = new ArrayList();
        animatorList.add(createBaseRouteAnimation(this.topBaseRouteView, this.topRoute));
        animatorList.add(createPickupMarkerAppearAnimation(this.topPickupMarkerView, this.topRoute));
        animatorList.add(createOverlayRouteAnimation(this.topOverlayRouteView, this.topRoute));
        animatorList.add(createVehicleMovementAnimation(this.topOverlayRouteView, this.topRoute, this.topVehicle, this.topPickupMarkerView));
        animatorList.add(createRouteDisappearingAnimation(this.topBaseRouteView, this.topRoute));
        this.topRouteAnimatorSet = new AnimatorSet();
        this.topRouteAnimatorSet.playSequentially(animatorList);
    }

    private void createBotRouteAnimatorSet() {
        createNewBotRoute();
        addBotViewsToLayout();
        List<Animator> animatorList = new ArrayList();
        animatorList.add(createBaseRouteAnimation(this.botBaseRouteView, this.botRoute));
        animatorList.add(createPickupMarkerAppearAnimation(this.botPickupMarkerView, this.botRoute));
        animatorList.add(createOverlayRouteAnimation(this.botOverlayRouteView, this.botRoute));
        animatorList.add(createVehicleMovementAnimation(this.botOverlayRouteView, this.botRoute, this.botVehicle, this.botPickupMarkerView));
        animatorList.add(createRouteDisappearingAnimation(this.botBaseRouteView, this.botRoute));
        this.botRouteAnimatorSet = new AnimatorSet();
        this.botRouteAnimatorSet.playSequentially(animatorList);
    }

    private ValueAnimator createBaseRouteAnimation(RouteLayerView routeLayerView, Route route) {
        ValueAnimator animation = ValueAnimator.ofFloat(new float[]{0.0f, route.getBasePathMeasureLength()});
        animation.addUpdateListener(SplashRelativeLayout$$Lambda$1.lambdaFactory$(routeLayerView));
        animation.setDuration(500);
        return animation;
    }

    static /* synthetic */ void lambda$createBaseRouteAnimation$0(RouteLayerView routeLayerView, ValueAnimator valueAnimator) {
        routeLayerView.setPathLength(0.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        routeLayerView.invalidate();
    }

    private ValueAnimator createOverlayRouteAnimation(RouteLayerView routeLayerView, Route route) {
        ValueAnimator animation = ValueAnimator.ofFloat(new float[]{route.getOverlayPathMeasureLength(), 0.0f});
        animation.addUpdateListener(SplashRelativeLayout$$Lambda$2.lambdaFactory$(routeLayerView, maxWidth));
        animation.setDuration(500);
        return animation;
    }

    static /* synthetic */ void lambda$createOverlayRouteAnimation$1(RouteLayerView routeLayerView, float maxWidth, ValueAnimator valueAnimator) {
        routeLayerView.setPathLength(((Float) valueAnimator.getAnimatedValue()).floatValue(), maxWidth);
        routeLayerView.invalidate();
    }

    private AnimatorSet createVehicleMovementAnimation(final RouteLayerView routeOverlayView, Route route, final AnimationVehicle vehicle, final ImageView pickupMarkerView) {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator vehicleAnimation = ValueAnimator.ofFloat(new float[]{0.0f, DRIVER_MARKER_Z_INDEX});
        vehicleAnimation.addUpdateListener(SplashRelativeLayout$$Lambda$3.lambdaFactory$(route, vehicle));
        vehicleAnimation.setStartDelay(150);
        float removeMarkerLength = route.getOverlayPathMeasureLength();
        ValueAnimator routeAnimator = ValueAnimator.ofFloat(new float[]{0.0f, route.getBasePathMeasureLength()});
        routeAnimator.addUpdateListener(SplashRelativeLayout$$Lambda$4.lambdaFactory$(this, routeOverlayView, removeMarkerLength, pickupMarkerView));
        ValueAnimator percentageAnimator = ValueAnimator.ofInt(new int[]{0, 100});
        percentageAnimator .addUpdateListener(SplashRelativeLayout$$Lambda$5.lambdaFactory$(this, route));
        animatorSet.setDuration(3500);
        animatorSet.playTogether(new Animator[]{vehicleAnimation, routeAnimator, percentageAnimator});
        animatorSet.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                SplashRelativeLayout.this.removeView(vehicle);
                SplashRelativeLayout.this.removeView(routeOverlayView);
                SplashRelativeLayout.this.removeView(pickupMarkerView);
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animatorSet;
    }

    static /* synthetic */ void lambda$createVehicleMovementAnimation$2(Route route, AnimationVehicle vehicle, ValueAnimator animation) {
        float[] point = new float[2];
        float val = animation.getAnimatedFraction();
        PathMeasure pathMeasure = new PathMeasure(route.getBasePath(), false);
        pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);
        float x = point[0];
        float y = point[1];
        vehicle.setVehicleRotation(x, y);
        vehicle.setX(x);
        vehicle.setY(y);
    }

    static /* synthetic */ void lambda$createVehicleMovementAnimation$3(SplashRelativeLayout this_, RouteLayerView routeOverlayView, float removeMarkerLength, ImageView pickupMarkerView, ValueAnimator animation) {
        float width = ((Float) animation.getAnimatedValue()).floatValue();
        routeOverlayView.setPathLength(width, removeMarkerLength);
        routeOverlayView.invalidate();
        if (width >= removeMarkerLength && this_.topPickupMarkerView.getVisibility() == VISIBLE) {
            pickupMarkerView.setVisibility(GONE);
        }
    }

    static /* synthetic */ void lambda$createVehicleMovementAnimation$4(SplashRelativeLayout this_, Route route, ValueAnimator animation) {
        if (((Integer) animation.getAnimatedValue()).intValue() >= 50) {
            animation.removeAllUpdateListeners();
            this_.startOtherSideAnimation(route);
            this_.createBotRouteAnimatorSet();
        }
    }

    private void startOtherSideAnimation(Route route) {
        switch (route.routePosition) {
            case TOP:
                createBotRouteAnimatorSet();
                this.botRouteAnimatorSet.start();
                return;
            case BOT:
                createTopRouteAnimatorSet();
                this.topRouteAnimatorSet.start();
                return;
            default:
                return;
        }
    }

    private ValueAnimator createPickupMarkerAppearAnimation(ImageView pickupMarker, Route route) {
        updatePickupMarker(pickupMarker, route);
        ValueAnimator animation = ValueAnimator.ofFloat(new float[]{0.0f, DRIVER_MARKER_Z_INDEX});
        animation.addUpdateListener(SplashRelativeLayout$$Lambda$6.lambdaFactory$(pickupMarker));
        animation.setDuration(150);
        return animation;
    }

    private ValueAnimator createRouteDisappearingAnimation(final RouteLayerView baseRouteView, Route route) {
        ValueAnimator animation = ValueAnimator.ofFloat(new float[]{0.0f, route.getBasePathMeasureLength()});
        animation.addUpdateListener(SplashRelativeLayout$$Lambda$7.lambdaFactory$(baseRouteView, maxWidth));
        animation.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                SplashRelativeLayout.this.removeView(baseRouteView);
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animation;
    }

    static /* synthetic */ void lambda$createRouteDisappearingAnimation$6(RouteLayerView baseRouteView, float maxWidth, ValueAnimator animation1) {
        baseRouteView.setPathLength(((Float) animation1.getAnimatedValue()).floatValue(), maxWidth);
        baseRouteView.invalidate();
    }

    private void createNewTopRoute() {
        this.topRoute = new Route(this.context, SplashAnimationHelper.RoutePosition.TOP);
        this.topBaseRouteView = new RouteLayerView(this.context, this.topRoute.getBasePath(), this.topRoute.getRouteBasePaint());
        this.topOverlayRouteView = new RouteLayerView(this.context, this.topRoute.getOverlayPath(), this.topRoute.getRouteOverlayPaint());
        this.topPickupMarkerView = createPickupMarkerImageView();
        this.topVehicle = new AnimationVehicle(this.context);
    }

    private void createNewBotRoute() {
        this.botRoute = new Route(this.context, SplashAnimationHelper.RoutePosition.BOT);
        this.botBaseRouteView = new RouteLayerView(this.context, this.botRoute.getBasePath(), this.botRoute.getRouteBasePaint());
        this.botOverlayRouteView = new RouteLayerView(this.context, this.botRoute.getOverlayPath(), this.botRoute.getRouteOverlayPaint());
        this.botPickupMarkerView = createPickupMarkerImageView();
        this.botVehicle = new AnimationVehicle(this.context);
    }

    public void stopAnimation() {
        if (this.topRouteAnimatorSet != null) {
            this.topRouteAnimatorSet.cancel();
        }
        if (this.botRouteAnimatorSet != null) {
            this.botRouteAnimatorSet.cancel();
        }
    }

    private void updatePickupMarker(ImageView pickupMarker, Route route) {
        Pair<Float, Float> pickupMarkerCoordinates = route.getPickupMarkerCoordinates();
        pickupMarker.setX((float) (Math.round(((Float) pickupMarkerCoordinates.first).floatValue()) - (this.pickupMarkerSize / 2)));
        pickupMarker.setY((float) (Math.round(((Float) pickupMarkerCoordinates.second).floatValue()) - (this.pickupMarkerSize / 2)));
    }

    private ImageView createPickupMarkerImageView()
    {
        Drawable drawable = ContextCompat.getDrawable(this.context, R.mipmap.address_bar_pickup);
        ImageView imageView = new ImageView(this.context);
        imageView.setAlpha(0.0f);
        imageView.setLayoutParams(new LayoutParams(this.pickupMarkerSize, this.pickupMarkerSize));
        imageView.setImageDrawable(drawable);
        return imageView;
    }

    private void addTopViewsToLayout() {
        addView(this.topBaseRouteView);
        addView(this.topOverlayRouteView);
        addView(this.topPickupMarkerView);
        addView(this.topVehicle);
    }

    private void addBotViewsToLayout() {
        try {
            addView(this.botBaseRouteView);
            addView(this.botOverlayRouteView);
            addView(this.botPickupMarkerView);
            addView(this.botVehicle);
        } catch (OutOfMemoryError e) {
            //Timber.m831d("Failed to allocate bytes", new Object[0]);
        }
    }
}
