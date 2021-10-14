package ucs.exercicio1.jogodavelha;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    static final int PLAYER_1 = 1;
    static final int PLAYER_2 = 2;
    Bitmap bmpPlayer1, bmpPlayer2;
    private boolean gameStarted = false;
    static final int numberOfFaces = 1;
    private FaceDetector myFaceDetect = null;
    private FaceDetector.Face[] myFace = null;
    float myEyesDistance;
    int numberOfFaceDetected = 0;
    String currentPhotoPathPlayer1, currentPhotoPathPlayer2;
    int currentPlayer = 0;
    ImageButton[][] positionMatrix = new ImageButton[3][3];
    int[][] tabuleiro = new int[3][3];
    Button btnClear;
    ImageButton btnJogador1, btnJogador2;
    int[] imageButtonPositionOnMatrix = null;
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
        switch (requestCode){
            case PLAYER_1:
                galleryAddPic(PLAYER_1);
                try {
                    setPic(btnJogador1, PLAYER_1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PLAYER_2:
                galleryAddPic(PLAYER_2);
                try {
                    setPic(btnJogador2, PLAYER_2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                Toast.makeText(this, "Failed To Capture Image", Toast.LENGTH_SHORT).show();
                break;
        }
    }




    //region Métodos criados

    //region Instanciar

    private void instanciaElementos() {
        btnClear.setOnClickListener(view -> clear());

        instanciaBtnTabuleiro();

        btnJogador1 = findViewById(R.id.imgPlayer1);
        btnJogador2 = findViewById(R.id.imgPlayer2);

        btnJogador1.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if(bmpPlayer1 == null){
                abreCameraIntent(PLAYER_1);
            }
            else{
                //iniciar jogo
                if(bmpPlayer2 == null) {
                    Toast.makeText(this,"Ambos jogadores precisam tirar foto!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!gameStarted){
                    currentPlayer = PLAYER_1;
                    setTabuleiroEnabled(true);
                }
            }
        });
        btnJogador2.setOnClickListener(view -> {
            // if imagem não existe abre camera else inicia jogo
            if(bmpPlayer2 == null){
                abreCameraIntent(PLAYER_2);
            }
            else{
                //iniciar jogo
                if(bmpPlayer1 == null) {
                    Toast.makeText(this,"Ambos jogadores precisam tirar foto!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!gameStarted){
                    currentPlayer = PLAYER_2;
                    setTabuleiroEnabled(true);
                }
            }
        });
    }

    private void abreCameraIntent(int player){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        cameraIntent.removeExtra(MediaStore.EXTRA_OUTPUT);
        File photoFile = null;
        try {
            photoFile = createImageFile(player);
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(this.getApplicationContext(),"Error creating file",Toast.LENGTH_LONG).show();
        }
        if (photoFile == null) return;
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
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
        bmpPlayer1 = null;
        bmpPlayer2 = null;
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

    //region Arquivos

    private boolean deletePicsFromGallery(String photoPath){
        File fdelete = new File(photoPath);
        if (fdelete.exists()) {
            return fdelete.delete();
        }
        return false;
    }

    private void galleryAddPic(int player) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(player == PLAYER_1 ? currentPhotoPathPlayer1 : currentPhotoPathPlayer2);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic(ImageButton btnJogador, int player) throws IOException {
        String currentPhotoPath = player == PLAYER_1 ? currentPhotoPathPlayer1 : currentPhotoPathPlayer2;
        Log.i("path", "path: " + currentPhotoPath);
        // Get the dimensions of the View
        int targetW = btnJogador.getWidth();
        int targetH = btnJogador.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.max(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        ExifInterface ei = new ExifInterface(currentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        bitmap = cropBitmap(bitmap);
        bitmap = rotateBitmap(bitmap,orientation);
        if(player == PLAYER_1) bmpPlayer1 = bitmap;
        else bmpPlayer2 = bitmap;
        btnJogador.setImageBitmap(bitmap);
        if(!deletePicsFromGallery(currentPhotoPath)) Toast.makeText(this,"Não foi possível deletar as imagens!",Toast.LENGTH_LONG).show();
    }

    private Bitmap cropBitmap(Bitmap srcBmp){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

    private Bitmap rotateBitmap(Bitmap srcBmp, int pOrientation){
        int orientation;
        switch(pOrientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = 270;
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                orientation = 0;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
    }

    private File createImageFile(int player) throws IOException {
        // Create an image file name
        String imageFileName = "Player" + player;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        if(player == PLAYER_1)
            currentPhotoPathPlayer1 = image.getAbsolutePath();
        else
            currentPhotoPathPlayer2 = image.getAbsolutePath();
        return image;
    }


    //endregion

    //endregion
}