package dino.image.processor.object;

public enum ObstacleType {
    BIRD,
    CACTUS,
    NONE;

    @Override
    public String toString() {
        return "ObstacleType{" + this.name() + "}";
    }
}
