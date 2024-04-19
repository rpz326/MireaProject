package ru.mirea.sukhanovmd.mireaproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class FactorialCalculationWorker extends Worker {
    private static final String TAG = "FactorialCalculationWorker";
    public static final String INPUT_KEY = "input_number";
    public static final String RESULT_KEY = "factorial_result";

    public FactorialCalculationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            int number = getInputData().getInt(INPUT_KEY, 0);

            long factorial = calculateFactorial(number);

            Log.d(TAG, "Факториал числа " + number + " равен " + factorial);

            Data outputData = new Data.Builder().putLong(RESULT_KEY, factorial).build();
            return Result.success(outputData);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка вычисления", e);
            return Result.failure();
        }
    }

    private long calculateFactorial(int n) {
        long factorial = 1;
        for (int i = 1; i <= n; ++i) {
            factorial *= i;
        }
        return factorial;
    }
}
