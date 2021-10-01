package ucs.exercicio1.jogodavelha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE_JOGADOR1 = 1;
    static final int REQUEST_IMAGE_CAPTURE_JOGADOR2 = 2;
    ImageButton[] positionArray = new ImageButton[9];
    Button btnClear;
    ImageButton btnJogador1, btnJogador2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClear = findViewById(R.id.btnClear);
        instanciaElementos();

    }
    private boolean ganhador(View view){
        // if posicao for impar ou 4 verificar os lados
        // senao verificar os posicao +- 1 e posicao +- 2
        // posicao +- 3 e posicao +- 6
        // posicao +- 4 e posicao +- 8
        return false;
    }
    private void instanciaElementos(){
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
        btnJogador1 = findViewById(R.id.imgPlayer1);
        btnJogador2 = findViewById(R.id.imgPlayer2);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        btnJogador1.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_JOGADOR1);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},666);
        });
        btnJogador2.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_JOGADOR2);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},666);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE_JOGADOR1:
                Log.i("TAG", "onActivityResult: JOGADOR1");
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    btnJogador1.setImageBitmap(imageBitmap);
                }
                break;
            case REQUEST_IMAGE_CAPTURE_JOGADOR2:
                Log.i("TAG", "onActivityResult: JOGADOR2");
                if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                    btnJogador2.setImageBitmap(imageBitmap);
                }
                break;
        }
    }
}