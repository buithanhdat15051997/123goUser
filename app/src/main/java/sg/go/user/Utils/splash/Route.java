package sg.go.user.Utils.splash;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Pair;

import sg.go.user.R;
import sg.go.user.Utils.AmbulancyUtils;

import java.util.Random;


public class Route {
    public static final int ASC = 1;
    public static final int DESC = -1;
    public static final int FLAT = 0;
    private int curveRadius = 20;
    private float percentageOfScreenWidth = 0.8f;
    private Pair<Float, Float> pickupMarkerCoordinates;
    private RouteCombination randomRoute;
    private Paint routeBasePaint;
    private Path routeBasePath;
    private Paint routeOverlayPaint;
    private Path routeOverlayPath;
    public SplashAnimationHelper.RoutePosition routePosition;
    private int screenBufferSize = 80;
    private int screenHeight;
    private int squareWidth;

    private class RouteCombination {
        private int[][] routeCombinations = new int[][]{new int[]{-1, -1, 0, 0}, new int[]{0, 0, 1, 1}, new int[]{1, 1, 0, 0}, new int[]{0, 0, -1, -1}, new int[]{-1, 0, 0, -1}, new int[]{1, 0, 0, 1}, new int[]{-1, 0, -1, 0}, new int[]{0, 1, 0, 1}, new int[]{-1, 0, -1, -1}, new int[]{1, 1, 0, 1}};
        private int[] routeStartSquare = new int[]{0, 2, 3, 1, 0, 2, 0, 2, 0, 3};
        private int[] selectedRandomRoute;
        private int selectedStartSquare;

        RouteCombination(SplashAnimationHelper.RoutePosition routePosition) {
            int random = new Random().nextInt(10);
            this.selectedRandomRoute = this.routeCombinations[random];
            this.selectedStartSquare = this.routeStartSquare[random];
            if (routePosition == SplashAnimationHelper.RoutePosition.BOT) {
                for (int i = 0; i < this.selectedRandomRoute.length; i++) {
                    int[] iArr = this.selectedRandomRoute;
                    iArr[i] = iArr[i] * -1;
                }
            }
        }

        private int get(int routeSquare) {
            return this.selectedRandomRoute[routeSquare];
        }

        private int getRouteStartSquare() {
            return this.selectedStartSquare;
        }

        public int size() {
            return this.selectedRandomRoute.length;
        }

        boolean isNextSquareSimilar(int position) {
            return position == size() + -1 || (position + 1 >= size() - 1 && get(position) == get(position + 1));
        }
    }

    public Route(Context context, SplashAnimationHelper.RoutePosition routePosition) {
        this.routePosition = routePosition;
        this.screenHeight = AmbulancyUtils.getScreenHeight(context) - 60;
        this.squareWidth = AmbulancyUtils.getScreenWidth(context) / 4;
        this.routeBasePath = new Path();
        this.routeOverlayPath = new Path();
        this.routeBasePaint = createBasePaint(context);
        this.routeOverlayPaint = createOverlayPaint(context);
        this.randomRoute = new RouteCombination(routePosition);
        this.routeBasePath = createBasePath();
        this.routeOverlayPath = createOverlayPath();
        createPickupMarkerCoordinates();
    }

    private Path createBasePath() {
        int nextY;
        Path path = new Path();
        boolean curvedConnection = false;
        int nextX = 0;
        if (this.routePosition == SplashAnimationHelper.RoutePosition.TOP) {
            nextY = this.randomRoute.getRouteStartSquare() * this.squareWidth;
        } else {
            nextY = this.screenHeight - (this.randomRoute.getRouteStartSquare() * this.squareWidth);
        }
        int curveX = 0;
        int curveY = 0;
        for (int i = 0; i < this.randomRoute.size(); i++) {
            if (i == 0) {
                addLeftBuffer(path, this.randomRoute.get(i), nextY);
                path.lineTo((float) nextX, (float) nextY);
            }
            int previousX = nextX;
            int previousY = nextY;
            nextX += this.squareWidth;
            switch (this.randomRoute.get(i)) {
                case -1:
                    nextY += this.squareWidth;
                    break;
                case 1:
                    nextY -= this.squareWidth;
                    break;
            }
            if (curvedConnection) {
                curveX = previousX + this.curveRadius;
                curveY = getPostCurveY(this.randomRoute.get(i), previousY);
                createCurvedLine(path, previousX, previousY, curveX, curveY);
                curvedConnection = false;
            }
            if (!this.randomRoute.isNextSquareSimilar(i)) {
                curvedConnection = true;
                curveX = nextX - this.curveRadius;
                curveY = getPreCurveY(this.randomRoute.get(i), nextY);
            }
            if (curvedConnection) {
                createStraightLine(path, curveX, curveY);
            } else {
                createStraightLine(path, nextX, nextY);
            }
            if (i == this.randomRoute.size() - 1) {
                addRightBuffer(path, this.randomRoute.get(i), nextX, nextY);
            }
        }
        return path;
    }

