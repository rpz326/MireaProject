package ru.mirea.sukhanovmd.mireaproject;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NoteFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final String TAG = "NoteFragment";
    private static final String PREFS_NAME = "NotePrefs";
    private static final String KEY_NOTE_TEXT = "noteText";
    private static final String KEY_IMAGE_PATH = "imagePath";

    private Button deleteNoteButton;

    private EditText noteEditText;
    private ImageView photoImageView;
    private TextView noteTextView;

    private ActivityResultLauncher<Void> takePicturePreviewLauncher;
    private String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note, container, false);

        noteEditText = root.findViewById(R.id.noteEditText);
        Button takePhotoButton = root.findViewById(R.id.takePhotoButton);
        Button createNoteButton = root.findViewById(R.id.createNoteButton);
        photoImageView = root.findViewById(R.id.photoImageView);
        noteTextView = root.findViewById(R.id.noteTextView);
        deleteNoteButton = root.findViewById(R.id.deleteNoteButton);

        loadNote();

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
                    Log.d(TAG, "Запрос разрешений на камеру");
                } else {
                    Log.d(TAG, "Разрешения на камеру уже предоставлены. Запуск камеры");
                    dispatchTakePicturePreviewIntent();
                }
            }
        });

        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoImageView.getDrawable() != null && noteEditText.getText().toString().trim().length() > 0) {
                    noteTextView.setVisibility(View.VISIBLE);
                    noteTextView.setText(noteEditText.getText().toString());
                    saveNote();
                    deleteNoteButton.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Заметка создана");
                } else {
                    Toast.makeText(requireContext(), "Вы не ввели текст или не прикрепили снимок", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        if (noteTextView.getText().toString().isEmpty() && photoImageView.getDrawable() == null) {
            deleteNoteButton.setVisibility(View.GONE);
        }

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        takePicturePreviewLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                if (result != null) {
                    photoImageView.setImageBitmap(result);
                    photoImageView.setVisibility(View.VISIBLE);
                    saveImage(result);
                } else {
                    Log.d(TAG, "Снимок не сделан");
                }
            }
        });
    }

    private void dispatchTakePicturePreviewIntent() {
        takePicturePreviewLauncher.launch(null);
        Log.d(TAG, "Запуск камеры");
    }

    private void saveNote() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOTE_TEXT, noteTextView.getText().toString());
        editor.putString(KEY_IMAGE_PATH, currentPhotoPath);
        editor.apply();
    }

    private void loadNote() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String noteText = sharedPreferences.getString(KEY_NOTE_TEXT, null);
        String imagePath = sharedPreferences.getString(KEY_IMAGE_PATH, null);

        if (noteText != null && imagePath != null) {
            noteTextView.setText(noteText);
            noteTextView.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                photoImageView.setImageBitmap(bitmap);
                photoImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        File storageDir = requireActivity().getFilesDir();
        File imageFile = new File(storageDir, "note_image.jpg");
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            currentPhotoPath = imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при сохранении картинки", e);
        }
    }

    private void deleteNote() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_NOTE_TEXT);
        editor.remove(KEY_IMAGE_PATH);
        editor.apply();

        noteTextView.setText("");
        noteTextView.setVisibility(View.GONE);
        photoImageView.setImageDrawable(null);
        photoImageView.setVisibility(View.GONE);

        deleteNoteButton.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на камеру предоставлено");
                dispatchTakePicturePreviewIntent();
            } else {
                Toast.makeText(requireContext(), "Разрешение на камеру не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}