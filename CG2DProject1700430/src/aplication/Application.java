package aplication;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import libproject.shapes.DrawDrag;//Desenho de barras deslizantes interativas, Painel SetValuesPanel para controlo de valores
import libproject.shapes.DrawSemiCircule;//Desenho de semicírculos,Painel HapLevPanel para o gráfico semicircular
import libproject.shapes.DrawGraphProfit;//Desenho de gráficos de barras ou áreas, Painel waitingTimePanel para os tempos de espera
import libproject.utilities.ProjectFunction;//Funções utilitárias para operações genéricas, Centralização de texto e ajustes geométricos

//Desenha a class principal porque extende a jpanel 
public class Application extends JFrame implements ActionListener {
	
//declaração dos paineis todos 
	ImagePanel image;//Painel das imagens manipula imagens.
	HapLevPanel happyLevel;//Mostra um gráfico semicircular de "nível de felicidade".
	SetValuesPanel setValues;//Contém os controles deslizantes para ajustar valores.
	HappyLevelApp mainPanel;// Painel principal com animações de estrelas
	waitingTimePanel waitGrap;// Painel que exibe o gráfico de barras dos tempos de espera
	
	PrinterJob pj;// impresão dos graficos 

	JFileChooser fc = new JFileChooser();//abrir e fechar os ficheiros jpg

//Método principal que inicia o progrma 
	public static void main(String[] args) {
		JFrame frame = new Application();
		frame.setTitle("SuperMegaDashBoard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new HappyLevelApp();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);

	}
//Construtor de classe Application
	public Application() {
		// Inicialização dos painéis
		mainPanel = new HappyLevelApp();// Painel principal
		waitGrap = new waitingTimePanel();// Painel do gráfico de barras
		image = new ImagePanel();// Painel para manipulação de imagens
		happyLevel = new HapLevPanel();// Painel do gráfico semicircular	
		setValues = new SetValuesPanel();// Painel dos controles deslizantes
		
		// Adiciona bordas a cada painel para separação visual
		setBorderToPanel(mainPanel, 3);
		setBorderToPanel(waitGrap, 3);
		setBorderToPanel(image, 3);
		setBorderToPanel(happyLevel, 3);
		
		setBorderToPanel(setValues, 3);
		// Usa JLayeredPane para organizar os painéis em camadas
		JLayeredPane layeredPane = getLayeredPane();
		// Adiciona os painéis às camadas
		layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(waitGrap, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(image, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(happyLevel, JLayeredPane.PALETTE_LAYER);
	

		layeredPane.add(setValues, JLayeredPane.PALETTE_LAYER);
		// Configura as posições e tamanhos dos painéis
		int xStart = 75;
		int yStart = 100;
		int panelWidth = 250;
		int panelHeight = 250;
		int spacing = 25;

		// Define as posições de cada painel no layout
		setValues.setBounds(xStart, yStart, 2 * panelWidth + spacing, panelHeight);
		happyLevel.setBounds(xStart + 2 * panelWidth + 2 * spacing, yStart, panelWidth, panelHeight);
		image.setBounds(xStart, yStart + panelHeight + spacing, panelWidth, panelHeight);
		waitGrap.setBounds(xStart + panelWidth + spacing, yStart + panelHeight + spacing, 2 * panelWidth + spacing,
				panelHeight);

		// add menus
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);

		// file menu
		JMenu menu = new JMenu("File");
		JMenuItem mi = new JMenuItem("Insert Foto");
		mi.addActionListener(this);
		menu.add(mi);

		mi = new JMenuItem("Save Foto");
		mi.addActionListener(this);
		menu.add(mi);

		menu.addSeparator();
		mi = new JMenuItem("Exit");
		mi.addActionListener(this);
		menu.add(mi);

		mb.add(menu);

		// Edit product image menu
		menu = new JMenu("Edit Foto");
		mi = new JMenuItem("Contrast");
		mi.addActionListener(this);
		menu.add(mi);
		mi = new JMenuItem("Brighten");
		mi.addActionListener(this);
		menu.add(mi);
		mi = new JMenuItem("Darken");
		mi.addActionListener(this);
		menu.add(mi);
		mb.add(menu);

		// Print menu
		menu = new JMenu("Print");
		mi = new JMenuItem("Graph 1");
		mi.addActionListener(this);
		menu.add(mi);
		mi = new JMenuItem("Graph 2");
		mi.addActionListener(this);
		menu.add(mi);

		mb.add(menu);

	}
	// Método para adicionar uma borda preta a um painel
	private void setBorderToPanel(JPanel panel, int borderWidth) {
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, borderWidth));
	}

