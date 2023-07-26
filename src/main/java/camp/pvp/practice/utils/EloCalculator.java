package camp.pvp.practice.utils;

public class EloCalculator {

    public static int getEloDifference(int winnerElo, int loserElo) {
        return (int) Math.round(40 * (1 / (1 + Math.pow(10.0, (double) (winnerElo - loserElo) / 400))));
    }

    public static int getNewWinnerElo(int winnerElo, int loserElo) {
        int difference = getEloDifference(winnerElo, loserElo);
        return winnerElo + difference;
    }

    public static int getNewLoserElo(int winnerElo, int loserElo) {
        int difference = getEloDifference(winnerElo, loserElo);
        return loserElo - difference;
    }
}
