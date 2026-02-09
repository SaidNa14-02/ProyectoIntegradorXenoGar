package com.said.xenogar.util;

import android.util.Log; // Import added
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.repository.AgroRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject; // Assuming Hilt injection will be used for this class
import javax.inject.Singleton;

@Singleton
public class SummaryGenerator {

    private static final String TAG = "SummaryGenerator"; // Tag added for logging

    private final AgroRepository repository;

    @Inject // Hilt injects AgroRepository
    public SummaryGenerator(AgroRepository repository) {
        this.repository = repository;
    }

    /**
     * Calculates the start of the current week (Monday 00:00:00).
     *
     * @return Milliseconds representing the start of the current week.
     */
    private long getStartOfWeekMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Set Monday as the first day of the week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Calculates the end of the current week (Sunday 23:59:59).
     *
     * @return Milliseconds representing the end of the current week.
     */
    private long getEndOfWeekMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Set Monday as the first day of the week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Generates a JSON summary of activities for the current week, grouped by cultivation.
     *
     * @return A JSON string representing the weekly summary, or null if an error occurs.
     */
    public String generateWeeklySummaryJson() {
        Log.d(TAG, "Generating weekly summary JSON.");
        try {
            long startOfWeek = getStartOfWeekMillis();
            long endOfWeek = getEndOfWeekMillis();

            Log.d(TAG, "Current week: " + new Date(startOfWeek) + " to " + new Date(endOfWeek));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String summaryDateRange = "Reporte Semanal: " + dateFormat.format(new Date(startOfWeek)) + " - " + dateFormat.format(new Date(endOfWeek));

            List<Actividad> weeklyActivities = repository.getActividadesInDateRangeSync(startOfWeek, endOfWeek);
            List<Cultivo> allCultivos = repository.getAllCultivosSync();

            if (weeklyActivities == null || allCultivos == null) {
                Log.e(TAG, "Failed to retrieve activities or cultivations from repository.");
                return null;
            }

            Log.d(TAG, "Retrieved " + weeklyActivities.size() + " weekly activities and " + allCultivos.size() + " cultivations.");

            Map<Long, Cultivo> cultivoMap = new HashMap<>();
            for (Cultivo cultivo : allCultivos) {
                cultivoMap.put(cultivo.getId(), cultivo);
            }

            Map<Long, JSONArray> actividadesByCultivo = new HashMap<>();
            for (Actividad actividad : weeklyActivities) {
                JSONObject actividadJson = new JSONObject();
                actividadJson.put("id", actividad.getId());
                actividadJson.put("tipoActividad", actividad.getActividad().toString());
                actividadJson.put("fecha", dateFormat.format(new Date(actividad.getFecha())));
                actividadJson.put("estado", actividad.getEstado().toString());

                if (!actividadesByCultivo.containsKey(actividad.getCultivoId())) {
                    actividadesByCultivo.put(actividad.getCultivoId(), new JSONArray());
                }
                actividadesByCultivo.get(actividad.getCultivoId()).put(actividadJson);
            }

            JSONArray cultivosArray = new JSONArray();
            for (Map.Entry<Long, JSONArray> entry : actividadesByCultivo.entrySet()) {
                Cultivo cultivo = cultivoMap.get(entry.getKey());
                if (cultivo != null) {
                    JSONObject cultivoJson = new JSONObject();
                    cultivoJson.put("nombre", cultivo.getNombre());
                    cultivoJson.put("tipo", cultivo.getTipo().toString());
                    cultivoJson.put("actividades", entry.getValue());
                    cultivosArray.put(cultivoJson);
                }
            }

            JSONObject summaryJson = new JSONObject();
            summaryJson.put("summaryDateRange", summaryDateRange);
            summaryJson.put("cultivos", cultivosArray);

            String finalJson = summaryJson.toString(2);
            Log.d(TAG, "Generated JSON Summary:\n" + finalJson);
            return finalJson;
        } catch (JSONException e) {
            Log.e(TAG, "Error generating JSON summary: " + e.getMessage(), e);
            return null;
        }
    }
}
