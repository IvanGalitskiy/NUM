package com.vallsoft.num.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vallsoft.num.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private MainActivity activity;
    private ImageView avatar_image;
    private TextView phone_number_view;
    private Button  edit_btn, save_btn, cancel_btn;
    private EditText name_field, address_field;
    private AutoCompleteTextView country_field, region_field, operator_field, category_field, group_field;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        activity = (MainActivity) getActivity();

        avatar_image = v.findViewById(R.id.avatar_image_view);

        // Buttons initialization
        edit_btn = v.findViewById(R.id.profile_edit_btn);
        cancel_btn = v.findViewById(R.id.profile_cancel_btn);
        save_btn = v.findViewById(R.id.profile_save_btn);

        // Fields initializations
        name_field = v.findViewById(R.id.name_field);
        address_field = v.findViewById(R.id.address_field);
        country_field = v.findViewById(R.id.country_field);
        region_field = v.findViewById(R.id.region_field);
        operator_field = v.findViewById(R.id.operator_field);
        category_field = v.findViewById(R.id.category_field);
        group_field = v.findViewById(R.id.group_field);

        phone_number_view = v.findViewById(R.id.phone_number_view);

        edit_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        avatar_image.setOnClickListener(this);
        setFieldsInputType(InputType.TYPE_NULL);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }




    @Override
    public void onClick(View view) {
        System.out.println("Clicked some button");
        switch (view.getId()){
            case R.id.profile_edit_btn:
                editBtnClicked(view);
                break;
            case R.id.profile_save_btn:
                saveBtnClicked(view);
                break;
            case R.id.profile_cancel_btn:
                cancelBtnClicked(view);
                break;
            case R.id.avatar_image_view:
                changeImage();
                break;
        }
    }

    private void editBtnClicked(View view){
        save_btn.setVisibility(View.VISIBLE);
        cancel_btn.setVisibility(View.VISIBLE);
        edit_btn.setVisibility(View.INVISIBLE);
        setFieldsInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        // Код обработки кнопки
    }

    private void cancelBtnClicked(View view){
        save_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);
        edit_btn.setVisibility(View.VISIBLE);
        setFieldsInputType(InputType.TYPE_NULL);
        hideKeyboard(view);

        // Код обработки кнопки
    }

    private void saveBtnClicked(View view){
        save_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);
        edit_btn.setVisibility(View.VISIBLE);
        setFieldsInputType(InputType.TYPE_NULL);
        hideKeyboard(view);

        // Код обработки кнопки
    }

    private void changeImage(){
        if(edit_btn.getVisibility() == View.INVISIBLE){
            Toast.makeText(activity,"Sorry this function is unavailable yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFieldsInputType(int text_input_type){
        name_field.setInputType(text_input_type);
        address_field.setInputType(text_input_type);
        country_field.setInputType(text_input_type);
        region_field.setInputType(text_input_type);
        operator_field.setInputType(text_input_type);
        category_field.setInputType(text_input_type);
        group_field.setInputType(text_input_type);
    }

    private void hideKeyboard(View view){
        try {
            ((InputMethodManager)activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        catch (NullPointerException e){
            Log.d("msg", "Cannot hide keyboard");
        }

    }
}
