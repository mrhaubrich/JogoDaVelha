package ucs.exercicio1.jogodavelha;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.media.FaceDetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    static final int PLAYER_1 = 1;
    static final int PLAYER_2 = 2;
    Gameplay game = new Gameplay();
    Images img = new Images();
    Drawable[][] originalBackground = new Drawable[3][3];
//    static final int numberOfFaces = 1;
//    private FaceDetector myFaceDetect = null;
//    private FaceDetector.Face[] myFace = null;
//    float myEyesDistance;
//    int numberOfFaceDetected = 0;
    ImageButton[][] positionMatrix = new ImageButton[3][3];
    Button btnClear;
    ImageButton btnJogador1, btnJogador2;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClear = findViewById(R.id.btnClear);
        file = new File(getFilesDir(), "picFromCamera" + ".jpg");
        instanciaElementos();
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},666);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        switch (requestCode){
            case PLAYER_1:
                img.setPic(btnJogador1,PLAYER_1,bitmap);
                break;
            case PLAYER_2:
                img.setPic(btnJogador2,PLAYER_2,bitmap);
                break;
            default:
                Toast.makeText(this, "Failed To Capture Image", Toast.LENGTH_SHORT).show();
                break;
        }
    }




    //region Métodos criados

    //region Instanciar

    private void instanciaElementos() {
        btnClear.setOnClickListener(view -> {
            clear();
            resetaImagensPlayers();
        });

        instanciaBtnTabuleiro();

        btnJogador1 = findViewById(R.id.imgPlayer1);
        btnJogador2 = findViewById(R.id.imgPlayer2);

        btnJogador1.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if(img.getPlayer1() == null){
                abreCameraIntent(PLAYER_1);
            }
            else{
                //iniciar jogo
                if(img.getPlayer2() == null) {
                    Toast.makeText(this,"Ambos jogadores precisam tirar foto!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!game.isGameStarted()){
                    game.setCurrentPlayer(PLAYER_1);
                    game.setGameStarted(true);
                    setTabuleiroEnabled(true);
                }
                habilitaDesabilitaPlayers(!game.isGameStarted());
            }
        });
        btnJogador2.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if(img.getPlayer2() == null){
                abreCameraIntent(PLAYER_2);
            }
            else{
                //iniciar jogo
                if(img.getPlayer1() == null) {
                    Toast.makeText(this,"Ambos jogadores precisam tirar foto!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!game.isGameStarted()){
                    game.setCurrentPlayer(PLAYER_2);
                    game.setGameStarted(true);
                    setTabuleiroEnabled(true);
                }
            }
            habilitaDesabilitaPlayers(!game.isGameStarted());
        });
    }

    private void abreCameraIntent(int player){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        if(cameraIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(cameraIntent, player);
    }

    private void instanciaBtnTabuleiro(){
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
                game.setTabuleiroPosition(i, j,true);
                // pega o id do xml
                String positionID = "imgPos" + somador;
                int resID = getResources().getIdentifier(positionID, "id", getPackageName());
                // coloca no array
                positionMatrix[i][j] = findViewById(resID);
                originalBackground[i][j] = positionMatrix[i][j].getBackground();
                String text = "S" + somador;
                // cria o listener de click
                int finalI = i;
                int finalJ = j;
                positionMatrix[i][j].setOnClickListener(view -> {
                    // verificar ganhador();
                    if (!game.checkPrePlay(finalI, finalJ)) return;
                    ImageButton position = (ImageButton) view;
                    position.setImageBitmap(game.getCurrentPlayer() == 1 ? img.getPlayer1() : img.getPlayer2());
                    game.setTabuleiroPosition(finalI, finalJ, false);
                    //Log.d("tabuleiro", "tabuleiro: ");
                    if(game.won(finalI, finalJ)){
                        if(!marcaWinningPlay()) return;
                        Handler handler = new Handler();
                        handler.postDelayed(()->{
                            abreActivityGanhador(false);
                            clear();
                        }, 5000);
                        return;
                    }
                    if(game.isVelha()){
                        abreActivityGanhador(true);
                        clear();
                        return;
                    }
                    game.alternarPlayer();
                });
                positionMatrix[i][j].setEnabled(false);
            }
        }
    }
    //endregion

    //region Gameplay
    private void setTabuleiroEnabled(boolean enabled){
        for(int i = 0 ; i < 3 ; i++){
            for(int j = 0 ; j < 3 ; j++){
                positionMatrix[i][j].setEnabled(enabled);
            }
        }
    }

    private boolean marcaWinningPlay(){
        int[][] winningPlay = game.getWinningPlay();
        if(winningPlay == null) return false;
        int i, j;

        for(int x = 0; x < 3; x++){
            i = winningPlay[x][0];
            j = winningPlay[x][1];
            positionMatrix[i][j].setBackgroundColor(getColor(R.color.green));
        }
        return true;
    }

    private void habilitaDesabilitaPlayers(boolean enabled){
        btnJogador1.setEnabled(enabled);
        btnJogador2.setEnabled(enabled);
    }

    private void abreActivityGanhador(boolean pVelha){
        Intent winnerIntent = new Intent(this, WinnerActivity.class);
        String winner = !pVelha ? "Jogador " + game.getCurrentPlayer() : "Deu velha!";
        Bitmap imagem = pVelha ? null : (game.getCurrentPlayer() == PLAYER_1 ? img.getOriginalPlayer1() : img.getOriginalPlayer2());
        winnerIntent.putExtra("vencedor", winner);
        winnerIntent.putExtra("imagem", imagem);
        startActivity(winnerIntent);
    }
    private void clear(){
        game.setCurrentPlayer(0);
        game.clearTabuleiro();
        game.setGameStarted(false);
        game.clearTabuleiroBoolean();
        setTabuleiroEnabled(false);
        habilitaDesabilitaPlayers(true);
        resetaImagens();
    }
    private void resetaImagens(){
        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                positionMatrix[i][j].setImageResource(R.drawable.invisivel);
                positionMatrix[i][j].setBackground(originalBackground[i][j]);
            }
        }
    }
    private void resetaImagensPlayers(){
        btnJogador1.setImageResource(R.drawable.jogador1);
        btnJogador2.setImageResource(R.drawable.jogador2_btn_foreground);
        img.clearImages();
    }

    //endregion

    //endregion
}