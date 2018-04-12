package com.vallsoft.num.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.squareup.picasso.Picasso;
import com.vallsoft.num.data.database.SettingsPreference;

import com.vallsoft.num.R;


public class PrivacyPolicyActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView vAcceptPrivacy;
    private SettingsPreference preference;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = new SettingsPreference(this);
        if (preference.isPrivacyAccepted()){
          goToMain();
        } else {
            setContentView(R.layout.activity_privacy_policy);
            ImageView vTel = findViewById(R.id.num_text);
            ImageView vNum = findViewById(R.id.tel);
            Picasso.with(this)
                    .load(R.drawable.num)
                    .into(vTel);
            Picasso.with(this)
                    .load(R.drawable.tel)
                    .into(vNum);
            vAcceptPrivacy = findViewById(R.id.accept_privacy);
            vAcceptPrivacy.setOnClickListener(this);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accept_privacy:
                preference.savePrivacyStatus(true);
                goToMain();
                break;
        }
    }
    private void goToMain(){
        Intent mainActivity = new Intent(this,MainActivity.class);
        finish();
        startActivity(mainActivity);
    }
}
