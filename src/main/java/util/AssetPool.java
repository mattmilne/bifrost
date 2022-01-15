package util;

import component.Spritesheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        String filepath = file.getAbsolutePath();
        Shader shader = shaders.get(filepath);

        if (shader == null) {
            shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(filepath, shader);
        }

        return shader;
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        String filepath = file.getAbsolutePath();
        Texture texture = AssetPool.textures.get(filepath);

        if (texture == null) {
            texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(filepath, texture);
        }

        return texture;
    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        String filepath = file.getAbsolutePath();
        if (!AssetPool.spritesheets.containsKey(filepath)) {
            AssetPool.spritesheets.put(filepath, spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);
        Spritesheet spritesheet = AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
        assert spritesheet != null : "Error: Tried to access spritesheet '" + resourceName +
                "' and it has not been added to asset pool.";

        return spritesheet;
    }
}
