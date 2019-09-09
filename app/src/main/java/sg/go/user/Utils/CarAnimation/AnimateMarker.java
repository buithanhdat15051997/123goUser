package sg.go.user.Utils.CarAnimation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Mahesh on 11/6/2017.
 */

public class AnimateMarker {

    public static void animateMarker(final Context activity, final Location destination, final Marker marker, final GoogleMap googleMap, final String bearing) {
        if (marker != null) {

            double[] startValues = new double[]{marker.getPosition().latitude, marker.getPosition().longitude};
            double[] endValues = new double[]{destination.getLatitude(), destination.getLongitude()};

            ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), startValues, endValues);
            latLngAnimator.setDuration(3000);
            latLngAnimator.setInterpolator(new DecelerateInterpolator());
            latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    double[] animatedValue = (double[]) animation.getAnimatedValue();

                    marker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));

                    marker.setRotation(Float.valueOf(String.valueOf(bearing)));

                    LatLng coordinate = new LatLng(animatedValue[0], animatedValue[1]); //Store these lat lng values somewhere. These should be constant.


                    if (null == googleMap) {
                        return;
                    } else {


                        CameraPosition camPos = CameraPosition
                                .builder(
                                        googleMap.getCameraPosition() // current Camera
                                )
                                // .bearing(destination.getBearing())
                                .target(coordinate)
                                .zoom(17)
                                .build();

                       googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                    }

                }
            });
            latLngAnimator.start();
        }

    }

}
