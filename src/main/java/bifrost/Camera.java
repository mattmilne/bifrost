package bifrost;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private static final float WORLD_POS_LEFT = 0.0f;
    private static final float WORLD_POS_RIGHT = 32.0f * 40.0f;
    private static final float WORLD_POS_TOP = 32.0f * 21.0f;
    private static final float WORLD_POS_BOTTOM = 0.0f;
    private static final float NEAR_CLIP = 0.0f;
    private static final float FAR_CLIP = 100.0f;

    private final Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position;
    private final Vector2f projectionSize = new Vector2f(WORLD_POS_RIGHT, WORLD_POS_TOP);
    private float zoom = 1.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
//        projectionSize.set(WORLD_POS_RIGHT * zoom, WORLD_POS_TOP * zoom);
        projectionMatrix.ortho(WORLD_POS_LEFT, projectionSize.x * zoom, WORLD_POS_BOTTOM, projectionSize.y * zoom, NEAR_CLIP, FAR_CLIP);
        projectionMatrix.invert(inverseProjection);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp
        );
        viewMatrix.invert(inverseView);

        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return this.inverseProjection;
    }

    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    public Vector2f getProjectionSize() {
        return this.projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }
}
