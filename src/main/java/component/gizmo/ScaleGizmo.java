package component.gizmo;

import component.Sprite;
import editor.PropertiesWindow;
import bifrost.MouseListener;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite blockSprite, PropertiesWindow propertiesWindow) {
        super(blockSprite, propertiesWindow);
    }

    @Override
    public void update(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= MouseListener.getWorldX();
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getWorldY();
            }
        }

        super.editorUpdate(dt);
    }
}
