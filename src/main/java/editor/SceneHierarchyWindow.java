package editor;

import bifrost.GameObject;
import bifrost.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    private static final String PAYLOAD_DRAG_DROP_TYPE = "SceneHierarchy";

    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.doSerialization()) {
                continue;
            }

            boolean treeNodeOpen = doTreeNode(gameObject, index);
            if (treeNodeOpen) {
                ImGui.treePop();
            }
            index++;
        }

        ImGui.end();
    }

    private boolean doTreeNode(GameObject gameObject, int index) {
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                gameObject.name,
                ImGuiTreeNodeFlags.DefaultOpen |
                        ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow |
                        ImGuiTreeNodeFlags.SpanAvailWidth,
                gameObject.name
        );
        ImGui.popID();

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(PAYLOAD_DRAG_DROP_TYPE, gameObject);
            ImGui.text(gameObject.name);
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            Object payloadObject = ImGui.acceptDragDropPayload(PAYLOAD_DRAG_DROP_TYPE);
            if (payloadObject != null && payloadObject.getClass().isAssignableFrom(GameObject.class)) {
                GameObject playerGameObject = (GameObject) payloadObject;
                System.out.println("Payload accepted: '" + playerGameObject.name + "'");
            }
            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }
}
