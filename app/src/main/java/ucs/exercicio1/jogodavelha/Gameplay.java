package ucs.exercicio1.jogodavelha;

public class Gameplay {
    private boolean gameStarted = false;
    private int currentPlayer = 0;
    private int[][] tabuleiro = new int[3][3];

    public boolean[][] getTabuleiroBoolean() {
        return tabuleiroBoolean;
    }

    private boolean[][] tabuleiroBoolean = new boolean[3][3];

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setTabuleiroPosition(int i, int j, boolean empty) {
        tabuleiro[i][j] = empty ? 0 : currentPlayer;
    }

    public boolean checkPrePlay(int i, int j) {
        return tabuleiro[i][j] == 0;
    }

    public void clearTabuleiro() {
        tabuleiro = new int[3][3];
    }


    public boolean isVelha() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tabuleiro[i][j] == 0) return false;
            }
        }
        return true;
    }

    public int[][] getWinningPlay(){
        int[][] positions = new int[3][2];
        int count = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(tabuleiroBoolean[i][j]){
                    positions[count][0] = i;
                    positions[count][1] = j;
                    count++;
                }
            }
        }
        if(count < 3) return null;
        return positions;
    }

    public void alternarPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    public void clearTabuleiroBoolean(){
        tabuleiroBoolean = new boolean[3][3];
    }

    private boolean verificaHorizontal(int i) {
        for (int j = 0; j < 3; j++) {
            if (tabuleiro[i][j] != currentPlayer) {
                clearTabuleiroBoolean();
                return false;
            }
            tabuleiroBoolean[i][j] = true;
        }
        return true;
    }

    private boolean verificaVertical(int j) {
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][j] != currentPlayer) {
                clearTabuleiroBoolean();
                return false;
            }
            tabuleiroBoolean[i][j] = true;
        }
        return true;
    }

    private boolean verificaDiagonal(int i, int j) {
        if ((i + j) % 2 == 1)
            return false;
        switch (i + j) {
            case 2:
                if (i == 1) {
                    if (!diagonalDecrescente() && !diagonalCrescente()) return false;
                } else {
                    if (!diagonalCrescente()) return false;
                }
                break;
            case 4:
            case 0:
                if (!diagonalDecrescente()) return false;
                break;
        }
        return true;
    }

    private boolean diagonalCrescente() {
        int j = 2;
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][j] != currentPlayer) {
                clearTabuleiroBoolean();
                return false;
            }
            tabuleiroBoolean[i][j] = true;
            j--;
        }
        return true;
    }

    private boolean diagonalDecrescente() {
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][i] != currentPlayer) {
                clearTabuleiroBoolean();
                return false;
            }
            tabuleiroBoolean[i][i] = true;
        }
        return true;
    }

    public boolean won(int i, int j) {
        return verificaDiagonal(i, j) || verificaVertical(j) || verificaHorizontal(i);
    }


}
