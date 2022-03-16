import connectgame.*;

public class Main{
    public static void main(String[] args) {
        GameBoard test = new GameBoard(5, 5);
        test.putDisk(1, 3);
        test.putDisk(2, 3);
        test.putDisk(1, 2);
        System.out.println(test.get(3, 0));
        System.out.println(test.popDisk(3));
        System.out.println(test.popDisk(3));
        System.out.println(test.popDisk(2));
    }
}
