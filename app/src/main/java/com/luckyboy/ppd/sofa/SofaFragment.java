package com.luckyboy.ppd.sofa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luckyboy.jetpacklearn.databinding.FragmentFreshBinding;
import com.luckyboy.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/sofa", asStarter = true)
public class SofaFragment extends Fragment {


    private FragmentFreshBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFreshBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
