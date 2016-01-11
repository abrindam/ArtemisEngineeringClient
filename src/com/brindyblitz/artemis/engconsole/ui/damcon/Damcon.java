package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.jogamp.opengl.math.VectorUtil;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;

public class Damcon implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final Color WIREFRAME_COLOR = Color.GREEN;

    private Canvas3D canvas;
    private SimpleUniverse universe;

    private static final double
            ZOOM_FACTOR = 0.25d,
            MIN_ZOOM_RADIUS = 2d;

    public Damcon() {
        universe = new SimpleUniverse();

        String OBJ_PATH = new File(System.getProperty("user.dir"), "art/models/obj-from-blender/artemis2.obj").getPath();
        try {
            Scene scene = new ObjectFile(ObjectFile.RESIZE).load(OBJ_PATH);
            wireframeifyScene(scene);
            universe.addBranchGraph(scene.getSceneGroup());
        } catch (FileNotFoundException | IncorrectFormatException | ParsingErrorException e) {
            e.printStackTrace();
        }

        // TODO: This is a hack to get rid of the extra window. The reason this creates a new window is explained here:
        // http://download.java.net/media/java3d/javadoc/1.3.2/com/sun/j3d/utils/universe/Viewer.html
        // This might help: https://community.oracle.com/thread/1274674?start=0&tstart=0
        JFrame unused_frame = universe.getViewer().getJFrame(0);
        unused_frame.setVisible(false);
        unused_frame.dispose();

        // This is an attempt to give the Viewer a canvas so it doesn't create its own window (it doesn't render properly)
        /*GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        Viewer viewer = new Viewer(canvas);
        ViewingPlatform viewingPlatform = new ViewingPlatform();
        SimpleUniverse universe = new SimpleUniverse(viewingPlatform, viewer);
        universe.getViewingPlatform().setNominalViewingTransform();
        String OBJ_PATH = new File(System.getProperty("user.dir"), "art/models/obj-from-blender/artemis2.obj").getPath();
        try {
            Scene scene = new ObjectFile(ObjectFile.RESIZE).load(OBJ_PATH);
            wireframeifyScene(scene);
            universe.addBranchGraph(scene.getSceneGroup());
        } catch (FileNotFoundException | IncorrectFormatException | ParsingErrorException e) {
            e.printStackTrace();
        }
        return canvas;*/

        this.canvas = universe.getCanvas();
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
        this.canvas.addMouseWheelListener(this);

        // Camera preset
        ViewingPlatform vp = universe.getViewingPlatform();
        vp.setNominalViewingTransform();

        // http://download.java.net/media/java3d/javadoc/1.3.2/com/sun/j3d/utils/behaviors/vp/OrbitBehavior.html
        /*OrbitBehavior orbit = new OrbitBehavior(this.canvas, OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.DISABLE_TRANSLATE);
        orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0d, 0d, 0d), Double.MAX_VALUE));
        vp.setViewPlatformBehavior(orbit);
        // vp.minRadius(... */
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
                Shape3D s3d = (Shape3D)node;
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
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            // System.out.println("drag!");
            // use lookAt()
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoom_move_distance = ZOOM_FACTOR * e.getPreciseWheelRotation();

        TransformGroup camera = getCamera();
        Transform3D xform = new Transform3D();  // copy camera's transform to xform
        Vector3d cam_pos = new Vector3d();
        camera.getTransform(xform);
        xform.get(cam_pos);                     // copy camera's position to cam_pos

        // Get unit vector representing camera's look/forward
        Vector3d look = new Vector3d(cam_pos);  // we know the camera is looking at the origin so this math is simpler than the general case
        look.normalize();

        // Determine zoom movement vector and apply it to camera position
        Vector3d zoom_move_vec = new Vector3d(look.x * zoom_move_distance, look.y * zoom_move_distance, look.z * zoom_move_distance);
        Vector3d new_cam_pos = new Vector3d(cam_pos.x + zoom_move_vec.x, cam_pos.y + zoom_move_vec.y, cam_pos.z + zoom_move_vec.z);

        // If zoom takes us too close to the object's center (such that it won't fit in the frame or the camera would be inside the object),
        // drop it at the edge of the minimum radius
        if (new_cam_pos.length() < MIN_ZOOM_RADIUS && Math.signum(zoom_move_distance) < 0d) {
            new_cam_pos = new Vector3d(look.x * MIN_ZOOM_RADIUS, look.y * MIN_ZOOM_RADIUS, look.z * MIN_ZOOM_RADIUS);
        }

        // Apply new position to transformation and transformation back to camera
        xform.set(new_cam_pos);
        camera.setTransform(xform);
    }
}