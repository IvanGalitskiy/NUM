package com.vallsoft.num.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vallsoft.num.R;


public class ProgressView extends FrameLayout implements IProgressListener {

    private TextView vAdd;
    private ProgressBar vProgress;

    public ProgressView(@NonNull Context context) {
        super(context);
        init();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init(){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_progress,this,true);
        vAdd = v.findViewById(R.id.vText);
        vProgress = v.findViewById(R.id.vProgress);
    }

    public void showProgress(){
        vAdd.setVisibility(INVISIBLE);
        vProgress.setVisibility(VISIBLE);
    }
    public void hideProgress(){
        vAdd.setVisibility(VISIBLE);
        vProgress.setVisibility(INVISIBLE);
    }
}
