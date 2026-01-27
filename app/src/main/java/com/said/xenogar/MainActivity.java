package com.said.xenogar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.said.xenogar.databinding.ActivityMainBinding;
import com.said.xenogar.ui.view.CultivoListFragment;
import com.said.xenogar.ui.view.HomeFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}
