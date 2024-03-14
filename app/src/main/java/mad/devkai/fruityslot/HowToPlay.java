package mad.devkai.fruityslot;

public class HowToPlay {
    MainSlot MainSlot;

    private int Coins;
    private int lines = 1;
    private int bet;
    private int jackpot;
    private int prize = 0;
    private boolean hasWon = false;



    private int[] slots = {1, 1, 1};

    public int getRandomInt() {
        return (int) (Math.random() * 7 + 1);
    }

    public int getPosition(int i) {
        return slots[i] + 5;
    }

    public void getSpinResults() {
        prize = 0;
        for (int i = 0; i < slots.length; i++) {
            slots[i] = getRandomInt();
        }

        if (slots[0] == 7 || slots[1] == 7 || slots[2] == 7) {
            hasWon = true;
            int i = 0;
            for (int a : slots) {
                if (a == 7) i++;
            }

            switch (i) {
                case 1:
                    prize = bet * 5;
                    break;
                case 2:
                    prize = bet * 10;
                    break;
                case 3:
                    prize = jackpot;
                    jackpot = 0;
                    break;
            }
            Coins += prize;
        } else if (slots[0] == slots[1] && slots[1] == slots[2]) {
            hasWon = true;
            switch (slots[0]) {
                case 1:
                    prize = bet * 2;
                    Coins += prize;
                    break;
                case 2:
                    prize = bet * 3;
                    Coins += prize;
                    break;
                case 3:
                    prize = bet * 5;
                    Coins += prize;
                    break;
                case 4:
                    prize = bet * 7;
                    Coins += prize;
                    break;
                case 5:
                    prize = bet * 10;
                    Coins += prize;
                    break;
                case 6:
                    prize = bet * 15;
                    Coins += prize;
                    break;
            }
        } else {
            Coins -= bet;
            jackpot += bet;
        }
    }

    public void setMyCoins(int myCoins) {
        this.Coins = myCoins;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public void setJackpot(int jackpot) {
        this.jackpot = jackpot;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public void betUp() {
        if (bet < 100) {
            bet += 5;
            Coins -= 5;
        }
    }

    public void betDown() {
        if (bet > 5) {
            bet -= 5;
            Coins += 5;
        }
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }

    public String getMyCoins() {
        return Integer.toString(Coins);
    }

    public String getLines() {
        return Integer.toString(lines);
    }

    public String getBet() {
        return Integer.toString(bet);
    }

    public String getJackpot() {
        return Integer.toString(jackpot);
    }

    public boolean getHasWon() {
        return hasWon;
    }

    public String getPrize() {
        return Integer.toString(prize);
    }

    public void applyFreeSpins(int i) {
    }
}

