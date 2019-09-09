package sg.go.user.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import sg.go.user.R;

/**
 * Created by user on 1/4/2017.
 */

public class CustomRegularEditText extends android.support.v7.widget.AppCompatEditText {

    private static final String TAG = "EditText";

    private Typeface typeface;

    public CustomRegularEditText(Context context) {
        super(context);
    }

    public CustomRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHintTextColor(getResources().getColor(R.color.lightblueA700));
        setCustomFont(context);
    }

    public CustomRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.app);
        String customFont = a.getString(R.styleable.app_customFont);
        setCustomFont(ctx);
        a.recycle();
    }

    private boolean setCustomFont(Context ctx) {
        try {
            if (typeface == null) {
                // Log.i(TAG, "asset:: " + "fonts/" + asset);
                typeface = ResourcesCompat.getFont(ctx, R.font.proximanova_regular);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }

}

