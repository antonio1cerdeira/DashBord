

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;

public class AnimatedPanel extends JPanel implements Runnable  {

	 @Override
	 protected void paintComponent(Graphics g) {
	     super.paintComponent(g); // Chama o método da classe pai para limpar o painel
	     Graphics2D g2 = (Graphics2D) g;

	     // Cores para o gradiente
	     Color startColor = new Color(0, 159, 224);
	     Color endColor = new Color(2, 9, 111);

	     // Gradiente de fundo
	     GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
	     g2.setPaint(gradient); 
	     g2.fillRect(0, 0, getWidth(), getHeight());

	     // Desenho de losangos
	     g2.setColor(Color.BLACK);

	     // Desenha diamantes em posições aleatórias
	     for (int i = 0; i < 5; i++) { // Desenha 5 diamantes por exemplo
	         int x = (int) (getWidth() * Math.random());
	         int y = (int) (getHeight() * Math.random());
	         int size = (int) (50 * Math.random());

	         GeneralPath diamond = new GeneralPath();
	         diamond.moveTo(x, y + size);
	         diamond.lineTo(x - size / 2, y);
	         diamond.lineTo(x, y - size);
	         diamond.lineTo(x + size / 2, y);
	         diamond.closePath();

	         g2.draw(diamond);
	     }
	 }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
