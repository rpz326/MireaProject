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

import java.io.FileOutputStream;

public class FileCreateFragment extends Fragment {

    private EditText fileNameEditText;
    private EditText fileContentEditText;
    private Button uppercaseButton;
    private Button lowercaseButton;
    private Button createButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_create, container, false);

        fileNameEditText = rootView.findViewById(R.id.file_name_edit_text);
        fileContentEditText = rootView.findViewById(R.id.file_content_edit_text);
        uppercaseButton = rootView.findViewById(R.id.uppercase_button);
        lowercaseButton = rootView.findViewById(R.id.lowercase_button);
        createButton = rootView.findViewById(R.id.create_button);

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
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile();
            }
        });

        return rootView;
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

    private void createFile() {
        String fileName = fileNameEditText.getText().toString();
        String fileContent = fileContentEditText.getText().toString();
        try {
            FileOutputStream outputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            Toast.makeText(requireContext(), "Файл создан!", Toast.LENGTH_SHORT).show();
            openFileFragment();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка создания файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileFragment() {
        FileFragment fileFragment = new FileFragment();
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.fileFragment);
    }
}