    private Path createOverlayPath() {
        PathMeasure pathMeasure = new PathMeasure(this.routeBasePath, false);
        Path path = new Path();
        pathMeasure.getSegment(0.0f, this.percentageOfScreenWidth * pathMeasure.getLength(), path, true);
        return path;
    }

    private int getPreCurveY(int curveSection, int nextY) {
        int curveY = nextY;
        switch (curveSection) {
            case -1:
                return nextY - this.curveRadius;
            case 1:
                return nextY + this.curveRadius;
            default:
                return curveY;
        }
    }

    private int getPostCurveY(int curveSection, int nextY) {
        int curveY = nextY;
        switch (curveSection) {
            case -1:
                return nextY + this.curveRadius;
            case 1:
                return nextY - this.curveRadius;
            default:
                return curveY;
        }
    }

    private void addLeftBuffer(Path path, int startSection, int startY) {
        switch (startSection) {
            case -1:
                path.moveTo((float) (-this.screenBufferSize), (float) (startY - this.screenBufferSize));
                return;
            case 0:
                path.moveTo((float) (-this.screenBufferSize), (float) startY);
                return;
            case 1:
                path.moveTo((float) (-this.screenBufferSize), (float) (this.screenBufferSize + startY));
                return;
            default:
                return;
        }
    }

    private void addRightBuffer(Path path, int endSection, int startX, int startY) {
        switch (endSection) {
            case -1:
                path.lineTo((float) (this.screenBufferSize + startX), (float) (this.screenBufferSize + startY));
                return;
            case 0:
                path.lineTo((float) (this.screenBufferSize + startX), (float) startY);
                return;
            case 1:
                path.lineTo((float) (this.screenBufferSize + startX), (float) (startY - this.screenBufferSize));
                return;
            default:
                return;
        }
    }

    private void createStraightLine(Path path, int nextX, int nextY) {
        path.lineTo((float) nextX, (float) nextY);
    }

    private void createCurvedLine(Path path, int curveCenterX, int curveCenterY, int nextX, int nextY) {
        path.quadTo((float) curveCenterX, (float) curveCenterY, (float) nextX, (float) nextY);
    }

    Path getBasePath() {
        return this.routeBasePath;
    }

    Path getOverlayPath() {
        return this.routeOverlayPath;
    }

    Pair<Float, Float> getPickupMarkerCoordinates() {
        return this.pickupMarkerCoordinates;
    }

    float getBasePathMeasureLength() {
        return new PathMeasure(this.routeBasePath, false).getLength();
    }

    float getOverlayPathMeasureLength() {
        return new PathMeasure(this.routeBasePath, false).getLength() * this.percentageOfScreenWidth;
    }

    private Paint createBasePaint(Context context) {
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.deep_grey));
        paint.setStyle(Style.STROKE);
        paint.setStrokeCap(Cap.ROUND);
        paint.setStrokeWidth(3.0f);
        paint.setAntiAlias(true);
        paint.setDither(true);
        return paint;
    }

    private Paint createOverlayPaint(Context context) {
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.circle_color));
        paint.setStyle(Style.STROKE);
        paint.setStrokeCap(Cap.ROUND);
        paint.setStrokeWidth(6.0f);
        paint.setAntiAlias(true);
        paint.setDither(true);
        return paint;
    }

    Paint getRouteBasePaint() {
        return this.routeBasePaint;
    }

    Paint getRouteOverlayPaint() {
        return this.routeOverlayPaint;
    }

    private void createPickupMarkerCoordinates() {
        float[] coordinates = new float[2];
        new PathMeasure(this.routeBasePath, false).getPosTan(getOverlayPathMeasureLength(), coordinates, null);
        this.pickupMarkerCoordinates = new Pair(Float.valueOf(coordinates[0]), Float.valueOf(coordinates[1]));
    }
}
