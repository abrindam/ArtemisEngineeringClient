package com.brindyblitz.artemis.engconsole.ui.damcon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.brindyblitz.artemis.utils.AudioManager;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

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

	private static final int HEALTH_QUANTA = 10;
	private static final float HEALTH_QUANTUM_PCT = (1f / HEALTH_QUANTA);
	private static Map<String, Texture[]> nodeTypeToStandardColorizedTextures = new HashMap<>(), nodeTypeToHoveredColorizedTextures = new HashMap<>();
	private String selectableType;

	private Damcon damcon;

	public InternalSelectable(String selectable_type,
			String relative_texture_path, Color color,
			int standard_alpha, int hovered_alpha, float icon_dimension,
			Damcon damcon,
			AudioManager audio_manager) {
		super(audio_manager);

		this.selectableType = selectable_type;
		this.standardAlpha = standard_alpha;
		this.hoveredAlpha = hovered_alpha;
		this.iconDimension = icon_dimension;
		this.damcon = damcon;

		billboard = createBillboard();

		this.image = loadImage(relative_texture_path);

		// Load an array of the same texture colored for health values from 0% to 100%
		// with HEALTH_QUANTA steps, where 0 is 0% and HEALTH_QUANTA-1 is 100%
		if (nodeTypeToStandardColorizedTextures.get(this.selectableType) == null) {
			Texture[] standard_textures = new Texture[HEALTH_QUANTA], hovered_textures = new Texture[HEALTH_QUANTA];
			nodeTypeToStandardColorizedTextures.put(this.selectableType, standard_textures);
			nodeTypeToHoveredColorizedTextures.put(this.selectableType, hovered_textures);

			for (int i = 0; i < HEALTH_QUANTA; i++) {
				float health_pct = HEALTH_QUANTUM_PCT * i;
				standard_textures[i] = loadTexture(this.image, getColorFromHealth(health_pct, standardAlpha));
				hovered_textures[i] = loadTexture(this.image, getColorFromHealth(health_pct, hoveredAlpha));
			}
		}

		standardTexture = nodeTypeToStandardColorizedTextures.get(this.selectableType)[HEALTH_QUANTA - 1];
		hoveredTexture = nodeTypeToHoveredColorizedTextures.get(this.selectableType)[HEALTH_QUANTA - 1];

		Appearance appearance = generateAppearance(standardTexture);

		this.shape = new Shape3D(billboard, appearance);
		this.shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
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
		trans_attrs.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

		TextureAttributes tex_attrs = new TextureAttributes();
		tex_attrs.setTextureMode(TextureAttributes.REPLACE);

		Appearance appearance = new Appearance(); // TODO: PERF > don't get new appearance every time
		appearance.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		appearance.setTexture(tex);
		appearance.setTransparencyAttributes(trans_attrs);
		appearance.setTextureAttributes(tex_attrs);

		return appearance;
	}

	public void setDamageTransparency(boolean transparent) {
		if (this.visible()) {
			Appearance app = this.shape.getAppearance();
			app.setTexture(transparent ? hoveredTexture : standardTexture);
		}
	}

	public void resetDamageTransparency() {
		this.setHovered(this.hovered);
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
		if (this.healthPct == pct)
			return;

		if (this.healthPct > pct) {
			this.damcon.startDamageShake(5000l, 0.7d);
		}

		this.healthPct = pct;

		int texture_index = Math.min((int) (HEALTH_QUANTA * pct), HEALTH_QUANTA -1);
		standardTexture = nodeTypeToStandardColorizedTextures.get(this.selectableType)[texture_index];
		hoveredTexture = nodeTypeToHoveredColorizedTextures.get(this.selectableType)[texture_index];

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