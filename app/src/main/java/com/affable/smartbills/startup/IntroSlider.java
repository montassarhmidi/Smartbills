package com.affable.smartbills.startup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

import org.jetbrains.annotations.Nullable;

public class IntroSlider extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change Indicator Color
        setIndicatorColor(getResources().getColor(R.color.blue_500), getResources().getColor(R.color.grey_40));

        //change buttons color
        setColorSkipButton(getResources().getColor(R.color.grey_90));
        setColorDoneText(getResources().getColor(R.color.grey_90));
        setBackArrowColor(getResources().getColor(R.color.grey_90));
        setNextArrowColor(getResources().getColor(R.color.grey_90));

        // Hide/Show the status Bar
        showStatusBar(true);
        // Control the status bar color
        setStatusBarColor(Color.BLUE);
        setStatusBarColorRes(R.color.blue_500);

        addSlide(AppIntroFragment.newInstance(
                "Completely offline",
                "No internet connection required, works entirely offline.",
                R.drawable.ic_offline,
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.blue_700),
                getResources().getColor(R.color.grey_90),
                R.font.alegreya_sans
        ));

        addSlide(AppIntroFragment.newInstance(
                "Create Backup",
                "Create entire data backup in one click, very easy.",
                R.drawable.ic_backup,
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.blue_700),
                getResources().getColor(R.color.grey_90),
                R.font.alegreya_sans
        ));

        addSlide(AppIntroFragment.newInstance(
                "Export Excel",
                "Export all data in excel format by just one click.",
                R.drawable.ic_generate_excel,
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.blue_700),
                getResources().getColor(R.color.grey_90),
                R.font.alegreya_sans
        ));

        addSlide(AppIntroFragment.newInstance(
                "Invoice PDF",
                "Create Invoices in pdf format.",
                R.drawable.ic_generate_pdf,
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.blue_700),
                getResources().getColor(R.color.grey_90),
                R.font.alegreya_sans
        ));

        addSlide(AppIntroFragment.newInstance(
                "Business Analytics",
                "Get a overall idea of business with interactive charts.",
                R.drawable.ic_analitics,
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.blue_700),
                getResources().getColor(R.color.grey_90),
                R.font.alegreya_sans
        ));


    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        nextPage();

    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        nextPage();

    }

    private void nextPage() {
        Intent intent = new Intent(IntroSlider.this, SetAccountInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
