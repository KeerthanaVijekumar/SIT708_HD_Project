package com.keerthana.ticketmateai.ui.script;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.keerthana.ticketmateai.databinding.FragmentScriptGeneratorBinding;

public class ScriptGeneratorFragment extends Fragment {

    private FragmentScriptGeneratorBinding binding;
    private ScriptGeneratorViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScriptGeneratorBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this)
                .get(ScriptGeneratorViewModel.class);

        // Observe script state
        viewModel.getScriptState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case IDLE:
                    binding.loadingState.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.loadingState.setVisibility(View.VISIBLE);
                    binding.btnGenerate.setEnabled(false);
                    binding.cardOutput.setVisibility(View.GONE);
                    binding.cardUnsafe.setVisibility(View.GONE);
                    break;
                case DONE:
                    binding.loadingState.setVisibility(View.GONE);
                    binding.btnGenerate.setEnabled(true);
                    binding.cardOutput.setVisibility(View.VISIBLE);
                    binding.cardUnsafe.setVisibility(View.GONE);
                    break;
                case ERROR:
                    binding.loadingState.setVisibility(View.GONE);
                    binding.btnGenerate.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Generation failed. Check model is installed.",
                            Toast.LENGTH_LONG).show();
                    break;
                case UNSAFE:
                    binding.loadingState.setVisibility(View.GONE);
                    binding.btnGenerate.setEnabled(true);
                    binding.cardUnsafe.setVisibility(View.VISIBLE);
                    break;
            }
        });

        // Observe generated script
        viewModel.getGeneratedScript().observe(getViewLifecycleOwner(), script -> {
            if (script != null) {
                binding.tvScriptOutput.setText(script);
            }
        });

        // Generate button
        binding.btnGenerate.setOnClickListener(v -> {
            String task = binding.etTaskDescription.getText() != null
                    ? binding.etTaskDescription.getText().toString().trim()
                    : "";

            if (task.isEmpty()) {
                binding.etTaskDescription.setError("Please describe your task");
                return;
            }
            viewModel.generateScript(task);
        });

        // Approve & Copy button
        binding.btnApproveScript.setOnClickListener(v -> {
            String script = binding.tvScriptOutput.getText().toString();
            if (!script.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager)
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("PowerShell Script", script);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),
                        "Script copied to clipboard",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}