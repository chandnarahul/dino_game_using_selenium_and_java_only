package dino.image.processor.action;

public class Action {
    private final ActionType actionType;
    private final long actionDuration;

    public Action(ActionType actionType, long actionDuration) {
        this.actionType = actionType;
        this.actionDuration = actionDuration;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public long getActionDuration() {
        return actionDuration;
    }
}