	// Implementação do ActionListener para tratar eventos de menu
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();// Obtém o comando associado ao evento
		// Caso o comando seja para inserir uma fot
		if (cmd.equals("Insert Foto")) {
			int returnVale = fc.showOpenDialog(this);
			if (returnVale == JFileChooser.APPROVE_OPTION) {
				try {
					File selectedFile = fc.getSelectedFile();
					// System.out.println("Selected File: " + selectedFile.getAbsolutePath());
					BufferedImage bi = ImageIO.read(fc.getSelectedFile());
					image.setImage(bi);
					pack();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		} else if (cmd.equals("Save Image")) { // Caso o comando seja para salvar uma imagem
			int returnVale = fc.showSaveDialog(this);
			if (returnVale == JFileChooser.APPROVE_OPTION) {
				try {
					ImageIO.write(image.getImage(), "png", fc.getSelectedFile());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
//sair do programa
		} else if (cmd.equals("Exit")) {
			System.exit(0);
		} else
			process(cmd);

	}
//para procesar a imaguem 
	private void process(String cmd) {
		BufferedImageOp op = null;//processamento de imagem 
		
		// para ajustar o contraste 
		if (cmd.equals("Contrast")) {
			float[] data = { 0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f };
			Kernel k = new Kernel(3, 3, data);
			op = new ConvolveOp(k);
			pack();
		} else if (cmd.equals("Brighten")) {//para ajustar a iluminação 
			image.setImage(WhiteMode(image.getImage()));
			pack();

		} else if (cmd.equals("Darken")) {// para escurecer a imaguem 
			image.setImage(BlackMode(image.getImage()));
			pack();

		} else//para fazer a impresão 
			process2(cmd);

		if (op != null) {
			image.setImage(op.filter(image.getImage(), null));//aplica o filtro a imaguem 
			pack();//atualiza o layoyut da janela 

		}
		pack();//atualiza o layout 
	}
	//metodo para a imprimir 
	private void process2(String cmd) {

		pj = PrinterJob.getPrinterJob();//instacia da impressão 
		//para imprimir o graf 1
		if (cmd.equals("Graph 1")) {

			pj.setPrintable(waitGrap);//define o painel 1 para imprimir
			if (pj.printDialog()) {//Exibe a box para a impressao 
				try {
					pj.print();

				} catch (PrinterException ex) {//tratamento de erros
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error during printing: " + ex.getMessage(), "Printing Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}
			pack();
			//mesma coisa mas para o graf 2
		} else if (cmd.equals("Graph 2")) {
			pj.setPrintable(happyLevel);
			if (pj.printDialog()) {
				try {
					pj.print();

				} catch (PrinterException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error during printing: " + ex.getMessage(), "Printing Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			pack();
		}
		} 
//Método para o brilho da imaguem 
	private BufferedImage WhiteMode(BufferedImage imgIn) {
		//Cria uma nova imagem de saída com as mesmas dimensões e tipo da imagem de entrada
		BufferedImage imgOut = new BufferedImage(imgIn.getWidth(), imgIn.getHeight(), imgIn.getType());
		
		// Obtém os dados rasterizadoso (matriz de pixeis) da imagem de entrada e saída
		WritableRaster rasterImgIn = imgIn.getRaster();
		WritableRaster rasterImgOut = imgOut.getRaster();
		// Array para armazenar os valores de Red, Green, Blue e Alpha
		int[] rgba = new int[4];
		//ciclo corre os pixeis 
		for (int x = 0; x < imgIn.getWidth(); x++) {//colunas 
			for (int y = 0; y < imgIn.getHeight(); y++) {//linhas 
				rasterImgIn.getPixel(x, y, rgba);//valores rgba 
				
				// Aumenta o brilho multiplicando os valores RGB por 1.1

				rgba[0] = (int) (rgba[0] * 1.1);
				rgba[1] = (int) (rgba[1] * 1.1);
				rgba[2] = (int) (rgba[2] * 1.1);
//para nao garantrir que o maximo nao é ultrapassado 
				if (rgba[0] > 255) {
					rgba[0] = 255;
				}

				if (rgba[1] > 255) {
					rgba[1] = 255;
				}

				if (rgba[2] > 255) {
					rgba[2] = 255;
				}
//define os valor de saida 
				rasterImgOut.setPixel(x, y, rgba);
			}

		}
		return imgOut; // devolve a imaguem 
	}
//escurecer imagem
	private BufferedImage BlackMode(BufferedImage imgIn) {
		//Cria uma nova Imaguem de saida com as mesma dimensoes de tipo imagem
		BufferedImage imgOut = new BufferedImage(imgIn.getWidth(), imgIn.getHeight(), imgIn.getType());
		//matriz de pixeis 
		WritableRaster rasterImgIn = imgIn.getRaster();
		WritableRaster rasterImgOut = imgOut.getRaster();
		
		int[] rgba = new int[4]; //armazenar valores rgb
		
		for (int x = 0; x < imgIn.getWidth(); x++) {
			for (int y = 0; y < imgIn.getHeight(); y++) {
				rasterImgIn.getPixel(x, y, rgba);
				rgba[0] = (int) (rgba[0] * 0.9);//red
				rgba[1] = (int) (rgba[1] * 0.9);//green
				rgba[2] = (int) (rgba[2] * 0.9);//blue
//define a saida 
				rasterImgOut.setPixel(x, y, rgba);
			}
		}
		return imgOut;
	}

}
//painel gráfico interativo com animações e eventos de teclado e rato
class HappyLevelApp extends JPanel implements KeyListener, Runnable, MouseListener, MouseMotionListener {
	// Regras de composição para manipulação de transparência
	int[] rules = { AlphaComposite.SRC_OVER, AlphaComposite.CLEAR, AlphaComposite.SRC_IN, AlphaComposite.DST_OUT,
			AlphaComposite.SRC };
	int ruleIndex;
	int position;
	float opacity = 0.4f;
	int Gradient = 0;
	Shape arealogo;
	Point2D.Double[] pts = new Point2D.Double[100];

	public HappyLevelApp() {
		setPreferredSize(new Dimension(1000, 625));// Define o tamanho do painel
		setBackground(Color.white);
		addKeyListener(this);// Adiciona eventos de teclado
		addMouseListener(this);// Adiciona evento do rato
		addMouseMotionListener(this);// Adiciona movimento do rato
		setFocusable(true);// Permite que o painel receba do teclado

		Random random = new Random();
//inicio dos desenhos das estrelas para o fundo animado 
		for (int i = 0; i < pts.length; i++) {
			pts[i] = new Point2D.Double(Math.random(), Math.random());
		}
//inicia o para GUI para as animaçoes
		Thread thread = new Thread(this);
		thread.start();
		
	}
//metodo responsavel pelo desenho do painel 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);//limpar 
		Graphics2D g2 = (Graphics2D) g;

		requestFocus();//Granrante que o painel recebe eventos do teclado 
		//Paint
		// Desenha o fundo com gradiente
		Color startColor = new Color(0, 128, 0);
		Color finishedColor = new Color(139, 0, 0);  
		GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), finishedColor, false);
		g2.setPaint(gp);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		// Desenha as estrelas animadas
		g.setColor(Color.black);

		for (int i = 0; i < pts.length; i++) {
			int x = (int) (1350 * pts[i].x);// Calcula a posição X
			int y = (int) (625 * pts[i].y);// Calcula a posição Y
			int size = (int) (50 * Math.random());// Tamanho aleatório para cada estrela
			// shapes personalisadas
			GeneralPath star = new GeneralPath();
	        // Posições das 5 pontas da estrela
	        star.moveTo(x, y - size); // topo
	        star.lineTo(x + size / 4, y - size / 2); // ponta direita
	        star.lineTo(x + size, y - size / 4); // ponta inferior direita
	        star.lineTo(x + size / 2, y + size / 4); // ponta inferior
	        star.lineTo(x + size / 2, y + size); // parte inferior direita
	        star.lineTo(x - size / 2, y + size); // parte inferior esquerda
	        star.lineTo(x - size / 2, y + size / 4); // ponta inferior esquerda
	        star.lineTo(x - size, y - size / 4); // ponta superior esquerda
	        star.lineTo(x - size / 4, y - size / 2); // ponta esquerda
	        star.closePath(); // fecha o caminho, voltando ao topo

	        g2.draw(star); // desenha a estrela
		}
		//shapes
		// Desenha o logotipo (um círculo com recorte no centro)
		Ellipse2D logo = new Ellipse2D.Double(75, 10, 50, 50); // Círculo externo
		Ellipse2D logo2 = new Ellipse2D.Double(87.5, 22.5, 25, 25);// Círculo interno

		Area arealogo = new Area(logo);
		Area arealogo2 = new Area(logo2);

		arealogo.subtract(arealogo2);// Subtrai o círculo interno do externo para criar o recorte
		
		// Alterna entre dois gradientes para o logotipo
		switch (Gradient) {
		case 0: {
			GradientPaint gp2 = new GradientPaint(10, 50, Color.black, 75, 65, Color.orange, true);
			g2.setPaint(gp2);
			g2.fill(arealogo);
			break;
		}

		case 1: {
			GradientPaint gp2 = new GradientPaint(10, 50, Color.white, 100, 35, Color.black, true);
			g2.setPaint(gp2);
			g2.fill(arealogo);
			break;
		}

		}
		// Desenha o título "Mega Xpto Dashboard"
		String text = "Mega Xpto DashBord";
		Font font2 = new Font("Times New Roman", Font.BOLD,25);
		FontRenderContext fcr2 = g2.getFontRenderContext();

		g2.setFont(font2);
		g2.setColor(Color.green);
		g2.drawString(text, 90, 50);
		// Configura a transparência usando AlphaComposite
		AlphaComposite ac = AlphaComposite.getInstance(rules[ruleIndex], opacity);
		g2.setComposite(ac);
		
		  // Desenha o texto "Xpto" no canto superior direito
		String product = "Xpto";
		Font font = new Font("Serif", Font.BOLD, 30);
		g2.setFont(font);
		g2.setColor(Color.yellow);

		// Obter a largura do texto
		FontMetrics metrics = g2.getFontMetrics(font);
		int textWidth = metrics.stringWidth(product);
		int textHeight = metrics.getHeight();

		// Calcular posição para o canto superior direito
		int x = getWidth() - textWidth - 20; // 20 pixels de margem da direita
		int y = textHeight + 20; // 20 pixels a partir do topo

		// Desenhar o texto no canto superior direito
		g2.drawString(product, x, y);
		
	}
	   // Eventos do teclado
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {

		case KeyEvent.VK_UP: {// Alterna para a próxima regra de composição
			ruleIndex++;
			ruleIndex %= rules.length;
			break;
		}
		case KeyEvent.VK_DOWN: {// Alterna para a regra anterior de composição
			ruleIndex--;
			if (ruleIndex < 0)
				ruleIndex = rules.length - 1;
			break;
		}

		case KeyEvent.VK_LEFT: {// Diminui a opacidade
			opacity -= 0.1f;
			if (opacity < 0)
				opacity = 0;
			break;
		}

		case KeyEvent.VK_RIGHT: {
			opacity += 0.1f;
			if (opacity > 1)
				opacity = 1;
			break;
		}

		}
		repaint();// Atualiza o painel
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	// Animação contínua para mover os pontos das estrelas
	@Override
	public void run() {
		while (true) {
			for (int i = 0; i < pts.length; i++) {
				double x = pts[i].getX();
				double y = pts[i].getY();
				y += 0.05 * Math.random();
				if (y > 1) {
					y = 0.15 * Math.random();
					x = Math.random();
				}
				pts[i].setLocation(x, y);
			}
			repaint();
			try {
				Thread.sleep(250);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}
//Eventos do rato
	@Override
	public void mousePressed(MouseEvent e) {
		if (arealogo.contains(e.getPoint())) {
			if (Gradient == 0)
				Gradient = 1;
			else
				Gradient = 0;
		}

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

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
//Representa um (sliders)
//ajusta valores percentuais no semicirculo
//mas na realidade a e barra de happy 
class SetValuesPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	// Dimensões e espaçamento para os objetos deslizantes
	int spacing = 70;// Espaço entre os sliders
	int height = 50;// Altura do slider
	int thickness = 10;// Espessura do slider

	int width = 250;// Largura da área de deslizamento

	public static float percentual;
	public static float percentual2;
	public static float percentual3;
	
	// Formas geométricas que representam os sliders
	Shape obj = new Rectangle2D.Double(67, 90, 15, 40);
	Shape obj1 = new Ellipse2D.Double(67, 90 + spacing + height, 40, 40);  // Círculo com diâmetro de 40
	Shape obj2 = new Rectangle2D.Double(67, 90 + 2 * spacing + height * 2, 15, 40);
	 // Transformação geométrica usada para mover os sliders
	AffineTransform at = new AffineTransform();

	// Array para armazenar as posições pré-definidas dos sliders (não utilizado completamente)
	Point position1[] = new Point[47];
	// Configuração dos botões, seleção e deslocamento inicial
	int r = 4;// Raio de ajuste (não utilizado diretamente no código atual)
	int maxDist = 5;// Distância máxima para fixar sliders (não implementado para todos os sliders)

	int button = -1;// Indica qual slider está sendo arrastado (-1: nenhum)
	Boolean selected = false;// Indica se algum slider foi selecionado
	int firstX = 0;// Variáveis slider1
	int firstY = 0;
	int deltaX = 0;
	int deltaY = 0;

	int firstX1 = 0;
	int firstY1 = 0;
	int deltaX1 = 0;
	int deltaY1 = 0;

	int firstX2 = 0;
	int firstY2 = 0;
	int deltaX2 = 0;
	int deltaY2 = 0;
	
	 JButton myButton;// Botão para redefinir os sliders

	// Construtor da classe
	public SetValuesPanel() {
		// Configurações do painel
		setPreferredSize(new Dimension(300,450));
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		 setLayout(null);
		 myButton = new JButton("Clear");
	        myButton.setBounds(0, 420, 300, 30); 
	        myButton.setBackground(new Color(255,50,50));
	        myButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
	        myButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	resetObjects();
	            }
	        });	
	
	        add(myButton);
	        // Inicializa as posições de referência para os sliders
		for (int i = 0; i < 47; i++) {
			position1[i] = new Point(5 * i+spacing, 110 + spacing + height);
		}



	}
	// Método para redefinir os sliders às suas posições iniciais
	   private void resetObjects() {


	        at.setToTranslation(position1[0].x - obj1.getBounds().getCenterX(),0);
	        obj1 = at.createTransformedShape(obj1);


	        percentual=0;

	    	
	        repaint();
	    }
	// Método chamado quando o mouse é arrastado
	@Override
	public void mouseDragged(MouseEvent e) {
		// Limits objs
		int minX = 70;// Limite mínimo de movimento
		int maxX = 285;// Limite máximo de movimento

		if (button == 0) {// Caso o slider 1 esteja sendo arrastado
			deltaX = e.getX() - firstX;// Calcula o deslocamento

			if (firstX + deltaX < minX) {// Impede que o slider passe do limite esquerdo
				deltaX = minX - firstX;
			} else if (firstX + deltaX > maxX) {// Impede que o slider passe do limite direito
				deltaX = maxX - firstX;
			}
			percentual = (float) (firstX + deltaX - minX) / (maxX - minX);// Calcula o percentual

			float degree = percentual * 180;

			at.setToTranslation(deltaX, deltaY);// Aplica a transformação de deslizamento
			obj = at.createTransformedShape(obj);
			firstX = firstX + deltaX;
			firstY = firstY + deltaY;
		}// Atualiza a posição inicial
		if (button == 1) {

			deltaX1 = e.getX() - firstX1;	

			if (firstX1 + deltaX1 < minX) {
				deltaX1 = minX - firstX1;
			} else if (firstX1 + deltaX1 > maxX) {
				deltaX1 = maxX - firstX1;
			}
			percentual2 = (float) (firstX1 + deltaX1 - minX) / (maxX - minX);
			at.setToTranslation(deltaX1, deltaY1);
			obj1 = at.createTransformedShape(obj1);
			firstX1 = firstX1 + deltaX1;
			firstY1 = firstY1 + deltaY1;
		}

		if (button == 2) {

			deltaX2 = e.getX() - firstX2;

			if (firstX2 + deltaX2 < minX) {
				deltaX2 = minX - firstX2;
			} else if (firstX2 + deltaX2 > maxX) {
				deltaX2 = maxX - firstX2;
			}
			percentual3 = (float) (firstX2 + deltaX2 - minX) / (maxX - minX);
			at.setToTranslation(deltaX2, deltaY2);
			obj2 = at.createTransformedShape(obj2);
			firstX2 = firstX2 + deltaX2;
			firstY2 = firstY2 + deltaY2;
		}

		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {// Verifica se o botão esquerdo do rato foi clicado
			if (obj.contains(getMousePosition())) { // Verifica se o clique foi no slider 1
				selected = true;
				firstX = e.getX();// Armazena a posição inicial do clique
				button = 0;// Define o slider 1 como ativo
			} else if (obj1.contains(getMousePosition())) {
				selected = true;
				firstX1 = e.getX();
				button = 1;
			} else if (obj2.contains(getMousePosition())) {
				selected = true;
				firstX2 = e.getX();
				
				button = 2;
			} else {
				selected = false;
				button = -1;
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		selected = false;
		/*if (button == 0) {
			int p = getNearestPosition();
			if (p != -1) {
			//	at.setToTranslation(position0[p].x - obj.getBounds().getCenterX(),0);
				obj = at.createTransformedShape(obj);
				repaint();
			}
		}*/

		if (button == 1) {
			int p = getNearestPosition1();
			if (p != -1) {
				at.setToTranslation(position1[p].x - obj1.getBounds().getCenterX(),0);
				obj1 = at.createTransformedShape(obj1);
				repaint();
			}
		}

		/*if (button == 2) {
			int p = getNearestPosition2();
			if (p != -1) {
			//	at.setToTranslation(position2[p].x - obj2.getBounds().getCenterX(),0);
				obj2 = at.createTransformedShape(obj2);
				repaint();
			}
		}*/

	}

	/*private int getNearestPosition() {
		int p = -1;
		float min = 9999999.9f;
		double dist = 0f;

		for (int i = 0; i < 47; i++) {
			dist = position0[i].distance(obj.getBounds().getCenterX(), obj.getBounds().getCenterY());
			if (dist <= maxDist) {
				if (dist < min) {
					min = (float) dist;
					p = i;
				}
			}
		}
		
		return p;
	}*/
	// Método para encontrar a posição mais próxima para o slider 1
	private int getNearestPosition1() {
		int p = -1;// Índice da posição mais próxima (inicialmente -1, ou seja, nenhuma encontrada)
		float min = 9999999.9f;// Valor inicial para a menor distância (valor muito alto)
		double dist1 = 0f;// Variável para armazenar a distância entre o slider e a posição
		// Itera por todas as posições predefinidas (47 no total)
		for (int i = 0; i < 47; i++) {
			// Calcula a distância entre o centro do slider 1 e a posição atual
			dist1 = position1[i].distance(obj1.getBounds().getCenterX(), obj1.getBounds().getCenterY());
	        // Verifica se a distância é menor que o limite máximo permitido (maxDist)
			if (dist1 <= maxDist) {
				// Se for menor que a menor distância já encontrada, atualiza o índice e a menor distância
				if (dist1 < min) {
					min = (float) dist1;// Atualiza a menor distância
					p = i; // Atualiza o índice da posição mais próxima
				}
			}
		}
		
		return p;// Retorna o índice da posição mais próxima (ou -1 se nenhuma estiver dentro do limite)
	}

	/*private int getNearestPosition2() {
		int p = -1;
		float min = 9999999.9f;
		double dist2 = 0f;
		for (int i = 0; i < 47; i++) {

			dist2 = position2[i].distance(obj2.getBounds().getCenterX(), obj.getBounds().getCenterY());
			if (dist2 <= maxDist) {
				if (dist2 < min) {
					min = (float) dist2;
					p = i;
				}
			}
		}
		
		return p;
	}*/

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	// Método responsável por desenhar o painel
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);// Limpa o painel antes de desenhá-lo novamente
	    Graphics2D g2 = (Graphics2D) g;// Converte para Graphics2D para maior controlo

	    // Desenhar a barra cinza
	    g2.setColor(Color.gray);
	    g2.translate(175, 40 + spacing);  // Posicionar a barra Xe Y
	    Shape drag1 = new DrawDrag(0, 0, width, height, thickness);
	    g2.fill(drag1);// Preenche a barra cinza com a forma definida

	    // Restaura a origem
	    g2.translate(-175, -(40 + spacing));

	    // Desenhar o controle deslizante azul na posição correta
	    g2.setColor(Color.blue);
	    g2.fill(obj);

	    // Título e texto
	    String title = "Taxa de satisfação";
	    Font font21 = new Font("roboto", Font.BOLD, 25);
	    FontRenderContext fcr2 = g2.getFontRenderContext();
	    g2.setFont(font21);
	    g2.setColor(Color.black);
	    ProjectFunction.drawCenteredText(g2, font21, fcr2, title, 175, 35);

	    Font font1 = new Font("Times new roman", Font.BOLD, 20);
	    g2.setFont(font1);
	    g2.setColor(Color.blue);
	    g2.drawString("Satisfaction", 60, spacing + height - 50);
	}


}

class ImagePanel extends JPanel {

    int panelWidth;
    int panelHeight;
    BufferedImage Image;
 // Construtor da classe
    public ImagePanel() {
        setPreferredSize(new Dimension(250, 250));
        setBackground(Color.white);
     // Obtém as dimensões preferidas do painel
        panelWidth = getPreferredSize().width;
        panelHeight = getPreferredSize().height;
        
     // Cria uma imagem inicial preenchida com padrão de losangos
        BufferedImage Image = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
        fillImageWithDiamonds(Image);
        setImage(Image);
    }
 // Método para configurar a imagem exibida no painel
    public void setImage(BufferedImage bi) {
        Image = bi;
        setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
        invalidate();
        repaint();
    }
 // Método para preencher uma imagem com um padrão de losangos
    private BufferedImage fillImageWithDiamonds(BufferedImage imgIn) {
        int[] rgba = new int[4];
        WritableRaster rasterImgIn = imgIn.getRaster();

        int diamondSize = 15; // Tamanho dos losangos

        for (int x = 0; x < imgIn.getWidth(); x++) {
            for (int y = 0; y < imgIn.getHeight(); y++) {

                int xInDiamond = x % diamondSize;
                int yInDiamond = y % diamondSize;
                boolean isDiamond = Math.abs(xInDiamond - diamondSize / 2) + Math.abs(yInDiamond - diamondSize / 2) < diamondSize / 2;

                if (isDiamond) {
                    rgba[0] = 0; 
                    rgba[1] = 0; 
                    rgba[2] = 0; 
                } else {
                    rgba[0] = 127; 
                    rgba[1] = 127;
                    rgba[2] = 127;
                }
                rgba[3] = 255;
                rasterImgIn.setPixel(x, y, rgba);
            }
        }
        return imgIn;
    }

    public BufferedImage getImage() {
        return Image;
    }
 // Método responsável por desenhar o painel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, panelWidth, panelHeight);
        
        // Se uma imagem estiver configurada, ela será redimensionada e desenhada no painel
        if (Image != null) {
            Image scaledImage = Image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
            g2.drawImage(scaledImage, 0, 0, this);
        }
    }
}
//Classe waitingTimePanel: Painel para exibição de um gráfico dinâmico representando tempos de espera.
//Implementa Runnable (para animações em thread) e Printable (para suporte à impressão).
class waitingTimePanel extends JPanel implements Runnable, Printable {
	// Variáveis para alturas e percentuais relacionados ao gráfico
	int height = 240;// Altura inicial do gráfico
	int height3;
	int height2;
	float percentual1, percentual2, percentual3;
	int[] ValuesY;
	int[] ValuesX;
	int thickness = 5;
	Shape grap;
	Shape lineColision = new Rectangle2D.Double(469, 55, 525, 1);
	 // Construtor da classe
	public waitingTimePanel() {
		setPreferredSize(new Dimension(250, 525));
		setBackground(Color.white);
		// Inicializa os arrays para as coordenadas do gráfico
		ValuesY = new int[13];
		ValuesX = new int[13];
		int x = 50;
		Random random = new Random();
		// Preenche as coordenadas com valores iniciais
		for (int i = 0; i < 13; i++) {
			if (i == 12) {
				ValuesY[i] = height;
			} else {
				ValuesY[i] = random.nextInt(90) + 60;
			}

			ValuesX[i] = x + i * 35;
		}
		// Inicia a thread para animações
		Thread thread = new Thread(this);
		thread.start();

	}
	// Método responsável por desenhar o painel
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
	    float thickness = 3.0f;

	 // letras do gráfico no eixo X (nomes das "paletes")
	    String[] pal = {"Pal 1", "Pal 2", "Pal 3", "Pal 4", "Pal 5", "Pal 6", "Pal 7"};
	    // letras do gráfico no eixo Y (tempos de espera)    
	    String[] wTime = {"0M", "10M", "20M", "30M", "40M"};


	    Font font = new Font("Sans-Serif", Font.BOLD, 14);
	    g2.setFont(font);
	    g2.setColor(Color.black);
	 // Configura a primativa grafica 
	    for (int i = 0; i < pal.length; i++) {
	        g2.drawString(pal[i], 75 + 60 * i, 240);
	    }
	 // Desenha os rótulos no eixo Y
	    for (int i = 0; i < wTime.length; i++) {
	        g2.drawString(wTime[i], 20, 220 - i * 40);
	    }
	    // Verifica se os arrays de coordenadas estão definidos
	    if (ValuesX != null && ValuesY != null) {
	        DrawGraphProfit grap = new DrawGraphProfit(60, 220, ValuesX, ValuesY);
	        g2.setColor(Color.blue);
	        g2.fill(grap);
//strokes
	        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	        g2.setColor(new Color(192, 217, 217, 188));
	        g2.draw(grap);
	        //intersao shapes
            // Verifica colisão com a linha e muda a cor se houver interseção
	        if (lineColision != null && lineColision.intersects(grap.getBounds2D())) {
	            g2.setColor(Color.green);
	            g2.fill(grap);
	            g2.setColor(new Color(144, 238, 144, 188));
	            g2.draw(grap);
	        }
	    }

	 // Desenha o título do gráfico
	    String title = "Tempo de espera na Web Summit";
	    Font titleFont = new Font("Roboto", Font.BOLD, 25);
	    g2.setFont(titleFont);
	    g2.setColor(Color.red);
	    ProjectFunction.drawCenteredText(g2, titleFont, g2.getFontRenderContext(), title, 275, 30);
	    // Desenha os eixos X e Y
	    g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	    g2.setColor(Color.black);
	    g2.drawLine(50, 220, 500, 220); // Eixo X
	    g2.drawLine(495, 215, 500, 220);
	    g2.drawLine(495, 225, 500, 220);
	    g2.drawLine(60, 230, 60, 35);   // Eixo Y
	    g2.drawLine(55, 40, 60, 35);
	    g2.drawLine(65, 40, 60, 35);
	}
	 // Implementação do método print para suporte à impressão
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0)

