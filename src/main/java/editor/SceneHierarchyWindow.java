package editor;

import bifrost.GameObject;
import bifrost.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    public void imgui() {
        ImGui.begin("Scene Heirarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.doSerialization()) {
                continue;
            }

            ImGui.pushID(index++);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    gameObject.name,
                    ImGuiTreeNodeFlags.DefaultOpen |
                            ImGuiTreeNodeFlags.FramePadding |
                            ImGuiTreeNodeFlags.OpenOnArrow |
                            ImGuiTreeNodeFlags.SpanAvailWidth,
                    gameObject.name
            );
            ImGui.popID();

            if (treeNodeOpen) {
                ImGui.treePop();
            }
        }

        ImGui.end();
    }
}
