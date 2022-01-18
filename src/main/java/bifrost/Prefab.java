package bifrost;

import component.AnimationState;
import component.Sprite;
import component.SpriteRenderer;
import component.Spritesheet;
import component.StateMachine;
import util.AssetPool;

public class Prefab {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite Object Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generateMario() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        AnimationState runState = new AnimationState();
        runState.title = "Run";
        float defaultFrameTime = 0.23f;
        runState.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        runState.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        runState.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        runState.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        runState.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(runState);
        stateMachine.setDefaultState(runState.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState flickerState = new AnimationState();
        flickerState.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flickerState.addFrame(items.getSprite(0), defaultFrameTime);
        flickerState.addFrame(items.getSprite(1), defaultFrameTime);
        flickerState.addFrame(items.getSprite(2), defaultFrameTime);
        flickerState.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flickerState);
        stateMachine.setDefaultState(flickerState.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }
}
