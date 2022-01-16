package editor;

import imgui.ImGui;
import observer.EventSystem;
import observer.event.Event;
import observer.event.EventType;

public class MenuBar {

    public void imgui() {
        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Cmd+S")) {
                EventSystem.notify(null, new Event(EventType.SaveLevel));
            } else if (ImGui.menuItem("Load", "Cmd+O")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Settings")) {
            if (ImGui.menuItem("Toggle Physics Debug Draw")) {
//                ObserverHandler.notify(null, new Event(EventType.TogglePhysicsDebugDraw));
            }
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }
}
