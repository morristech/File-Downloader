package com.amqtech.zipdownloader.intro_slides;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amqtech.zipdownloader.R;

/**
 * Created by andrew on 6/8/15.
 */
public class Slide2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.appintro_slide2, container, false);
        return v;
    }

}
