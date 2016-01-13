package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;

public class Damcon implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final Color WIREFRAME_COLOR = Color.GREEN;

    private Canvas3D canvas;
    private SimpleUniverse universe;
    private Scene scene = null;
    private static final boolean WINDOW_HACK = true;

    private static final double
            ZOOM_FACTOR = 0.25d,
            ROTATION_FACTOR = 0.01d,
            MIN_PITCH_Y = 0.1d,
            MIN_ZOOM_RADIUS = 2.9d;
    private static final Vector2d MAX_ROTATION_AMOUNT = new Vector2d(0.125, 0.125);

    private Point lastMouseDragPosition = new Point();
    private boolean rotating = false;

    public Damcon() {
        loadAndWireframeifyModel();

        if (WINDOW_HACK) {
            createUniverseAndScene_HACK();
            // TODO: This is a hack to get rid of the extra window. The reason this creates a new window is explained here:
            // http://download.java.net/media/java3d/javadoc/1.3.2/com/sun/j3d/utils/universe/Viewer.html
            // This might help: https://community.oracle.com/thread/1274674?start=0&tstart=0
            JFrame unused_frame = universe.getViewer().getJFrame(0);
            unused_frame.setVisible(false);
            unused_frame.dispose();
        } else {
            createUniverseAndScene();
        }

        addMouseListeners();
        setCameraPresets();
    }

    private void loadAndWireframeifyModel() {
        String OBJ_PATH = new File(System.getProperty("user.dir"), "art/models/obj-from-blender/artemis2.obj").getPath();
        try {
            this.scene = new ObjectFile(ObjectFile.RESIZE).load(OBJ_PATH);
            wireframeifyScene(scene);

        } catch (FileNotFoundException | IncorrectFormatException | ParsingErrorException e) {
            e.printStackTrace(System.err);
        }
    }

    private void createUniverseAndScene() {
        // This is an attempt to give the Viewer a canvas so it doesn't create its own window (it doesn't render properly)
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        Viewer viewer = new Viewer(canvas);
        ViewingPlatform viewingPlatform = new ViewingPlatform();
        this.universe = new SimpleUniverse(viewingPlatform, viewer);
    }

    private void createUniverseAndScene_HACK() {
        this.universe = new SimpleUniverse();
        this.universe.addBranchGraph(scene.getSceneGroup());
    }

    private void addMouseListeners() {
        this.canvas = universe.getCanvas();
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
        this.canvas.addMouseWheelListener(this);
    }

    private void setCameraPresets() {
        ViewingPlatform vp = this.universe.getViewingPlatform();
        vp.setNominalViewingTransform();
        // TODO: this actually puts the camera too close.
        // Fixes:
        // (1) manually set camera start position (I'd recommend a side or 3/4 view)
        // (2) call the code in mouse wheel zoom and it'll automatically fix camera position
    }

    ////////
    // 3D //
    ////////
    public Canvas3D getCanvas() {
        return this.canvas;
    }

    private TransformGroup getCamera() {
        return this.universe.getViewingPlatform().getViewPlatformTransform();
    }

    ///////////////
    // Wireframe //
    ///////////////
    private static void wireframeifyScene(Scene scene) {
        Appearance wireframe = getWireframeAppearance();

        // TODO: This works for the Artemis OBJ model.  If the scene graph has multiple Shape3D nodes, this would need to be set on all of them.  Is that necessary or can we guarantee it won't be needed?
        Enumeration<Node> children = scene.getSceneGroup().getAllChildren();
        while (children.hasMoreElements()) {
            Node node = children.nextElement();
            if (node.getClass().equals(Shape3D.class)) {
                Shape3D s3d = (Shape3D) node;
                s3d.setAppearance(wireframe);
            }
        }
    }

    private static Appearance getWireframeAppearance() {
        Appearance appearance = new Appearance();

        Color awtColor = WIREFRAME_COLOR;
        Color3f color = new Color3f(awtColor);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(color);
        appearance.setColoringAttributes(ca);

        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(pa.POLYGON_LINE);
        pa.setCullFace(pa.CULL_NONE);
        appearance.setPolygonAttributes(pa);

        return appearance;
    }

    ///////////
    // Mouse //
    ///////////
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {
            // TODO: damcon selection and orders issuing
            System.out.println("Left click");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        rotating = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Note: an alternative is OrbitBehavior, but it won't really do what we want (we need axis-specific limits)
        // http://download.java.net/media/java3d/javadoc/1.3.2/com/sun/j3d/utils/behaviors/vp/OrbitBehavior.html
        // OrbitBehavior orbit = new OrbitBehavior(this.canvas, OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.DISABLE_TRANSLATE);
        // orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0d, 0d, 0d), Double.MAX_VALUE));
        // vp.setViewPlatformBehavior(orbit);
        // vp.minRadius(...)

        if (SwingUtilities.isRightMouseButton(e)) {
            if (!rotating) {
                rotating = true;
                lastMouseDragPosition = e.getPoint();
            } else {
                // Get mouse movement
                Point mouse_diff = new Point(lastMouseDragPosition.x - e.getPoint().x, lastMouseDragPosition.y - e.getPoint().y);
                lastMouseDragPosition = e.getPoint();

                // Convert mouse movement to rotation factors
                Vector2d rotation_amount = new Vector2d(ROTATION_FACTOR * mouse_diff.x, ROTATION_FACTOR * mouse_diff.y);
                clampRotation(rotation_amount);

                // Get camera spatial data
                TransformGroup camera = getCamera();
                Transform3D xform = new Transform3D();  // copy camera's transform to xform
                Vector3d cam_pos = new Vector3d();
                camera.getTransform(xform);
                xform.get(cam_pos);                     // copy camera's position to cam_pos

                // Get unit vector representing camera's look/forward
                // We know the camera is always looking at the origin so this math is simpler than the general case
                Vector3d look = new Vector3d(-cam_pos.x, -cam_pos.y, -cam_pos.z);
                look.normalize();

                // Get right vector
                Vector3d up = new Vector3d(0d, 1d, 0d);
                Vector3d right = new Vector3d();
                right.cross(look, up);
                right.normalize();

                // Apply pitch
                AxisAngle4d pitch_aa = new AxisAngle4d();
                pitch_aa.set(right, rotation_amount.y);
                Transform3D pitch_xform = new Transform3D();
                pitch_xform.set(pitch_aa);
                Vector3d pitched_cam_pos = new Vector3d(cam_pos);
                pitch_xform.transform(pitched_cam_pos);

                // Calculate new up vector after pitch
                Vector3d new_up = new Vector3d();
                new_up.cross(pitched_cam_pos, right);
                new_up.normalize();

                // Ensure we don't get too close to looking directly up or down the Y axis
                if (new_up.y < MIN_PITCH_Y) {
                    new_up = new Vector3d(up);
                    pitched_cam_pos = new Vector3d(cam_pos);
                }

                // Generate updated look vector
                look = new Vector3d(pitched_cam_pos);
                look.normalize();

                // Apply yaw
                AxisAngle4d yaw_aa = new AxisAngle4d();
                yaw_aa.set(new_up, rotation_amount.x);
                Transform3D yaw_xform = new Transform3D();
                yaw_xform.set(yaw_aa);
                Vector3d yawed_cam_pos = new Vector3d(pitched_cam_pos);
                yaw_xform.transform(yawed_cam_pos);

                // Using new camera position and new up vector, generate a new camera transformation pointing at the origin
                xform.lookAt(new Point3d(yawed_cam_pos.x, yawed_cam_pos.y, yawed_cam_pos.z), new Point3d(0d, 0d, 0d), new_up);
                xform.invert();             // Why do we have to invert this?!  Who knows?!
                camera.setTransform(xform);

                // TODO: make sure look is not colinear with up etc./set axis limits
            }
        }
    }

    private static void clampRotation(Vector2d rotation_amount) {
        rotation_amount.x = clampRotationAxis(rotation_amount.x, MAX_ROTATION_AMOUNT.x);
        rotation_amount.y = clampRotationAxis(rotation_amount.y, MAX_ROTATION_AMOUNT.y);
    }

    private static double clampRotationAxis(double r, double max) {
        if (Math.abs(r) > max) {
            return max * Math.signum(r);
        }
        return r;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoom_move_distance = ZOOM_FACTOR * -e.getPreciseWheelRotation();

        // Get camera spatial data
        TransformGroup camera = getCamera();
        Transform3D xform = new Transform3D();  // copy camera's transform to xform
        camera.getTransform(xform);
        Vector3d cam_pos = new Vector3d();
        xform.get(cam_pos);                     // copy camera's position to cam_pos

        // Get unit vector representing camera's look/forward
        //Vector3d look = new Vector3d(cam_pos);  // we know the camera is looking at the origin so this math is simpler than the general case
        Vector3d look = new Vector3d(-cam_pos.x, -cam_pos.y, -cam_pos.z);  // we know the camera is looking at the origin so this math is simpler than the general case
        look.normalize();

        // Determine zoom movement vector and apply it to camera position
        Vector3d zoom_move_vec = new Vector3d(look.x * zoom_move_distance, look.y * zoom_move_distance, look.z * zoom_move_distance);
        Vector3d new_cam_pos = new Vector3d(cam_pos.x + zoom_move_vec.x, cam_pos.y + zoom_move_vec.y, cam_pos.z + zoom_move_vec.z);

        // If zoom takes us too close to the object's center (such that it won't fit in the frame or the camera would be inside the object),
        // drop it at the edge of the minimum radius
        if (new_cam_pos.length() < MIN_ZOOM_RADIUS && Math.signum(zoom_move_distance) > 0d) {
            new_cam_pos = new Vector3d(-look.x * MIN_ZOOM_RADIUS, -look.y * MIN_ZOOM_RADIUS, -look.z * MIN_ZOOM_RADIUS);
        }

        // Apply new position to transformation and transformation back to camera
        xform.setTranslation(new_cam_pos);
        camera.setTransform(xform);
    }
}