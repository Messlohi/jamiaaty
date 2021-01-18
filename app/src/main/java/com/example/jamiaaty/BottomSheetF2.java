package com.example.jamiaaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetF2 extends BottomSheetDialogFragment {

    CardView  feturedQuestions, myQuestions;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = getLayoutInflater().inflate(R.layout.bottomsheet_f2,null);
        feturedQuestions = view.findViewById(R.id.related_f2);
        myQuestions = view.findViewById(R.id.your_Question_f2);

        feturedQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RelatedQuestionsActivity.class));
            }
        });

        myQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UserQuestionsActivity.class));
            }
        });
        return  view;
    }
}
