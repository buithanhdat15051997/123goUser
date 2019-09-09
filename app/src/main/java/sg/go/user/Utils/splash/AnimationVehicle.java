package sg.go.user.Utils.splash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v7.widget.AppCompatImageView;
import android.widget.RelativeLayout;

import sg.go.user.R;

import java.util.Random;

public class AnimationVehicle
  extends AppCompatImageView
{
  private float pivotX = 0.0F;
  private float pivotY = 0.0F;
  int resId = createResId();
  
  public AnimationVehicle(Context paramContext)
  {
    super(paramContext);
    setImageResource(this.resId);
    setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
    initializePivot();
    setX(-100.0F);
    setY(-100.0F);
  }
  


  private int createResId() {
    return new Random().nextBoolean() ? R.drawable.carambulance : R.drawable.carambulance;
  }
  
  private void initializePivot()
  {
    Object localObject = new Options();
    ((Options)localObject).inTargetDensity = 160;
    localObject = BitmapFactory.decodeResource(getResources(), 2130837695, (Options)localObject);
    if (localObject == null)
    {
     // Timber.d("Activity is finishing", new Object[0]);
      return;
    }
    this.pivotX = (((Bitmap)localObject).getWidth() / 2);
    this.pivotY = (((Bitmap)localObject).getHeight() / 2);
    setPivotX(this.pivotX);
    setPivotY(this.pivotY);
    setRotation(180.0F);
  }
  
  public void setVehicleRotation(float paramFloat1, float paramFloat2)
  {
    double d2 = Math.toDegrees(Math.atan2(paramFloat2 - getY(), paramFloat1 - getX()) + 3.141592653589793D);
    double d1 = d2;
    if (d2 < 0.0D) {

      d1 = d2 + 360.0D;
    }
    setRotation((float)d1);
    if ((d1 > 0.0D) && (d1 < 150.0D))
    {
      setPivotX(this.pivotX - 4.0F);
      setPivotY(this.pivotY - 4.0F);
      return;
    }
    if ((d1 > 200.0D) && (d1 < 270.0D))
    {
      setPivotX(this.pivotX - 7.0F);
      setPivotY(this.pivotY + 7.0F);
      return;
    }
    setPivotX(this.pivotX);
    setPivotY(this.pivotY);
  }
}


/* Location:              E:\Proven\RYDEAPP\Taxify\jarFiles\dex2jar-2.0\Taxify-dex2jar.jar!\ee\mtakso\client\helper\splash\AnimationVehicle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */