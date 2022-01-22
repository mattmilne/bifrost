package bifrost;

import component.AnimationState;
import component.PlayerController;
import component.Sprite;
import component.SpriteRenderer;
import component.Spritesheet;
import component.StateMachine;
import component.block.CoinBlock;
import component.block.QuestionBlock;
import component.tag.Ground;
import org.joml.Vector2f;
import physics2d.component.Box2DCollider;
import physics2d.component.PillboxCollider;
import physics2d.component.Rigidbody2D;
import physics2d.enums.BodyType;
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
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");
        Spritesheet bigPlayerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        float defaultFrameTime = 0.2f;

        AnimationState run = getRunAnimationState(playerSprites, defaultFrameTime);
        AnimationState switchDirection = getSwitchDirectionAnimationState(playerSprites);
        AnimationState idle = getIdleAnimationState(playerSprites);
        AnimationState jump = getJumpAnimationState(playerSprites);

        // Big mario animations
        AnimationState bigRun = getBigRunAnimationState(bigPlayerSprites, defaultFrameTime);
        AnimationState bigSwitchDirection = getBigSwitchDirectionAnimationState(bigPlayerSprites);
        AnimationState bigIdle = getBigIdleAnimationState(bigPlayerSprites);
        AnimationState bigJump = getBigJumpAnimationState(bigPlayerSprites);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = getFireRunAnimationState(bigPlayerSprites, defaultFrameTime, fireOffset);
        AnimationState fireSwitchDirection = getFireSwitchDirectionAnimationState(bigPlayerSprites, fireOffset);
        AnimationState fireIdle = getFireIdleAnimationState(bigPlayerSprites, fireOffset);
        AnimationState fireJump = getFireJumpAnimationState(bigPlayerSprites, fireOffset);
        AnimationState die = getDieAnimationState(playerSprites);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addState(run.title, switchDirection.title, "switchDirection");
        stateMachine.addState(run.title, idle.title, "stopRunning");
        stateMachine.addState(run.title, jump.title, "jump");
        stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addState(switchDirection.title, run.title, "startRunning");
        stateMachine.addState(switchDirection.title, jump.title, "jump");
        stateMachine.addState(idle.title, run.title, "startRunning");
        stateMachine.addState(idle.title, jump.title, "jump");
        stateMachine.addState(jump.title, idle.title, "stopJumping");

        stateMachine.addState(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addState(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigRun.title, bigJump.title, "jump");
        stateMachine.addState(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addState(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addState(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addState(bigIdle.title, bigJump.title, "jump");
        stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireRun.title, fireJump.title, "jump");
        stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addState(fireIdle.title, fireJump.title, "jump");
        stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addState(run.title, bigRun.title, "powerup");
        stateMachine.addState(idle.title, bigIdle.title, "powerup");
        stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addState(jump.title, bigJump.title, "powerup");
        stateMachine.addState(bigRun.title, fireRun.title, "powerup");
        stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addState(bigJump.title, fireJump.title, "powerup");

        stateMachine.addState(bigRun.title, run.title, "damage");
        stateMachine.addState(bigIdle.title, idle.title, "damage");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addState(bigJump.title, jump.title, "damage");
        stateMachine.addState(fireRun.title, bigRun.title, "damage");
        stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addState(fireJump.title, bigJump.title, "damage");

        stateMachine.addState(run.title, die.title, "die");
        stateMachine.addState(switchDirection.title, die.title, "die");
        stateMachine.addState(idle.title, die.title, "die");
        stateMachine.addState(jump.title, die.title, "die");
        stateMachine.addState(bigRun.title, run.title, "die");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addState(bigIdle.title, idle.title, "die");
        stateMachine.addState(bigJump.title, jump.title, "die");
        stateMachine.addState(fireRun.title, bigRun.title, "die");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addState(fireIdle.title, bigIdle.title, "die");
        stateMachine.addState(fireJump.title, bigJump.title, "die");
        mario.addComponent(stateMachine);

        PillboxCollider pillboxCollider = new PillboxCollider();
        pillboxCollider.width = 0.39f;
        pillboxCollider.height = 0.31f;
        Rigidbody2D rigidbody2D = new Rigidbody2D();
        rigidbody2D.setBodyType(BodyType.Dynamic);
        rigidbody2D.setContinuousCollision(false);
        rigidbody2D.setFixedRotation(true);
        rigidbody2D.setMass(25.0f);

        mario.addComponent(rigidbody2D);
        mario.addComponent(pillboxCollider);
        mario.addComponent(new PlayerController());

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState flickerState = new AnimationState();
        flickerState.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flickerState.addFrame(items.getSprite(0), 0.57f);
        flickerState.addFrame(items.getSprite(1), defaultFrameTime);
        flickerState.addFrame(items.getSprite(2), defaultFrameTime);
        flickerState.setLoop(true);

        AnimationState inactiveState = new AnimationState();
        inactiveState.title = "Inactive";
        inactiveState.addFrame(items.getSprite(3), 0.1f);
        inactiveState.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flickerState);
        stateMachine.addState(inactiveState);
        stateMachine.setDefaultState(flickerState.title);
        stateMachine.addState(flickerState.title, inactiveState.title, "setInactive");
        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        Rigidbody2D rigidbody2D = new Rigidbody2D();
        rigidbody2D.setBodyType(BodyType.Static);
        questionBlock.addComponent(rigidbody2D);
        Box2DCollider box2DCollider = new Box2DCollider();
        box2DCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
        questionBlock.addComponent(box2DCollider);
        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateCoinBlock() {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());
        coin.addComponent(new CoinBlock());

        return coin;
    }

    private static AnimationState getRunAnimationState(Spritesheet playerSprites, float defaultFrameTime) {
        AnimationState runState = new AnimationState();
        runState.title = "Run";
        runState.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        runState.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        runState.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        runState.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        runState.setLoop(true);
        return runState;
    }

    private static AnimationState getSwitchDirectionAnimationState(Spritesheet playerSprites) {
        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);
        return switchDirection;
    }

    private static AnimationState getIdleAnimationState(Spritesheet playerSprites) {
        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);
        return idle;
    }

    private static AnimationState getBigJumpAnimationState(Spritesheet bigPlayerSprites) {
        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);
        return bigJump;
    }

    private static AnimationState getBigIdleAnimationState(Spritesheet bigPlayerSprites) {
        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);
        return bigIdle;
    }

    private static AnimationState getBigSwitchDirectionAnimationState(Spritesheet bigPlayerSprites) {
        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);
        return bigSwitchDirection;
    }

    private static AnimationState getBigRunAnimationState(Spritesheet bigPlayerSprites, float defaultFrameTime) {
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);
        return bigRun;
    }

    private static AnimationState getJumpAnimationState(Spritesheet playerSprites) {
        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);
        return jump;
    }

    private static AnimationState getDieAnimationState(Spritesheet playerSprites) {
        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);
        return die;
    }

    private static AnimationState getFireJumpAnimationState(Spritesheet bigPlayerSprites, int fireOffset) {
        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);
        return fireJump;
    }

    private static AnimationState getFireIdleAnimationState(Spritesheet bigPlayerSprites, int fireOffset) {
        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);
        return fireIdle;
    }

    private static AnimationState getFireSwitchDirectionAnimationState(Spritesheet bigPlayerSprites, int fireOffset) {
        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);
        return fireSwitchDirection;
    }

    private static AnimationState getFireRunAnimationState(Spritesheet bigPlayerSprites, float defaultFrameTime, int fireOffset) {
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);
        return fireRun;
    }
}
