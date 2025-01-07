package dino.image.processor.object;

public class DinoGameObject {
    public String type;
    public final DinoPoint dinoPoint;

    public DinoGameObject(String type, DinoPoint dinoPoint) {
        this.type = type;
        this.dinoPoint = dinoPoint;
    }
}
