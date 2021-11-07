package com.affable.smartbills.invoice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.affable.smartbills.R;
import com.affable.smartbills.invoice.adapter.ViewPagerAdapter;
import com.affable.smartbills.invoice.order_fragments.CanceledOrderFragment;
import com.affable.smartbills.invoice.order_fragments.CompletedOrderFragment;
import com.affable.smartbills.invoice.order_fragments.PendingOrderFragment;
import com.google.android.material.tabs.TabLayout;


public class InvoiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views by id
        ViewPager2 viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        // attach tablayout with viewpager

        ViewPagerAdapter adapter =
                new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());

        // add your fragments
        adapter.addFrag(new PendingOrderFragment());
        adapter.addFrag(new CompletedOrderFragment());
        adapter.addFrag(new CanceledOrderFragment());

        // set adapter on viewpager
        viewPager.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));
        tabLayout.addTab(tabLayout.newTab().setText("Canceled"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //type your code here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //type your code here
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }
}