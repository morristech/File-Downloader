package com.amqtech.zipdownloader.contributors;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;

import com.amqtech.zipdownloader.R;
import com.pkmmte.view.CircularImageView;

public class Contributors extends ActionBarActivity {

    private CircularImageView david, andrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributers);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setTaskDescription(new ActivityManager.TaskDescription("Contributors",
                    drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher)), Color.parseColor("#212121")));

            Window window = getWindow();
            window.setStatusBarColor(darker(Color.parseColor("#212121"), 0.8f));
        }

        david = (CircularImageView) findViewById(R.id.david_image);
        david.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gplus = "https://plus.google.com/+DavidHanz";
                Intent david = new Intent(Intent.ACTION_VIEW);
                david.setData(Uri.parse(gplus));
                startActivity(david);
            }
        });

        andrew = (CircularImageView) findViewById(R.id.andrew_image);
        andrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gplus = "https://plus.google.com/+AndrewQuebe";
                Intent andrew = new Intent(Intent.ACTION_VIEW);
                andrew.setData(Uri.parse(gplus));
                startActivity(andrew);
            }
        });
    }

    /**
     * Returns darker version of specified <code>color</code>.
     */
    public static int darker(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
