package com.dummy.dummyphoneprakash.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dummy.dummyphoneprakash.R;

public class OnboardingFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    
    private int position;
    
    public static OnboardingFragment newInstance(int position) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        // Select the appropriate layout based on position
        int layoutResId;
        switch (position) {
            case 0:
                layoutResId = R.layout.onboarding_screen1;
                break;
            case 1:
                layoutResId = R.layout.onboarding_screen2;
                break;
            case 2:
                layoutResId = R.layout.onboarding_screen3;
                break;
            case 3:
                layoutResId = R.layout.onboarding_screen4;
                break;
            default:
                layoutResId = R.layout.onboarding_screen1;
                break;
        }
        
        return inflater.inflate(layoutResId, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Each onboarding screen will have its own implementation in the layout files
        // This method can be used to set up any dynamic content or listeners if needed
    }
}