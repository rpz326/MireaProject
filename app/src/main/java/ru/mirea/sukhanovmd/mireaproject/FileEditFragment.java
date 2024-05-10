package ru.mirea.sukhanovmd.mireaproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileEditFragment extends Fragment {

    private EditText fileContentEditText;
    private Button saveButton;
    private Button uppercaseButton;
    private Button lowercaseButton;

    private String fileName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_edit, container, false);

        fileContentEditText = rootView.findViewById(R.id.file_content_edit_text);
        saveButton = rootView.findViewById(R.id.save_button);
        uppercaseButton = rootView.findViewById(R.id.uppercase_button);
        lowercaseButton = rootView.findViewById(R.id.lowercase_button);

        if (getArguments() != null) {
            fileName = getArguments().getString("fileName");
            loadFileContent();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFileContent();
            }
        });
        uppercaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToUppercase();
            }
        });
        lowercaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToLowercase();
            }
        });

        return rootView;
    }

    private void loadFileContent() {
        try {
            FileInputStream inputStream = requireContext().openFileInput(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String fileContent = new String(buffer);
            fileContentEditText.setText(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка загрузки текста в файле", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileContent() {
        String fileContent = fileContentEditText.getText().toString();
        try {
            FileOutputStream outputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            Toast.makeText(requireContext(), "Сохранено!", Toast.LENGTH_SHORT).show();
            openFileFragment();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.fileFragment);
    }

    private void convertToUppercase() {
        String content = fileContentEditText.getText().toString();
        String uppercaseContent = content.toUpperCase();
        fileContentEditText.setText(uppercaseContent);
    }

    private void convertToLowercase() {
        String content = fileContentEditText.getText().toString();
        String lowercaseContent = content.toLowerCase();
        fileContentEditText.setText(lowercaseContent);
    }
}