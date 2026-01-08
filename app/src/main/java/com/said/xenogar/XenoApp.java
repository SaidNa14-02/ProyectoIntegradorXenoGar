package com.said.xenogar;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class XenoApp extends Application {
    // Al usar Hilt, ya no necesitas este método.
    // Hilt se encargará de proveer la instancia de la base de datos
    // donde sea que la inyectes.
}