			return Printable.NO_SUCH_PAGE;

		Graphics2D g2 = (Graphics2D) graphics;
		double sclaceX = pageFormat.getImageableWidth() / getWidth();
		double sclaceY = pageFormat.getImageableHeight() / getHeight();
		if (sclaceX < sclaceY)
			g2.scale(sclaceX, sclaceY);
		else
			g2.scale(sclaceY, sclaceX);
		paintComponent(g2);
		return Printable.PAGE_EXISTS;
	}

    // Método run para animar as alterações no gráfico
	@Override
	public void run() {
		while (true) {


            // Calcula as novas alturas com base nos percentuais dos sliders

			height2 = (int) ((int) 170 * (-percentual2));
			height3 = (int) ((int) 170 * (percentual3));

			int newHeight = (int) (height2 + height3 + 220);
            // Limita a altura máxima para o topo do eixo Y

			if (newHeight > 220)
				newHeight = 220;

			if (height != newHeight) {
				if (height < newHeight) {

					height = (height + 1);
					ValuesY[12] = height;

				} else {
					height = (height - 1);
					ValuesY[12] = height;
				}

			}
			repaint();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
//Classe HapLevPanel: Representa um painel que exibe um gráfico semicircular interativo
//indicando o nível de felicidade. Implementa Runnable (para animações em thread) e Printable (para suporte à impressão).
class HapLevPanel extends JPanel implements Runnable, Printable {
	String formattedStockPercentual;// String formatada para exibir o percentual (ex.: "75.0%")
	float degreeActual;// Ângulo atual do ponteiro no gráfico semicircular

	AffineTransform at = new AffineTransform();// Objeto para realizar transformações geométricas

	public HapLevPanel() {
		setPreferredSize(new Dimension(250, 250));
		setBackground(Color.white);
		 // Inicia a thread para atualizações e animações
		Thread thread = new Thread(this);
		thread.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		// Cria dois semicírculos concêntricos (externo e interno)
		Shape SemiCirculeExt = new DrawSemiCircule(0, 0, 100);// Semicírculo externo
		Shape SemiCirculeInt = new DrawSemiCircule(0, 0, 75);// Semicírculo interno
		// Cria áreas de semicírculos para recortar o interior
		Area Areagrap4Ext = new Area(SemiCirculeExt);// Área do semicírculo externo
		Area Areagrap4Int = new Area(SemiCirculeInt);// Área do semicírculo interno

		Areagrap4Ext.subtract(Areagrap4Int);// Remove a parte interna do semicírculo externo
		// Move o sistema de coordenadas para centralizar o gráfico
		g2.translate(125, 175);
		// Preenche o semicírculo externo (anel) com cor cinza
		g2.setColor(Color.GRAY);
		g2.fill(Areagrap4Ext);
		 // Restaura o sistema de coordenadas
		g2.translate(-100, -10);
		
		// Cria o ponteiro rotacional (elipse)
		Shape ellipse = new Ellipse2D.Double(0, 0, 100, 20);
		// Aplica uma rotação ao ponteiro com base no ângulo atual
		at.setToRotation(Math.toRadians(degreeActual), 100, 10);
		ellipse = at.createTransformedShape(ellipse);
		// Preenche o ponteiro com cor laranja
		g2.setColor(Color.orange);
		g2.fill(ellipse);
		// Adiciona um semicírculo menor ao centro do gráfico
		g2.translate(100, 20);
		Shape SemiCircule = new DrawSemiCircule(0, 0, 45);
		g2.setColor(Color.gray);
		g2.fill(SemiCircule);
		// Restaura o sistema de coordenadas novamente
		g2.translate(-100, 5);
		
		// Adiciona uma barra preta para exibição de texto (percentual)
		Shape rect = new Rectangle2D.Double(0, 0, 200, 30);
		g2.setPaint(Color.black);
		g2.fill(rect);
		// Move o sistema de coordenadas para desenhar o título
		g2.translate(-25, -190);
		// Desenha o título "Happiness level" no topo
		Font font = new Font("Sans-Serif", Font.BOLD, 30);
		FontRenderContext fcr = g2.getFontRenderContext();
		g2.setFont(font);
		g2.setColor(Color.black);
		ProjectFunction.drawCenteredText(g2, font, fcr, "Hapiness level", 125, 50);
		// precentaguem do gráfico
		Font font2 = new Font("Roboto", Font.BOLD, 30);
		FontRenderContext fcr2 = g2.getFontRenderContext();
		g2.setFont(font);
		g2.setColor(Color.white);

		ProjectFunction.drawCenteredText(g2, font2, fcr2, formattedStockPercentual + "%", 125, 215);
	}
	// Método run para animações e atualizações
	@Override
	public void run() {

		while (true) {

            // Obtém o percentual ajustado pelo slider no painel SetValuesPanel
			float percentual1 = aplication.SetValuesPanel.percentual;
			float StockPercentual = percentual1 * 100;
			formattedStockPercentual = String.format("%.1f", StockPercentual);
			// Atualiza o ângulo do ponteiro para refletir o percentual
			if (degreeActual != Math.abs(percentual1 * 180)) {
				if (degreeActual < Math.abs(percentual1 * 180)) {
					degreeActual = (degreeActual + 2f);

				}
				if (degreeActual > Math.abs(percentual1 * 180)) {
					degreeActual = (degreeActual - 2f);

				}

			}
			repaint();

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	// Implementação do método print para suporte à impressão
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0)
			return Printable.NO_SUCH_PAGE;

		Graphics2D g2 = (Graphics2D) graphics;
		// Ajusta a escala para caber no tamanho da página
		double sclaceX = pageFormat.getImageableWidth() / getWidth();
		double sclaceY = pageFormat.getImageableHeight() / getHeight();
		if (sclaceX < sclaceY)
			g2.scale(sclaceX, sclaceY);
		else
			g2.scale(sclaceY, sclaceX);
		paintComponent(g2);
		return Printable.PAGE_EXISTS;
	}

}
