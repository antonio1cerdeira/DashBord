package cenario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.AudioDevice;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Material;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.Morph;
import javax.media.j3d.PointLight;
import javax.media.j3d.PointSound;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Screen3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Sound;
import javax.media.j3d.Switch;
import javax.media.j3d.Text3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.View;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Point4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import libproject.shapes.HexagonalPrism;
import libproject.shapes.ImagePanel;
import libproject.shapes.Escalator;
import libproject.shapes.FloorTriangle;
import libproject.shapes.TexturedCylinder;
import libproject.shapes.MechanicalArm;
import libproject.shapes.Octaedro;
import libproject.shapes.Person;
import libproject.utilities.MyMaterials;
import libproject.utilities.TextureApperance;
import libproject.utilities.ViewLocation;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

public class Cenario extends Frame implements MouseListener {
	private boolean state = false;
	private Octaedro octaedro;
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 4.0);
	PickCanvas pc = null;

	Canvas3D cv1 = null;
	Canvas3D offScreenCanvas;
	View view;
	private TransformGroup spin = null;
	ImageComponent2D image = null;
	private JMenuItem exitItem;

	public static void main(String[] args) {
		Frame frame = new Cenario();
		frame.setPreferredSize(new Dimension(960, 480));
		frame.setTitle("Cenario");
		frame.pack();
		frame.setVisible(true);

	}

	public Cenario() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		cv1 = new Canvas3D(gc);
		cv1.addMouseListener(this);

		WindowListener wl = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(wl);

		setLayout(new GridLayout(1, 1));
		add(cv1);

		setLayout(new BorderLayout()); // Use BorderLayout for the main frame

		// Create a panel to hold the menu bar and canvas
		JPanel mainPanel = new JPanel(new BorderLayout());
		add(mainPanel);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		exitItem = new JMenuItem("Exit");

		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		mainPanel.add(menuBar, BorderLayout.NORTH);
		mainPanel.add(cv1, BorderLayout.CENTER);

		setVisible(true);

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage capturedImage = captureImage();
				saveImage(capturedImage);
			}
		});

		SimpleUniverse su = new SimpleUniverse(cv1, 2);
		su.getViewingPlatform().setNominalViewingTransform();

		Transform3D ViewTr = new Transform3D();
		ViewTr.lookAt(new Point3d(0, 1, -2), new Point3d(0.3, 0.5, 0.5), new Vector3d(0, 1, 0));

		ViewTr.invert();
		su.getViewingPlatform().getViewPlatformTransform().setTransform(ViewTr);

		BranchGroup bg = createSceneGraph(su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0));
		bg.compile();
		su.addBranchGraph(bg);

		OrbitBehavior orbit = new OrbitBehavior(cv1, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		pc = new PickCanvas(cv1, bg);
		pc.setMode(PickTool.GEOMETRY);

		AudioDevice audioDev = new JavaSoundMixer(su.getViewer().getPhysicalEnvironment());
		audioDev.initialize();

		// off-screen
		view = su.getViewer().getView();
		offScreenCanvas = new Canvas3D(gc, true);

		view = su.getViewer().getView();
		offScreenCanvas = new Canvas3D(gc, true);

		Screen3D sOn = cv1.getScreen3D();
		Screen3D sOff = offScreenCanvas.getScreen3D();
		Dimension dim = sOn.getSize();
		sOff.setSize(dim);
		sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
		sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());
		Point loc = cv1.getLocationOnScreen();
		offScreenCanvas.setOffScreenLocation(loc);
	}

	private BranchGroup createSceneGraph(TransformGroup viewTg) {
	    BranchGroup root = new BranchGroup();

	    // Adiciona cubos e triângulos alternados
	    addInterleavedShapes(root);

	    Font awtFont = new Font("SansSerif", Font.PLAIN, 1);

	    Font3D font3D = new Font3D(awtFont, new FontExtrusion());

	    // Create a Text3D object with the desired text, font, and appearance
	    Text3D text3D = new Text3D(font3D, "Fun Area", new Point3f(-2f, 1f, -3.0f));

	    // Set appearance for the text (customize as needed)
	    Appearance textAppearance = new Appearance();
	    ColoringAttributes textColor = new ColoringAttributes(new Color3f(Color.BLUE),
	            ColoringAttributes.SHADE_GOURAUD);
	    textAppearance.setColoringAttributes(textColor);

	    // Create a Shape3D for the Text3D object
	    Shape3D textShape = new Shape3D(text3D, textAppearance);

	    // Wrap the text in a TransformGroup for applying scale
	    Transform3D scaleTransform = new Transform3D();
	    scaleTransform.setScale(0.35); // Adjust the scale factor as needed

	    TransformGroup scaleGroup = new TransformGroup(scaleTransform);
	    scaleGroup.addChild(textShape);

	    // Add the scaled Text3D to the scene
	    root.addChild(scaleGroup);

	    FloorTriangle floor = new FloorTriangle(10, -2f, 2f, new Color3f(Color.BLUE), new Color3f(Color.white), true);
	    root.addChild(floor);

	    Appearance appIRON = new TextureApperance(this, "images/Iron.png", new MyMaterials(MyMaterials.IRON), true,
	            false);

	    Appearance appBRASS = new TextureApperance(this, "images/cobre.png", new MyMaterials(MyMaterials.BRASS), true,
	            false);

	    TransformGroup tg2 = new TransformGroup();
	    TransformGroup spin = new TransformGroup();
	    spin.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	    MechanicalArm arm = new MechanicalArm(0.2f, appIRON, appBRASS);

	    scaleTransform = new Transform3D();
	    scaleTransform.setScale(0.45);

	    TransformGroup scaleGroup2 = new TransformGroup(scaleTransform);
	    scaleGroup2.addChild(arm);

	    spin.addChild(scaleGroup2);

	    Alpha alpha = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 12000, 5000, 0, 2000, 1000,
	            0);
	    RotationInterpolator rotator = new RotationInterpolator(alpha, spin);
	    rotator.setSchedulingBounds(new BoundingSphere());

	    spin.addChild(rotator);

	    tg2.addChild(spin);
	    root.addChild(tg2);

	    Person person = new Person(0.2f, appIRON, appBRASS);
	    viewTg.addChild(person);

	    KeyControl keyControl = new KeyControl(viewTg, person);
	    keyControl.setSchedulingBounds(bounds);

	    root.addChild(keyControl);

	    Appearance appRUBBER = new Appearance();
	    appRUBBER.setMaterial(new MyMaterials(MyMaterials.RUBBER));

	    Appearance appWOOD = new TextureApperance(this, "images/Wood.jpg", new MyMaterials(MyMaterials.WOOD), true,
	            false);

	    Escalator escalator = new Escalator(1.7f, 0.05f, 0.3f, appWOOD, appRUBBER);
	    Transform3D tr = new Transform3D();
	    tr.setTranslation(new Vector3f(0, 0.075f, 1.4f));
	    TransformGroup tg = new TransformGroup(tr);
	    tg.addChild(escalator);
	    root.addChild(tg);

	    Escalator escalator2 = new Escalator(1.3f, 0.05f, 0.3f, appWOOD, appRUBBER);
	    tr = new Transform3D();
	    tr.rotY(Math.toRadians(90));
	    tr.setTranslation(new Vector3f(-1.4f, 0.075f, 0f));
	    tg = new TransformGroup(tr);
	    tg.addChild(escalator2);
	    root.addChild(tg);

	    Escalator escalator3 = new Escalator(1.7f, 0.05f, 0.3f, appWOOD, appRUBBER);
	    tr = new Transform3D();

	    tr.setTranslation(new Vector3f(0, 0.075f, -1.4f));

	    tg = new TransformGroup(tr);
	    tg.addChild(escalator3);
	    root.addChild(tg);

	    Escalator escalator4 = new Escalator(0.5f, 0.05f, 0.3f, appWOOD, appRUBBER);
	    tr = new Transform3D();

	    Transform3D rotationY = new Transform3D();
	    rotationY.rotY(Math.toRadians(90));

	    Transform3D rotationZ = new Transform3D();
	    rotationZ.rotZ(Math.toRadians(35));

	    tr.mul(rotationY);
	    tr.mul(rotationZ);

	    tr.setTranslation(new Vector3f(1.4f, 0.35f, 0.7f));
	    tg = new TransformGroup(tr);
	    tg.addChild(escalator4);
	    root.addChild(tg);

	    Escalator escalator5 = new Escalator(0.5f, 0.05f, 0.3f, appWOOD, appRUBBER);
	    tr = new Transform3D();

	    rotationY = new Transform3D();
	    rotationY.rotY(Math.toRadians(90));

	    rotationZ = new Transform3D();
	    rotationZ.rotZ(Math.toRadians(-35));

	    tr.mul(rotationY);
	    tr.mul(rotationZ);

	    tr.setTranslation(new Vector3f(1.4f, 0.35f, -0.7f));

	    tg = new TransformGroup(tr);
	    tg.addChild(escalator5);
	    root.addChild(tg);

	    Escalator escalator6 = new Escalator(0.4f, 0.05f, 0.3f, appWOOD, appRUBBER);
	    tr = new Transform3D();
	    tr.rotY(Math.toRadians(90));
	    tr.setTranslation(new Vector3f(1.4f, 0.62f, 0f));

	    tg = new TransformGroup(tr);
	    tg.addChild(escalator6);
	    root.addChild(tg);

	    for (int i = 0; i < 6; i++) {
	        TexturedCylinder coca = new TexturedCylinder(createTextureAppearance2("images/Lata.jpg"), 30, 0.8f, 0.2f);
	        tr = new Transform3D();
	        tr.rotX(Math.toRadians(-90));
	        tr.setTranslation(new Vector3f(-0.5f + i * 0.1f, 0.005f, -0.6f));
	        tr.setScale(0.2);
	        tg = new TransformGroup(tr);
	        tg.addChild(coca);
	        root.addChild(tg);
	    }

	    URL url = getClass().getClassLoader().getResource("images/1.wav");
	    MediaContainer mc = new MediaContainer(url);
	    BackgroundSound bSound = new BackgroundSound(mc, 0.001f);
	    bSound.setSchedulingBounds(bounds);
	    bSound.setLoop(Sound.INFINITE_LOOPS);
	    bSound.setEnable(true);
	    root.addChild(bSound);

	    PointLight pLight4 = new PointLight(new Color3f(Color.white), new Point3f(2f, 2f, 2f),
	            new Point3f(1.5f, 0f, -1f));
	    pLight4.setCapability(PointLight.ALLOW_STATE_READ);
	    pLight4.setCapability(PointLight.ALLOW_STATE_WRITE);
	    pLight4.setInfluencingBounds(bounds);
	    root.addChild(pLight4);

	    PointLight pLight5 = new PointLight(new Color3f(Color.white), new Point3f(-2f, 2f, -2f),
	            new Point3f(-1f, 0f, 1.5f));
	    pLight5.setCapability(PointLight.ALLOW_STATE_READ);
	    pLight5.setCapability(PointLight.ALLOW_STATE_WRITE);
	    pLight5.setInfluencingBounds(bounds);
	    root.addChild(pLight5);

	    addPointLight(root, 2f, 2f, 2f, 1.5f, 0f, 1.5f, Color.red);
	    addPointLight(root, -2f, 2f, -2f, -1.5f, 0f, -1.5f, Color.yellow);
	    addPointLight(root, 0f, 2f, 0f, 1f, 0.2f, 0f, Color.blue);

	    AmbientLight aLight = new AmbientLight(true, new Color3f(3f, 3f, 3f));
	    aLight.setInfluencingBounds(bounds);
	    root.addChild(aLight);

	    Appearance app = new Appearance();
	    app.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

	    Background bk = new Background();
	    bk.setApplicationBounds(bounds);

	    url = getClass().getClassLoader().getResource("images/blue.jpg");
	    BufferedImage bi = null;
	    try {
	        bi = ImageIO.read(url);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bi);

	    bk.setCapability(Background.ALLOW_COLOR_WRITE);
	    bk.setCapability(Background.ALLOW_IMAGE_READ);
	    bk.setCapability(Background.ALLOW_IMAGE_WRITE);

	    bk.setImage(image);

	    root.addChild(bk);

	    TransformGroup bbTg = new TransformGroup();
	    bbTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    root.addChild(bbTg);
	    Billboard bb = new Billboard(bbTg, Billboard.ROTATE_ABOUT_POINT, new Point3f(0f, 0f, -2f));
	    bb.setSchedulingBounds(bounds);
	    bbTg.addChild(bb);

	    tr = new Transform3D();
	    tr.setTranslation(new Vector3f(0f, 0.5f, 0 - 1f));
	    tg = new TransformGroup(tr);
	    ImagePanel imagePanel = new ImagePanel(this, "images/funArea.png", 0.5f);

	    tg.addChild(imagePanel);
	    bbTg.addChild(tg);

	    return root;
	}
	private void addPointLight(BranchGroup root, float x, float y, float z, float atX, float atY, float atZ,
			Color color) {
		PointLight pLight = new PointLight(new Color3f(color), new Point3f(x, y, z), new Point3f(atX, atY, atZ));
		pLight.setCapability(PointLight.ALLOW_STATE_READ);
		pLight.setCapability(PointLight.ALLOW_STATE_WRITE);
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);
	}

	Appearance createTextureAppearance2(String file) {
		Appearance app = new Appearance();
		URL filename = getClass().getClassLoader().getResource(file);
		TextureLoader loader = new TextureLoader(filename, this);
		ImageComponent2D image = loader.getImage();
		if (image == null) {
			System.out.println("can't find texture file.");
		}
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		texture.setEnable(true);
		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		app.setTexture(texture);

		app.setMaterial(new Material());
		TextureAttributes texatt = new TextureAttributes();
		texatt.setTextureMode(TextureAttributes.COMBINE);
		app.setTextureAttributes(texatt);

		return app;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		pc.setShapeLocation(e);
		PickResult result = pc.pickClosest();
		System.out.println("Mouse clicked!");
		if (result != null) {
			TransformGroup nodeTg = (TransformGroup) result.getNode(PickResult.TRANSFORM_GROUP);
			if (nodeTg != null) {

				System.out.println(nodeTg.toString());

				Transform3D tr = new Transform3D();
				nodeTg.getTransform(tr);

				Transform3D rot = new Transform3D();
				rot.rotY(Math.PI / 8);
				tr.mul(rot);

				nodeTg.setTransform(tr);
			}

			Shape3D nodeS = (Shape3D) result.getNode(PickResult.SHAPE3D);
			if (nodeS != null) {
				if (nodeS.toString().contains("Octaedro")) {

					System.out.println(nodeS.toString());
					state = !state;
					octaedro.setSolid(state);

				}
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void saveImage(BufferedImage bImage) {
		// save image to file
		JFileChooser chooser = new JFileChooser();

		// Set a default file name
		String defaultFileName = "frame.jpg";
		chooser.setSelectedFile(new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + defaultFileName));
		chooser.updateUI();

		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File oFile = chooser.getSelectedFile();
			try {
				ImageIO.write(bImage, "jpeg", oFile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public BufferedImage captureImage() {

		Dimension dim = cv1.getSize();

		view.stopView();
		view.addCanvas3D(offScreenCanvas);

		BufferedImage bImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);

		ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage);
		offScreenCanvas.setOffScreenBuffer(buffer);
		view.startView();

		offScreenCanvas.renderOffScreenBuffer();
		offScreenCanvas.waitForOffScreenRendering();

		bImage = offScreenCanvas.getOffScreenBuffer().getImage();

		view.removeCanvas3D(offScreenCanvas);

		return bImage;
	}

	private static Appearance createAppearance(Color3f color) {
		Appearance appearance = new Appearance();
		ColoringAttributes coloringAttributes = new ColoringAttributes(color, ColoringAttributes.SHADE_GOURAUD);
		appearance.setColoringAttributes(coloringAttributes);
		return appearance;
	}

	private TransformGroup createBoxTransformGroup(Vector3d translation, Box box) {
		Transform3D transformBox = new Transform3D();
		transformBox.setTranslation(translation);
		TransformGroup tgBox = new TransformGroup(transformBox);
		tgBox.addChild(box);
		return tgBox;
	}

	private void addInterleavedShapes(BranchGroup root) {
		float size = 0.15f; // Tamanho de cada forma
		int count = 6; // Número total de formas
		float spacing = 0.3f; // Espaçamento entre formas
		float startX = -1.0f; // Posição inicial no eixo X
		float y = 0.2f; // Altura no eixo Y
		float z = -0.5f; // Profundidade fixa

		for (int i = 0; i < count; i++) {
			Transform3D transform = new Transform3D();
			transform.setTranslation(new Vector3d(startX + i * spacing, y, z));
			TransformGroup tg = new TransformGroup(transform);

			if (i % 2 == 0) {
				// Adiciona um cubo
				Box box = new Box(size, size, size, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS,
						createAppearance(
								new Color3f((float) Math.random(), (float) Math.random(), (float) Math.random())));
				tg.addChild(box);
			} else {
				// Adiciona um triângulo
				Shape3D triangle = createTriangle1(size);
				tg.addChild(triangle);
			}
			root.addChild(tg);
		}
	}

	private Shape3D createTriangle1(float size) {
		Point3f[] vertices = new Point3f[] { new Point3f(-size, -size, 0.0f), new Point3f(size, -size, 0.0f),
				new Point3f(0.0f, size, 0.0f) };

		Color3f[] colors = new Color3f[] { new Color3f(Color.RED), new Color3f(Color.GREEN), new Color3f(Color.BLUE) };

		GeometryArray geometry = new TriangleArray(3, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		geometry.setCoordinates(0, vertices);
		geometry.setColors(0, colors);

		Appearance appearance = new Appearance();
		return new Shape3D(geometry, appearance);
	}

	private Appearance createAppearance1(Color3f color) {
		Appearance appearance = new Appearance();
		ColoringAttributes coloringAttributes = new ColoringAttributes(color, ColoringAttributes.SHADE_GOURAUD);
		appearance.setColoringAttributes(coloringAttributes);
		return appearance;
	}

}
