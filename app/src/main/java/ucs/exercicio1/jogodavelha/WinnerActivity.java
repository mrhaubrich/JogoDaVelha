package ucs.exercicio1.jogodavelha;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);
        TextView whoWon = findViewById(R.id.txtWhoWon);
        TextView txtVencedor = findViewById(R.id.txtVencedor);
        Button btnVoltar = findViewById(R.id.btnVoltar);
        ImageView imgWinner = findViewById(R.id.imgWinner);
        String vencedor = getIntent().getStringExtra("vencedor");
        Bitmap imagem = getIntent().getParcelableExtra("imagem");
        if(imagem == null) imgWinner.setVisibility(View.INVISIBLE);
        else {
            imgWinner.setVisibility(View.VISIBLE);
            imgWinner.setImageBitmap(imagem);
        }
        if(vencedor.equals("Deu velha!")) txtVencedor.setVisibility(View.INVISIBLE);
        else txtVencedor.setVisibility(View.VISIBLE);
        whoWon.setText(vencedor);
        btnVoltar.setOnClickListener(view -> {
            finish();
        });
    }
}