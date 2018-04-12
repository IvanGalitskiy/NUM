package com.vallsoft.num.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vallsoft.num.data.database.SettingsPreference;

import com.vallsoft.num.R;


public class FragmentPositionPicker extends Fragment implements View.OnTouchListener{
    private FrameLayout vPos,vRoot;
    private int _yDelta;
    private SettingsPreference preference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_poition_picker, container, false);
        vPos = v.findViewById(R.id.position_layout);
        vRoot = v.findViewById(R.id.root_pos_layout);
        preference = new SettingsPreference(getActivity());
        vPos.setOnTouchListener(this);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) vPos.getLayoutParams();
        params.topMargin = preference.getPositionOfMessage();
        vPos.setLayoutParams(params);
        return v;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                FrameLayout.LayoutParams layoutParamsSave = (FrameLayout.LayoutParams) v
                        .getLayoutParams();
                preference.saveMessagePos(layoutParamsSave.topMargin);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v
                        .getLayoutParams();
                layoutParams.topMargin = Y - _yDelta;
                v.setLayoutParams(layoutParams);
                break;
        }
        vRoot.invalidate();
        return true;
    }

}
