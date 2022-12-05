package snake.Networking.ClientPackage;

public class MovePackage {
    private final GamePackage _addPackage;
    private final GamePackage _removePackage;

    public MovePackage(GamePackage startPackage, GamePackage endPackage) {
        _addPackage = startPackage;
        _removePackage = endPackage;
    }
    public MovePackage(GamePackage startPackage) {
        _addPackage = startPackage;
        _removePackage = null;
    }

    public GamePackage getAddPackage() {
        return _addPackage;
    }

    public GamePackage getRemovePackage() {
        return _removePackage;
    }
}
