package ru.mirea.sukhanovmd.mireaproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {

    private ListView fileListView;
    private List<String> fileList;
    private ArrayAdapter<String> adapter;
    private FloatingActionButton fabCreateFile;
    private TextView emptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file, container, false);
        fileListView = rootView.findViewById(R.id.file_list_view);
        fabCreateFile = rootView.findViewById(R.id.fab_create_file);
        emptyTextView = rootView.findViewById(R.id.empty_text_view);
        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fileList);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = fileList.get(position);
                openFileEditFragment(fileName);
            }
        });
        loadFileList();
        fabCreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileCreateFragment();
            }
        });

        return rootView;
    }
    private void loadFileList() {
        File[] files = requireContext().getFilesDir().listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                fileList.add(file.getName());
            }
            adapter.notifyDataSetChanged();
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    private void openFileEditFragment(String fileName) {
        NavController navController = Navigation.findNavController(requireView());
        Bundle bundle = new Bundle();
        bundle.putString("fileName", fileName);
        navController.navigate(R.id.action_fileFragment_to_fragmentFileEdit, bundle);
    }

    private void openFileCreateFragment() {
        FileCreateFragment fileCreateFragment = new FileCreateFragment();
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.fileCreateFragment);
    }
}