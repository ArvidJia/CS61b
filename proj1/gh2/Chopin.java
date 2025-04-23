package gh2;

/**
 * Play Etudes,Op.10:No.3
 */
public class Chopin {
    public static void main(String[] args) {
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/Chopin_Tristesse.mid"));
        player.play(120);
    }
}
