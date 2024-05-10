package ru.mirea.sukhanovmd.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private LinearLayout staticInfoLayout, editLayout;
    private TextView nameTextView, dobTextView, emailTextView, phoneTextView;
    private EditText nameEditText, dobEditText, emailEditText, phoneEditText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        staticInfoLayout = root.findViewById(R.id.staticInfoLayout);
        editLayout = root.findViewById(R.id.editLayout);
        nameTextView = root.findViewById(R.id.nameTextView);
        dobTextView = root.findViewById(R.id.dobTextView);
        emailTextView = root.findViewById(R.id.emailTextView);
        phoneTextView = root.findViewById(R.id.phoneTextView);
        nameEditText = root.findViewById(R.id.nameEditText);
        dobEditText = root.findViewById(R.id.dobEditText);
        emailEditText = root.findViewById(R.id.emailEditText);
        phoneEditText = root.findViewById(R.id.phoneEditText);
        Button editButton = root.findViewById(R.id.editButton);
        Button saveButton = root.findViewById(R.id.saveButton);

        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        loadProfile();

        editButton.setOnClickListener(v -> {
            staticInfoLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
        });

        saveButton.setOnClickListener(v -> {
            saveProfile();
            staticInfoLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
            loadProfile();
        });

        return root;
    }

    private void loadProfile() {
        String name = sharedPreferences.getString("name", "");
        String dob = sharedPreferences.getString("dob", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");

        nameTextView.setText(name);
        dobTextView.setText(dob);
        emailTextView.setText(email);
        phoneTextView.setText(phone);

        nameEditText.setText(name);
        dobEditText.setText(dob);
        emailEditText.setText(email);
        phoneEditText.setText(phone);
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", nameEditText.getText().toString());
        editor.putString("dob", dobEditText.getText().toString());
        editor.putString("email", emailEditText.getText().toString());
        editor.putString("phone", phoneEditText.getText().toString());
        editor.apply();
    }
}