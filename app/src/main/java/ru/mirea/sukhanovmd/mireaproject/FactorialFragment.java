package ru.mirea.sukhanovmd.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class FactorialFragment extends Fragment {

    private EditText numberEditText;
    private Button calculateButton;
    private TextView factorialResultTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_factorial, container, false);
        numberEditText = rootView.findViewById(R.id.numberEditText);
        calculateButton = rootView.findViewById(R.id.calculateButton);
        factorialResultTextView = rootView.findViewById(R.id.factorialResultTextView);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateFactorial();
            }
        });
        return rootView;
    }

    private void calculateFactorial() {
        String numberString = numberEditText.getText().toString();
        if (numberString.isEmpty()) {
            Toast.makeText(requireContext(), "Введите число", Toast.LENGTH_SHORT).show();
            return;
        }
        int number = Integer.parseInt(numberString);

        Data inputData = new Data.Builder().putInt(FactorialCalculationWorker.INPUT_KEY, number).build();

        OneTimeWorkRequest factorialCalculationWorkRequest = new OneTimeWorkRequest.Builder(FactorialCalculationWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(requireContext()).enqueue(factorialCalculationWorkRequest);

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(factorialCalculationWorkRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                        Data outputData = workInfo.getOutputData();
                        long factorialResult = outputData.getLong(FactorialCalculationWorker.RESULT_KEY, -1);

                        factorialResultTextView.setText("Факториал равен " + factorialResult);
                    }
                });
    }
}