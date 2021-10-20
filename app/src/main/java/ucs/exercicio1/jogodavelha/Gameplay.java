package ucs.exercicio1.jogodavelha;

public class Gameplay {
    private boolean gameStarted = false;
    private int currentPlayer = 0;
    private int[][] tabuleiro = new int[3][3];

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


    public void alternarPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    private boolean verificaHorizontal(int i) {
        for (int j = 0; j < 3; j++) {
            if (tabuleiro[i][j] != currentPlayer) return false;
        }
        return true;
    }

    private boolean verificaVertical(int j) {
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][j] != currentPlayer) return false;
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
            if (tabuleiro[i][j] != currentPlayer) return false;
            j--;
        }
        return true;
    }

    private boolean diagonalDecrescente() {
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][i] != currentPlayer) return false;
        }
        return true;
    }

    public boolean won(int i, int j) {
        return verificaDiagonal(i, j) || verificaVertical(j) || verificaHorizontal(i);
    }


}
