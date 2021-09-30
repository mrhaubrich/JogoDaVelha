package ucs.exercicio1.jogodavelha;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton[] positionArray = new ImageButton[9];
    Button btnClear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClear = findViewById(R.id.btnClear);
        instanciaPosition();
    }
    private boolean ganhador(View view){
        // if posicao for impar ou 4 verificar os lados
        // senao verificar os posicao +- 1 e posicao +- 2
        // posicao +- 3 e posicao +- 6
        // posicao +- 4 e posicao +- 8
        return false;
    }
    private void instanciaPosition(){
        for (int i = 0; i<9; i++){
            // pega o id do xml
            String positionID = "imgPos" + i;
            int resID = getResources().getIdentifier(positionID, "id", getPackageName());
            // coloca no array
            positionArray[i] = findViewById(resID);
            String text = "S" + i;
            // cria o listener de click
            positionArray[i].setOnClickListener(view -> {
                // verificar ganhador();
                ImageButton position = (ImageButton) view;
                position.setBackgroundResource(R.mipmap.dog_foreground);
                btnClear.setText(text);
            });
        }
    }
}