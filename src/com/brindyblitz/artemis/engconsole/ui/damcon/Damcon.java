package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;

// See: http://download.java.net/media/java3d/javadoc/1.5.1/
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.vesseldata.VesselNode;
import net.dhleong.acl.vesseldata.VesselNodeConnection;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Damcon implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final int WIDTH = 400, HEIGHT = 300;

    private static final Color WIREFRAME_COLOR = Color.GREEN; // TODO: vary this color with total ship health if available?

    private static final Transform3D DEFAULT_CAMERA_VIEW = new Transform3D(new double[] {
            0.6954015757171349d, 0.4658852009660681d, -0.5471449789689495d, -2.0244364221851137d,
            -0.0, 0.7613814659810991d, 0.648304144102498d, 2.3987253331792435d,
            0.7186213526539035d, -0.45083172335282545d, 0.5294658711650786d, 1.9590237233107914d,
            0.0d, 0.0d, 0.0d, 1.0d });

    private EngineeringConsoleManager engineeringConsoleManager;
    private Canvas3D canvas;
    private PickCanvas pickCanvas;
    private SimpleUniverse universe;
    private Scene scene = null;
    private static final boolean WINDOW_HACK = true;

    private static final double
            ZOOM_FACTOR = 0.25d,
            ROTATION_FACTOR = 0.01d,
            MIN_PITCH_Y = 0.1d,
            MIN_ZOOM_RADIUS = 3.7d,
            MAX_ZOOM_RADIUS = 15d;
    private static final Vector2d MAX_ROTATION_AMOUNT = new Vector2d(0.125, 0.125);

    private Point lastMouseDragPosition = new Point();
    private boolean rotating = false, dotified = false;

    private static final int[] wireframeLineTypes =
            new int[] { LineAttributes.PATTERN_DASH, LineAttributes.PATTERN_DASH_DOT, LineAttributes.PATTERN_DOT, LineAttributes.PATTERN_SOLID };
    private static final Random random = new Random();

    private Map<GridCoord, InternalNode> internalNodes = new HashMap<>();
    private Set<InternalHallway> internalHallways = new HashSet<>();

    public Damcon(EngineeringConsoleManager engineeringConsoleManager) {
        this.engineeringConsoleManager = engineeringConsoleManager;

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

        loadInternalNodes();
        loadCorridors();

        addMouseListeners();
        setCameraPresets();

        // This allows the UserInterfaceFrame to receive key events.  See:
        // https://community.oracle.com/thread/1276834?start=0&tstart=0
        // And also see other keyboard related comments (UserInterfacerame.keyPressed() and
        // SystemSlider.handleKeyPress()).
        //
        // This also would work (and remove the setFocusable() calls), but is somehow uglier:
        // this.damconCanvas.addKeyListener(slider);
        this.canvas.setFocusable(false);

        this.canvas.setSize(WIDTH, HEIGHT);
    }

    private void loadAndWireframeifyModel() {
        String OBJ_PATH = new File(System.getProperty("user.dir"), "art/models/obj-from-blender/artemis2.obj").getPath();
        try {
            this.scene = new ObjectFile(ObjectFile.RESIZE).load(OBJ_PATH);
            wireframeifyScene(scene, LineAttributes.PATTERN_SOLID);

        } catch (FileNotFoundException | IncorrectFormatException | ParsingErrorException e) {
            e.printStackTrace(System.err);
        }
    }

    private void loadInternalNodes() {
        BranchGroup node_branchgroup = new BranchGroup();

        for (VesselNode vn : this.engineeringConsoleManager.getGrid()) {
            InternalNode in = new InternalNode(vn);
            internalNodes.put(vn.getGridCoord(), in);
            node_branchgroup.addChild(in.getBranchGroup());
        }

        this.universe.addBranchGraph(node_branchgroup);

        pickCanvas = new PickCanvas(this.canvas, node_branchgroup);
        pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
        pickCanvas.setFlags(PickInfo.NODE | PickInfo.CLOSEST_INTERSECTION_POINT);
        pickCanvas.setTolerance(0.5f);

        this.engineeringConsoleManager.addChangeListener(new EngineeringConsoleManager.EngineeringConsoleChangeListener() {
            @Override
            public void onChange() {
                for (Map.Entry<GridCoord, Float> entry : engineeringConsoleManager.getGridHealth().entrySet()) {
                    InternalNode node = internalNodes.get(entry.getKey());
                    node.updateHealth(entry.getValue());
                }
            }
        });
    }

    private void loadCorridors() {
        BranchGroup corridor_bg = new BranchGroup();

        for (VesselNodeConnection vnc : this.engineeringConsoleManager.getGridConnections()) {
            InternalHallway ih = new InternalHallway(vnc);
            internalHallways.add(ih);
            corridor_bg.addChild(ih.getShape());
        }

        this.universe.addBranchGraph(corridor_bg);
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
        getCamera().setTransform(DEFAULT_CAMERA_VIEW);
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
    private static void wireframeifyScene(Scene scene, int line_attribute_pattern) {
        Appearance wireframe = getWireframeAppearance(line_attribute_pattern);

        // TODO: This works for the Artemis OBJ model.  If the scene graph has multiple Shape3D nodes, this would need to be set on all of them.  Is that necessary or can we guarantee it won't be needed?
        Enumeration<Node> children = scene.getSceneGroup().getAllChildren();
        while (children.hasMoreElements()) {
            Node node = children.nextElement();
            if (node.getClass().equals(Shape3D.class)) {
                Shape3D s3d = (Shape3D) node;
                s3d.setAppearance(wireframe);
                try {
                    s3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    // TODO: try scene.setCapability above with BranchGroup.ALLOW_CHILDREN_WRITE etc. and see if this exception is no longer thrown
                    // See http://www.java-tips.org/other-api-tips-100035/119-java3d/1527-how-to-use-the-capability.html
                    // I got it working for the internal node spheres so I probably just need to figure out which node(s) to set it on, or set it on creation
                } catch(RestrictedAccessException e) {
                    // e=Exception in thread "AWT-EventQueue-0" javax.media.j3d.RestrictedAccessException: Cannot modify capability bits on a live or compiled object
                }
            }
        }
    }

    private static Appearance getWireframeAppearance(int line_attributes_pattern) {
        Appearance appearance = new Appearance();

        // Set transparency
        TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, .9f);
        appearance.setTransparencyAttributes(transparency);

        // Enable automatic anti-aliasing
        LineAttributes line_attributes = new LineAttributes();
        line_attributes.setLineAntialiasingEnable(true);
        line_attributes.setLinePattern(line_attributes_pattern);
        appearance.setLineAttributes(line_attributes);

        // Set color
        Color awtColor = WIREFRAME_COLOR;
        Color3f color = new Color3f(awtColor);
        ColoringAttributes coloring_attributes = new ColoringAttributes();
        coloring_attributes.setColor(color);
        appearance.setColoringAttributes(coloring_attributes);

        // Make wireframe
        PolygonAttributes polygon_attributes = new PolygonAttributes();
        polygon_attributes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        polygon_attributes.setCullFace(PolygonAttributes.CULL_NONE);   // allow triangles with normals facing away from the camera to render
        appearance.setPolygonAttributes(polygon_attributes);

        return appearance;
    }

    ///////////
    // Mouse //
    ///////////
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {
            // TODO: damcon selection and orders issuing
            // pickCanvas.setShapeLocation(e);
            // PickInfo[] pickInfos = pickCanvas.pickAll();

            System.out.println("Left click:\n");
            /*for (PickInfo pi : pickInfos) {
                System.out.println(pi);
            }*/
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
        } else if (new_cam_pos.length() > MAX_ZOOM_RADIUS && Math.signum(zoom_move_distance) < 0d) {
            new_cam_pos = new Vector3d(-look.x * MAX_ZOOM_RADIUS, -look.y * MAX_ZOOM_RADIUS, -look.z * MAX_ZOOM_RADIUS);
        }

        // Apply new position to transformation and transformation back to camera
        xform.setTranslation(new_cam_pos);
        camera.setTransform(xform);
    }

    //////////////////
    // Damage Shake //
    //////////////////

    public void toggleDamageShake() {
        dotified = !dotified;
        setDamageShake(dotified);
    }

    public void setDamageShake (boolean enabled) {
        setLineType(enabled ? LineAttributes.PATTERN_DOT : LineAttributes.PATTERN_SOLID);
    }

    public void setLineType(int line_attributes_pattern) {
        wireframeifyScene(this.scene, line_attributes_pattern);
    }

    public void startDamageShake(long duration_ms, double intensity) {
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new DamageShake(duration_ms, intensity), 0, 10);
    }

    private class DamageShake extends TimerTask {
        private long end;
        private double intensity;

        public DamageShake(long duration, double intensity) {
            this.end = System.currentTimeMillis() + duration;
            this.intensity = intensity;
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() >= end) {
                setDamageShake(false);
                this.cancel();
                return;
            }

            // toggleDamageShake();

            //if (random.nextDouble() < this.intensity) {
            //    toggleDamageShake();
            //}

            // setDamageShake(random.nextDouble() < this.intensity);

            if (random.nextDouble() < this.intensity) {
                setLineType(wireframeLineTypes[random.nextInt(wireframeLineTypes.length)]);
            }
        }
    }
}