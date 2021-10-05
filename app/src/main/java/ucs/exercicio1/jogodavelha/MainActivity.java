package ucs.exercicio1.jogodavelha;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    int currentPlayer = 1;
    ImageButton[][] positionMatrix = new ImageButton[3][3];
    int[][] tabuleiro = new int[3][3];
    Button btnClear;
    ImageButton btnJogador1, btnJogador2;
    int[] imageButtonPositionOnMatrix = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClear = findViewById(R.id.btnClear);
        instanciaElementos();

    }
    ActivityResultLauncher<Intent> cameraP1Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    assert result.getData() != null;
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    Log.i("TAG", "onActivityResult: JOGADOR1");
                    if (resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        btnJogador1.setImageBitmap(imageBitmap);
                    }
                }
            });
    ActivityResultLauncher<Intent> cameraP2Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    assert result.getData() != null;
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    Log.i("TAG", "onActivityResult: JOGADOR2");
                    if (resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        btnJogador2.setImageBitmap(imageBitmap);
                    }
                }
            });


    //region Métodos criados
    private int[] getImageButtonPositionOnMatrix(ImageButton imgBtn){
        for(int i = 0 ; i < 3 ; i++){
            for(int j = 0 ; j < 3 ; j++){
                if(positionMatrix[i][j] == imgBtn){
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    private boolean verificaHorizontal(ImageButton imgBtn){
        if(imageButtonPositionOnMatrix == null)
            imageButtonPositionOnMatrix = getImageButtonPositionOnMatrix(imgBtn);
        if(imageButtonPositionOnMatrix == null) return false;
        int i = imageButtonPositionOnMatrix[0];
        for(int j = 0 ; j < 3 ; j++){
            if(tabuleiro[i][j] != currentPlayer) return false;
        }
        return true;
    }
    private boolean verificaVertical(ImageButton imgBtn){
        if(imageButtonPositionOnMatrix == null)
            imageButtonPositionOnMatrix = getImageButtonPositionOnMatrix(imgBtn);
        if(imageButtonPositionOnMatrix == null) return false;
        int j = imageButtonPositionOnMatrix[1];
        for(int i = 0 ; i < 3 ; i++){
            if(tabuleiro[i][j] != currentPlayer) return false;
        }
        return true;
    }
    private boolean verificaDiagonal(ImageButton imgBtn){
        if(imageButtonPositionOnMatrix == null)
            imageButtonPositionOnMatrix = getImageButtonPositionOnMatrix(imgBtn);
        if(imageButtonPositionOnMatrix == null) return false;
        if(Arrays.stream(imageButtonPositionOnMatrix).sum() % 2 == 1)
            return false;
        switch (imageButtonPositionOnMatrix[0] + imageButtonPositionOnMatrix[1]){
            case 2:
                if(imageButtonPositionOnMatrix[0] == 1){
                    if(!diagonalDecrescente() && !diagonalCrescente()) return false;
                }
                else{
                    if(!diagonalCrescente()) return false;
                }
                break;
            case 4:
            case 0:
                if(!diagonalDecrescente()) return false;
                break;
        }
        return true;
    }
    private boolean diagonalCrescente(){
        int j = 2;
        for(int i = 0; i < 3 ; i++){
            if(tabuleiro[i][j] != currentPlayer) return false;
            j--;
        }
        return true;
    }
    private boolean diagonalDecrescente(){
        for(int i = 0; i < 3 ; i++){
            if(tabuleiro[i][i] != currentPlayer) return false;
        }
        return true;
    }
    private boolean ganhador(View view){
        return verificaDiagonal((ImageButton) view) || verificaVertical((ImageButton) view) || verificaHorizontal((ImageButton) view);
    }
    private void instanciaElementos() {
        btnClear.setOnClickListener(view -> clear());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // region Somador para pegar posicao
                int somador;
                switch (i) {
                    case 0:
                        somador = i + j;
                        break;
                    case 1:
                        somador = i + j + 2;
                        break;
                    case 2:
                        somador = i + j + 4;
                        break;
                    default:
                        somador = 0;
                }
                // endregion
                tabuleiro[i][j] = 0;
                // pega o id do xml
                String positionID = "imgPos" + somador;
                int resID = getResources().getIdentifier(positionID, "id", getPackageName());
                // coloca no array
                positionMatrix[i][j] = findViewById(resID);
                String text = "S" + somador;
                // cria o listener de click
                int finalI = i;
                int finalJ = j;
                positionMatrix[i][j].setOnClickListener(view -> {
                    // verificar ganhador();
                    if (tabuleiro[finalI][finalJ] != 0) return;
                    ImageButton position = (ImageButton) view;
                    position.setImageResource(currentPlayer == 1 ? R.drawable.jogador1 : R.drawable.jogador2_btn_foreground);
                    tabuleiro[finalI][finalJ] = currentPlayer;
                    //Log.d("tabuleiro", "tabuleiro: ");
                    if(ganhador(view)){
                        abreActivityGanhador(false);
                        clear();
                        return;
                    }
                    if(isVelha()){
                        abreActivityGanhador(true);
                        clear();
                        return;
                    }
                    imageButtonPositionOnMatrix = null;
                    alternarPlayer();
                });
            }
        }
        btnJogador1 = findViewById(R.id.imgPlayer1);
        btnJogador2 = findViewById(R.id.imgPlayer2);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        btnJogador1.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraP1Launcher.launch(cameraIntent);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 666);
        });
        btnJogador2.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraP2Launcher.launch(cameraIntent);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 666);
        });
    }
    private void alternarPlayer(){
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }
    private void abreActivityGanhador(boolean pVelha){
        Intent winnerIntent = new Intent(this, WinnerActivity.class);
        String winner = !pVelha ? "Jogador " + currentPlayer : "Deu velha!";
        winnerIntent.putExtra("vencedor", winner);
        startActivity(winnerIntent);
    }
    private void clear(){
        currentPlayer = 1;
        tabuleiro = new int[3][3];
        imageButtonPositionOnMatrix = null;
        resetaImagens();
    }
    private void resetaImagens(){
        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                positionMatrix[i][j].setImageResource(R.drawable.invisivel);
            }
        }
        btnJogador1.setImageResource(R.drawable.jogador1);
        btnJogador2.setImageResource(R.drawable.jogador2_btn_foreground);
    }
    private boolean isVelha(){
        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                if(tabuleiro[i][j] == 0) return false;
            }
        }
        return true;
    }
    //endregion
}