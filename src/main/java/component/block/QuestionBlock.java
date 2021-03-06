package component.block;

import bifrost.GameObject;
import bifrost.Prefab;
import bifrost.Window;
import component.PlayerController;
import component.StateMachine;

public class QuestionBlock extends Block {

    public BlockType blockType = BlockType.Coin;

    @Override
    void playerHit(PlayerController playerController) {
        switch (blockType) {
            case Coin:
                doCoin(playerController);
                break;
            case Powerup:
                doPowerup(playerController);
                break;
            case Invincibility:
                doInvincibility(playerController);
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("setInactive");
            this.setInactive();
        }
    }

    private void doInvincibility(PlayerController playerController) {

    }

    private void doPowerup(PlayerController playerController) {

    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefab.generateCoinBlock();
        coin.transform.position.set(this.gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }

    private enum BlockType {
        Coin,
        Powerup,
        Invincibility
    }
}
