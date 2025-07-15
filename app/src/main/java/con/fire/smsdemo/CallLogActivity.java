package con.fire.smsdemo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import con.fire.smsdemo.databinding.ActivityCalllogBinding;

public class CallLogActivity extends AppCompatActivity {


    ActivityCalllogBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalllogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
