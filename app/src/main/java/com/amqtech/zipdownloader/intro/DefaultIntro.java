package com.amqtech.zipdownloader.intro;

import android.os.Bundle;
import android.view.View;

import com.amqtech.zipdownloader.intro_slides.Slide1;
import com.amqtech.zipdownloader.intro_slides.Slide2;
import com.amqtech.zipdownloader.intro_slides.Slide3;
import com.amqtech.zipdownloader.intro_slides.Slide4;
import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by andrew on 6/9/15.
 */
public class DefaultIntro extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new Slide1(), getApplicationContext());
        addSlide(new Slide2(), getApplicationContext());
        addSlide(new Slide3(), getApplicationContext());
        addSlide(new Slide4(), getApplicationContext());
    }

    private void loadMainActivity() {
        finish();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
