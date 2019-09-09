package sg.go.user.Utils.splash;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;

public class RouteLayerView
  extends View
{
  private Paint paint;
  private Path partialPath;
  private float pathLeftStart;
  private PathMeasure pathMeasure;
  private float pathRightEnd;
  
  public RouteLayerView(Context paramContext)
  {
    super(paramContext);
  }
  
  public RouteLayerView(Context paramContext, Path paramPath, Paint paramPaint)
  {
    super(paramContext);
    setWillNotDraw(false);
    attachNewPath(paramPath, paramPaint);
  }
  
  public void attachNewPath(Path paramPath, Paint paramPaint)
  {
    this.paint = paramPaint;
    this.pathMeasure = new PathMeasure(paramPath, false);
    this.partialPath = new Path();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    this.partialPath.rewind();
    this.pathMeasure.getSegment(this.pathLeftStart, this.pathRightEnd, this.partialPath, true);
    paramCanvas.drawPath(this.partialPath, this.paint);
  }
  
  public void setPathLength(float paramFloat1, float paramFloat2)
  {
    this.pathLeftStart = paramFloat1;
    this.pathRightEnd = paramFloat2;
  }
}

