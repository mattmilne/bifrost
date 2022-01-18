package component;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StateMachine extends Component {

    public Map<StateTrigger, String> stateTransfers = new HashMap<>();
    private final List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";

    public void refreshTextures() {
        for (AnimationState animationState : states) {
             animationState.refreshTextures();
        }
    }

    public void addStateTrigger(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state) {
        this.states.add(state);
    }

    public void setDefaultState(String animationTitle) {
        for (AnimationState animationState : states) {
            if (animationState.title.equals(animationTitle)) {
                defaultStateTitle = animationTitle;
                if (currentState == null) {
                    currentState = animationState;
                    return;
                }
            }
        }

        System.out.println("Unable to find default state '" + animationTitle + "'");
    }

    public void trigger(String trigger) {
        StateTrigger stateTrigger = new StateTrigger(currentState.title, trigger);
        String target = stateTransfers.get(stateTrigger);
        if (target != null) {
            for (AnimationState animationState : states) {
                if (animationState.title.equals(target)) {
                    currentState = animationState;
                    break;
                }
            }

            return;
        }

        System.out.println("Unable to find trigger '" + trigger + "'");
    }

    @Override
    public void start() {
        for (AnimationState animationState : states) {
            if (animationState.title.equals(defaultStateTitle)) {
                currentState = animationState;
                break;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void imgui() {
        for (AnimationState animationState : states) {
            ImString title = new ImString(animationState.title);
            ImGui.inputText("State: ", title);
            animationState.title = title.get();

            ImBoolean doesLoop = new ImBoolean(animationState.doesLoop);
            ImGui.checkbox("Does Loop?", doesLoop);
            animationState.setLoop(doesLoop.get());

            for (int i = 0; i < animationState.animationFrames.size(); i++) {
                Frame frame = animationState.animationFrames.get(i);
                float[] frameFloats = new float[1];
                frameFloats[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + i + ") Time: ", frameFloats, 0.01f);
                frame.frameTime = frameFloats[0];
            }
        }
    }

    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger() {}
        public StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger stateTrigger = (StateTrigger) o;
            return stateTrigger.trigger.equals(this.trigger) && stateTrigger.state.equals(this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(trigger, state);
        }
    }
}
