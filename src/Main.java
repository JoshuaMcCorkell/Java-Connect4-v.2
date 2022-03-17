import connectgame.engine.Connect4;
import connectgame.engine.ConnectGame;

public class Main{
    public static void main(String[] args) {
        ConnectGame game = new Connect4();
        game.play(0);
        game.play(5);
        game.play(1);
        game.play(5);
        game.play(2);
        game.play(5);
        game.play(3);
        System.out.println(game.getWinner());
    }
}
