package ucs.exercicio1.jogodavelha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class WinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);
        TextView whoWon = findViewById(R.id.txtWhoWon);
        Button btnVoltar = findViewById(R.id.btnVoltar);
        String vencedor = getIntent().getStringExtra("vencedor");
        whoWon.setText(vencedor);
        btnVoltar.setOnClickListener(view -> {
            finish();
        });
    }
}