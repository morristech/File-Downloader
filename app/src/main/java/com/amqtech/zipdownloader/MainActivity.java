package com.amqtech.zipdownloader;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.amqtech.zipdownloader.contributors.Contributors;
import com.amqtech.zipdownloader.fragments.DownloadFragment;
import com.amqtech.zipdownloader.intro.DefaultIntro;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

public class MainActivity extends AppCompatActivity {

    //Toolbar initialization
    private Toolbar toolbar;

    //Navigation Drawer initialization
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set our main content view for activity
        setContentView(R.layout.activity_main);

        //Find the toolbar in XML
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Set it's title text color to white
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //Change activity's action bar to toolbar
        setSupportActionBar(toolbar);

        //Set up a new drawer builder
        drawer = new DrawerBuilder()
                //Bind it to this activity
                .withActivity(this)
                        //Bind it to the toolbar attached to this activity
                .withToolbar(toolbar)
                        //Set header image
                .withHeader(R.layout.util_navdrawer_header)
                        //Disable translucent navigation menu/bar
                .withTranslucentNavigationBar(false)
                        //Add our drawer items
                .addDrawerItems(
                        //Regular drawer item
                        new PrimaryDrawerItem()
                                //Icon
                                .withIcon(R.mipmap.ic_launcher)
                                        //Name
                                .withName("Home")
                                        //ID = 1
                                .withIdentifier(1)
                                        //Text color
                                .withTextColor(Color.parseColor("#444444")),

                        //Divider item
                        new DividerDrawerItem(),

                        //Small/secondary drawer item
                        new SecondaryDrawerItem()
                                //Icon
                                .withIcon(R.mipmap.ic_launcher)
                                        //Name
                                .withName("Sources")
                                        //ID = 2
                                .withIdentifier(2)
                                        //Text color
                                .withTextColor(Color.parseColor("#444444"))
                                        //Disable highlighting since this isn't a fragment
                                .withCheckable(false),

                        //Small/secondary drawer item
                        new SecondaryDrawerItem()
                                //Icon
                                .withIcon(R.mipmap.ic_launcher)
                                        //Name
                                .withName("About")
                                        //ID = 3
                                .withIdentifier(3)
                                        //Text color
                                .withTextColor(Color.parseColor("#444444"))
                                        //Disable highlighting since this isn't a fragment
                                .withCheckable(false)
                )
                        //Attach onClickListeners for each drawer item (not dividers!)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        // update the main content by replacing fragments
                        Fragment fragment = new DownloadFragment();
                        FragmentManager fragmentManager = getFragmentManager();

                        switch (iDrawerItem.getIdentifier()) {
                            case 1:
                                fragment = new DownloadFragment();
                                break;
                            case 2:
                                new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityTitle("Sources")
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .start(MainActivity.this);
                                break;
                            case 3:
                                //Launch about activity
                                Intent about = new Intent(MainActivity.this, Contributors.class);
                                startActivity(about);
                                break;

                        }
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        if (iDrawerItem instanceof Nameable) {
                            setTitle(((Nameable) iDrawerItem).getName());
                        }

                        return false;
                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();

        //Set a custom shadow that overlays the main content when the drawer opens
        drawer.getDrawerLayout().setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //If the activity has never started before...
                if (isFirstStart) {

                    //Launch app intro
                    Intent i = new Intent(MainActivity.this, DefaultIntro.class);
                    startActivity(i);

                    //Open the drawer to let users know it exists
                    drawer.openDrawer();

                    //Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //Apply changes
                    e.apply();
                }
            }
        });

        //Start this thread AFTER drawer and activity have been loaded to prevent errors
        t.start();
    }

    @Override
    public void setTitle(CharSequence title) {
        //noinspection StatementWithEmptyBody
        if (title == "Home" || title == "Sources" || title == "About") {
            //Do nothing...
        } else
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(title);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //Get an instance of the ActionBar
        ActionBar mActionBar = getSupportActionBar();

        //Set the action bar colors to whatever the user selects from the ListPreference
        int color = Color.parseColor("#ff1963be");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Overview color
            int actionBarColor = Color.parseColor("#ff1963be");
            MainActivity.this.setTaskDescription(new ActivityManager.TaskDescription("Zip Downloader",
                    drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher)), actionBarColor));
        }

        if (mActionBar != null) {
            toolbar.setBackgroundColor(color);
            drawer.setStatusBarColor(darker(color, 0.8f));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(8);
            }
        }
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
}
