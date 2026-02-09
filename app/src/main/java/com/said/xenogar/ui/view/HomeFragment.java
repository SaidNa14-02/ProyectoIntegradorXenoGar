package com.said.xenogar.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.said.xenogar.R;
import com.said.xenogar.data.remote.PdfApiService;
import com.said.xenogar.databinding.HomeFragmentBinding;
import com.said.xenogar.util.SummaryGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeFragmentBinding binding;
    private ExecutorService executorService;

    @Inject
    SummaryGenerator summaryGenerator;

    @Inject
    PdfApiService pdfApiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        binding.goToCultivosButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CultivoListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.generateSummaryButton.setOnClickListener(v -> {


            executorService.execute(() -> {

                try {
                    String jsonSummary = summaryGenerator.generateWeeklySummaryJson();


                    if (jsonSummary != null && !jsonSummary.isEmpty()) {
                        // Enviar JSON a la API
                        Log.d(TAG, "Sending JSON to API...");
                        RequestBody body = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonSummary
                        );

                        Call<ResponseBody> call = pdfApiService.generatePdfFromJson(body);
                        Response<ResponseBody> response = call.execute();

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "API response successful, saving PDF...");
                            // Guardar el PDF
                            byte[] pdfBytes = response.body().bytes();
                            File pdfFile = savePdfToFile(pdfBytes);

                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(),
                                        "PDF generado exitosamente",
                                        Toast.LENGTH_LONG).show();
                                // Abrir el PDF
                                openPdf(pdfFile);
                            });
                        } else {
                            Log.e(TAG, "API error: " + response.code() + " - " + response.message());
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(),
                                            "Error en la API: " + response.code(),
                                            Toast.LENGTH_LONG).show()
                            );
                        }
                    } else {
                        Log.e(TAG, "JSON is null or empty");
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(),
                                        "Error: No se pudo generar el resumen JSON",
                                        Toast.LENGTH_LONG).show()
                        );
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error calling API or generating PDF", e);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
                }
            });
        });
    }

    private File savePdfToFile(byte[] pdfBytes) throws IOException {
        File pdfDir = new File(requireContext().getExternalFilesDir(null), "pdfs");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "resumen_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(pdfDir, fileName);

        FileOutputStream fos = new FileOutputStream(pdfFile);
        fos.write(pdfBytes);
        fos.close();

        Log.d(TAG, "PDF saved to: " + pdfFile.getAbsolutePath());
        return pdfFile;
    }

    private void openPdf(File pdfFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri pdfUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    pdfFile
            );
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF", e);
            Toast.makeText(requireContext(),
                    "No se pudo abrir el PDF. Archivo guardado en: " + pdfFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}