package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.brindyblitz.artemis.utils.AudioManager;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public abstract class InternalSelectable extends Internal {
    protected boolean hovered = false;
    protected float healthPct = 1f;

    private QuadArray billboard;
    protected BranchGroup branchGroup;
    protected TransformGroup transformGroup;

    private static final int SOURCE_COLOR_TO_REPLACE_ARGB8 = 0xffffffff;

    protected static Color INVISIBLE_COLOR = new Color(0, 0, 0, 0);

    protected float iconDimension;

    protected Point3f position;

    private static final double TWO_PI = 2d * Math.PI, PI_OVER_TWO = Math.PI / 2d;

    private BufferedImage image;
    private Texture standardTexture, hoveredTexture;
    private int standardAlpha, hoveredAlpha;

    public InternalSelectable(String relative_texture_path, Color color,
                              int standard_alpha, int hovered_alpha, float icon_dimension,
                              AudioManager audio_manager) {
        super(audio_manager);

        this.standardAlpha = standard_alpha;
        this.hoveredAlpha = hovered_alpha;
        this.iconDimension = icon_dimension;

        billboard = createBillboard();

        this.image = loadImage(relative_texture_path);

        standardTexture = loadTexture(this.image, new Color(color.getRed(), color.getGreen(), color.getBlue(), standard_alpha));
        hoveredTexture = loadTexture(this.image, new Color(color.getRed(), color.getGreen(), color.getBlue(), hovered_alpha));

        Appearance appearance = generateAppearance(standardTexture);

        this.shape = new Shape3D(billboard, appearance);
        setPickable(shape);

        this.transformGroup = new TransformGroup();
        this.transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        this.transformGroup.addChild(shape);

        this.branchGroup = new BranchGroup();
        this.branchGroup.addChild(transformGroup);
    }

    protected QuadArray createBillboard() {
        QuadArray qa;
        qa = new QuadArray(4, QuadArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2 | Primitive.GENERATE_NORMALS);
        qa.setCapability(QuadArray.ALLOW_COORDINATE_WRITE);
        // TODO: BUG > this starts off facing the wrong way; maybe just poke camera so it updates?
        // Need update when damcons init too, so maybe just pass in cam pos and call billboardify?
        qa.setCoordinate(0, new Point3f(-iconDimension, -iconDimension, 0));
        qa.setCoordinate(1, new Point3f(iconDimension, -iconDimension, 0));
        qa.setCoordinate(2, new Point3f(iconDimension, iconDimension, 0));
        qa.setCoordinate(3, new Point3f(-iconDimension, iconDimension, 0));
        qa.setTextureCoordinate(0, 0, new TexCoord2f(0f, 0f));
        qa.setTextureCoordinate(0, 1, new TexCoord2f(1f, 0f));
        qa.setTextureCoordinate(0, 2, new TexCoord2f(1f, 1f));
        qa.setTextureCoordinate(0, 3, new TexCoord2f(0f, 1f));
        return qa;
    }

    protected static BufferedImage loadImage(String relative_texture_path) {
        try {
            BufferedImage bi = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/" + relative_texture_path));
            return bi;
        } catch (IOException e) {
            System.err.println("Unable to locate system team icon(s)");
            throw new RuntimeException(e);
        }
    }

    protected static BufferedImage colorizedImage(BufferedImage bi, Color c) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        BufferedImage colorized = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        colorize(colorized, c);
        return colorized;
    }

    protected static Texture loadTexture(BufferedImage bi, Color c) {
        TextureLoader loader = new TextureLoader(colorizedImage(bi, c));
        return loader.getTexture();
    }

    protected static Appearance generateAppearance(Texture tex) {
        // Set transparency; it seems like you can put any value here as long as you set the attributes
        TransparencyAttributes trans_attrs = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.5f);

        TextureAttributes tex_attrs = new TextureAttributes();
        tex_attrs.setTextureMode(TextureAttributes.REPLACE);

        Appearance appearance = new Appearance();
        appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        appearance.setTexture(tex);
        appearance.setTransparencyAttributes(trans_attrs);
        appearance.setTextureAttributes(tex_attrs);

        return appearance;
    }

    //////////////////////////
    // Position/Orientation //
    //////////////////////////

    public void updatePos(float x, float y, float z) {
        this.position = Internal.internalPositionToWorldSpace(x, y, z);
        Transform3D transform = new Transform3D();  // TODO: PERF > don't recreate Transform3D every time
        this.transformGroup.getTransform(transform);
        transform.setTranslation(new Vector3f(this.position));
        this.transformGroup.setTransform(transform);
    }

    public void billboardify(Vector3d cam) {
        // Get vector from node to camera
        Vector3d diff = new Vector3d(cam.x - position.x, cam.y - position.y, cam.z - position.z);
        diff.normalize();

        // Calculate yaw angle
        double yaw_angle = Math.atan2(diff.x, -diff.z);
        yaw_angle += PI_OVER_TWO;
        yaw_angle = normalizeAngle(yaw_angle);

        // Calculate pitch angle
        Vector3d xz_diff = new Vector3d(diff.x, 0d, diff.z);
        double pitch_angle = Math.atan2(diff.y, xz_diff.length());
        pitch_angle = normalizeAngle(pitch_angle);

        // Create yaw transform
        Transform3D yaw_xform = new Transform3D();
        yaw_xform.rotY(yaw_angle);

        // Transform right vector by yaw
        Vector3d right = new Vector3d(1d, 0d, 0d);
        yaw_xform.transform(right);

        // Use transformed right vector to apply pitch
        Transform3D pitch_xform = new Transform3D();
        pitch_xform.setRotation(new AxisAngle4d(right, -pitch_angle));

        // Compose rotation transforms and set translation
        Transform3D composed_xform = new Transform3D();
        composed_xform.mul(pitch_xform, yaw_xform);
        composed_xform.setTranslation(new Vector3d(this.position));

        this.transformGroup.setTransform(composed_xform);
    }

    // Clamp angle in range [0, 2*PI)
    private static double normalizeAngle(double rads) {
        while (rads < 0d) {
            rads += TWO_PI;
        }
        while (rads > 2d * Math.PI) {
            rads -= TWO_PI;
        }
        return rads;
    }

    /////////////
    // Picking //
    /////////////
    protected abstract boolean selected();

    protected boolean visible() {
        return true;
    }

    protected boolean hovered() {
        return this.hovered;
    }

    public void setSelected(boolean selected) { }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;

        Appearance app = this.shape.getAppearance();
        app.setTexture(hovered ? hoveredTexture : standardTexture);
    }

    protected void setPickable(Shape3D shape) {
        shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        shape.setPickable(true);
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }

    //////////////////
    // Health/Color //
    //////////////////

    public void updateHealth(float pct) {
        this.healthPct = pct;

        standardTexture = loadTexture(this.image, getColorFromHealth(this.healthPct, this.standardAlpha));
        hoveredTexture = loadTexture(this.image, getColorFromHealth(this.healthPct, this.hoveredAlpha));

        Appearance app = this.shape.getAppearance();
        app.setTexture(this.hovered ? hoveredTexture : standardTexture);
    }

    protected Color getColorFromHealth(float pct, int alpha) {
        Color c = Color.getHSBColor(getEmptyHue() - (getEmptyHue() - getFullHue()) * pct, 1, 1);    // don't use real alpha here
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    protected float getFullHue() {
        return 120f / 360f;
    }

    protected float getEmptyHue() {
        return 0f;
    }

    protected static void colorize(BufferedImage bi, Color c) {
        int color_argb8_hex = colorToIntARGBHex(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());

        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                if (bi.getRGB(x, y) == SOURCE_COLOR_TO_REPLACE_ARGB8) {
                    bi.setRGB(x, y, color_argb8_hex);
                }
            }
        }
    }

    private static int colorToIntARGBHex(int r, int g, int b, int a) {
        if (colorComponentValid(r) && colorComponentValid(g) && colorComponentValid(b) && colorComponentValid(a)) {
            int result = 0;
            result |= (a << 24);
            result |= (r << 16);
            result |= (g << 8);
            result |= (b);
            return result;
        }
        throw new RuntimeException("Invalid color component specified in color (r,g,b,a)=(" + r + "," + g + "," + b + "," + a + ")");
    }

    private static boolean colorComponentValid(int component) {
        return component >= 0 && component <= 255;
    }
}