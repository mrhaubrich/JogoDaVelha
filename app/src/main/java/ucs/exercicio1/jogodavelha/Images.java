package ucs.exercicio1.jogodavelha;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageButton;

import java.io.IOException;

public class Images {
    static final int PLAYER_1 = 1;
    static final int PLAYER_2 = 2;

    private Bitmap player1;
    private Bitmap player2;

    public Bitmap getOriginalPlayer1() {
        return originalPlayer1;
    }

    public void setOriginalPlayer1(Bitmap originalPlayer1) {
        this.originalPlayer1 = originalPlayer1;
    }

    public Bitmap getOriginalPlayer2() {
        return originalPlayer2;
    }

    public void setOriginalPlayer2(Bitmap originalPlayer2) {
        this.originalPlayer2 = originalPlayer2;
    }

    private Bitmap originalPlayer1;
    private Bitmap originalPlayer2;

    public Images() {
    }

    public Bitmap getPlayer1() {
        return player1;
    }

    public void setPlayer1(Bitmap player1) {
        this.player1 = player1;
    }

    public Bitmap getPlayer2() {
        return player2;
    }

    public void setPlayer2(Bitmap player2) {
        this.player2 = player2;
    }

    public void clearImages(){
        player1 = player2 = originalPlayer1 = originalPlayer2 = null;
    }

    public void setPic(ImageButton btnJogador, int player, Bitmap original){
        int targetW = btnJogador.getMaxWidth();
        int targetH = btnJogador.getMaxHeight();
        if(player == PLAYER_1) originalPlayer1 = rotateBitmap(original, 270);
        else originalPlayer2 = rotateBitmap(original, 270);
        original = cropBitmap(original);
        original = rotateBitmap(original, 270);


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = original.getWidth();
        int photoH = original.getHeight();


        double scaleFactor = Math.max(1, Math.min((double) photoW/targetW, (double) photoH/targetH));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int)scaleFactor;
        bmOptions.inPurgeable = true;

        Matrix matrix = new Matrix();
        matrix.postScale((float) targetW / (float) photoW, (float) targetH / (float) photoH);

        Bitmap bitmap = Bitmap.createBitmap(original,0,0,photoW,photoH,matrix,false);
        if(player == PLAYER_1) player1 = bitmap;
        else player2 = bitmap;
        btnJogador.setImageBitmap(bitmap);
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
                orientation = pOrientation;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
    }
}
