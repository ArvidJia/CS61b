package gh2;


import org.junit.Test;

public class PlayForFun {
    @Test
    public void BWV847() {
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/BWV847.mid"));
        player.play(120);
    }

    @Test
    public void StariwayToHeaven() {
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/StairwayToHeaven.mid"));
        player.play(120);
    }

    @Test
    public void TheManWhoSoldTheWorld() {
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/TheManWhoSoldTheWorld.mid"));
        player.play(120);
    }

    @Test
    public void NovemberRain() {
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/NovemberRain.mid"));
        player.play(120);
    }

    @Test
    public void SmellsLikeTeenSpirit(){
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/SmellsLikeTeenSpirit.mid"));
        player.play(120);
    }

    @Test
    public void NorvagainWood(){
        GuitarPlayer player = new GuitarPlayer(new java.io.File("D:/Resources_61b/NorwegianWood.mid"));
        player.play(120);
    }


}
