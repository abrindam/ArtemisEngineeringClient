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
import javax.vecmath.Color3f;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;

public abstract class DamconCanvas {
    private static final Color WIREFRAME_COLOR = Color.GREEN;

    public static Canvas3D buildDamconCanvas() {
        SimpleUniverse universe = new SimpleUniverse();
        universe.getViewingPlatform().setNominalViewingTransform();
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
        JFrame unused_frame = universe.getViewer().getJFrame(0);
        unused_frame.setVisible(false);

        return universe.getCanvas();

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
    }

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
        Appearance app = new Appearance();
        Color awtColor = WIREFRAME_COLOR;
        Color3f color = new Color3f(awtColor);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(color);
        app.setColoringAttributes(ca);
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(pa.POLYGON_LINE);
        pa.setCullFace(pa.CULL_NONE);
        app.setPolygonAttributes(pa);
        return app;
    }
}