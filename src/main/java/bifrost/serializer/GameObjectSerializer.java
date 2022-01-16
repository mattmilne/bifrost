package bifrost.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import component.Component;
import bifrost.GameObject;
import bifrost.Transform;

import java.lang.reflect.Type;

public class GameObjectSerializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        GameObject go = new GameObject(name);
        jsonObject.getAsJsonObject("components").entrySet()
                .forEach(x -> {
                    Component c = context.deserialize(x.getValue(), Component.class);
                    go.addComponent(c);
                    if (c instanceof Transform) {
                        go.transform = go.getComponent(Transform.class);
                    }
                });

        return go;
    }
}
