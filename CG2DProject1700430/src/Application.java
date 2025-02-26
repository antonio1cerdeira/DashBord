
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application extends JFrame implements ActionListener {
    CheckersGameUI checkersGameUI; // Declaration of the Checkers board panel variable

    JFileChooser fc = new JFileChooser(); // File chooser to open/save images

    public static void main(String[] args) {
        // Main method creates the main application frame and sets its properties
        JFrame frame = new Application();
        frame.setTitle("MegaGameXPTO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800); // Window size is fixed to 1200x800
        frame.setVisible(true); // Makes the frame visible
    }

    public Application() {
        // Constructor sets up the main window frame and its components

        setTitle("DashBoard");  // Sets the window's title
        setSize(1200, 800);  // Sets the size of the window (1200x800)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ensures the app closes when the window is closed

        // Create and configure the menu bar
        JMenuBar mb = new JMenuBar();  // Main menu bar
        setJMenuBar(mb);  // Attaches the menu bar to the frame

        // Create the "File" menu
        JMenu menu = new JMenu("File");  // "File" menu
        JMenuItem mi = new JMenuItem("Insert Product Image");  // Menu item for inserting an image
        mi.addActionListener(this);  // Adds an action listener for the menu item
        menu.add(mi);  // Adds the item to the "File" menu

        mi = new JMenuItem("Save Image");  // Menu item for saving an image
        mi.addActionListener(this);
        menu.add(mi);  // Adds the item to the "File" menu

        menu.addSeparator();  // Adds a separator line to divide menu items

        mi = new JMenuItem("Exit");  // Menu item to exit the application
        mi.addActionListener(this);
        menu.add(mi);  // Adds the "Exit" option to the "File" menu

        mb.add(menu);  // Adds the "File" menu to the menu bar

        // Create the "Edit product image" menu
        menu = new JMenu("Edit product image");  // "Edit product image" menu
        mi = new JMenuItem("Contrast");  // Option to adjust contrast
        mi.addActionListener(this);
        menu.add(mi);  // Adds the "Contrast" option to the menu

        mi = new JMenuItem("Brighten");  // Option to brighten the image
        mi.addActionListener(this);
        menu.add(mi);  // Adds the "Brighten" option

        mi = new JMenuItem("Darken");  // Option to darken the image
        mi.addActionListener(this);
        menu.add(mi);  // Adds the "Darken" option

        mb.add(menu);  // Adds the "Edit product image" menu to the menu bar

        // Create the "Print" menu
        menu = new JMenu("Print");  // "Print" menu
        mi = new JMenuItem("Painel 1");  // Option to print panel 1
        mi.addActionListener(this);
        menu.add(mi);

        mi = new JMenuItem("Painel 2");  // Option to print panel 2
        mi.addActionListener(this);
        menu.add(mi);

        mi = new JMenuItem("Painel 3");  // Option to print panel 3
        mi.addActionListener(this);
        menu.add(mi);

        mb.add(menu);  // Adds the "Print" menu to the menu bar

        // Main panel configuration (left panel with game board, right panel with animations)
        JPanel backgroundPanel = new JPanel(new BorderLayout());  // Background panel with BorderLayout
        backgroundPanel.setBackground(Color.BLUE);  // Sets the background color to blue
        backgroundPanel.add(new MainPanel(), BorderLayout.CENTER);  // Adds the main panel in the center

        add(backgroundPanel);  // Adds the background panel to the main frame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handles menu actions triggered by user interactions
        String cmd = e.getActionCommand();

        if (cmd.equals("Insert Product Image")) {
            // Opens a dialog to insert an image when "Insert Product Image" is selected
            int returnVale = fc.showOpenDialog(this);
            if (returnVale == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "Product Image Inserted.");
            }
        } else if (cmd.equals("Save Image")) {
            // Opens a dialog to save an image when "Save Image" is selected
            int returnVale = fc.showSaveDialog(this);
            if (returnVale == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "Image Saved.");
            }
        } else if (cmd.equals("Exit")) {
            // Closes the application when "Exit" is selected
            System.exit(0);
        } else if (cmd.equals("Contrast")) {
            // Displays a message for the contrast option
            JOptionPane.showMessageDialog(this, "Contrast selected.");
        } else if (cmd.equals("Brighten")) {
            // Displays a message for the brighten option
            JOptionPane.showMessageDialog(this, "Brighten selected.");
        } else if (cmd.equals("Darken")) {
            // Displays a message for the darken option
            JOptionPane.showMessageDialog(this, "Darken selected.");
        } else if (cmd.equals("Graph 1")) {
            // Displays a message when "Painel 1" is selected
            JOptionPane.showMessageDialog(this, "Printing Painel 1.");
        } else if (cmd.equals("Graph 2")) {
            // Displays a message when "Painel 2" is selected
            JOptionPane.showMessageDialog(this, "Printing Painel 2.");
        } else if (cmd.equals("Graph 3")) {
            // Displays a message when "Painel 3" is selected
            JOptionPane.showMessageDialog(this, "Printing Painel 3.");
        }
    }
}

// MainPanel class with a checkers game board on the left and 3 animated panels on the right
class MainPanel extends JPanel {

    public MainPanel() {
        // Sets the layout of the main panel to BorderLayout
        setLayout(new BorderLayout());

        // Left panel for the checkers game board
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(780, 780));  // Sets fixed dimensions for the left panel
        leftPanel.setBackground(Color.RED);  // Sets the background color to red for visibility
        leftPanel.setBorder(BorderFactory.createTitledBorder("DAMAS"));  // Adds a titled border to the left panel

        // Instantiate and add the checkers game board (CheckersGameUI)
        CheckersGameUI checkersGame = new CheckersGameUI();  // Creates the checkers board panel
        checkersGame.setPreferredSize(new Dimension(780, 780));  // Sets the size of the checkers board panel

        leftPanel.setLayout(new GridBagLayout());  // Uses GridBagLayout to center the board within the left panel
        leftPanel.add(checkersGame);  // Adds the checkers board to the left panel

        // Adds the left panel to the main panel on the left side
        add(leftPanel, BorderLayout.WEST);  // Adds the panel to the west (left side)

        // Right panel with animated rhombuses (three vertically aligned panels)
        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // 3 vertical panels with 10px spacing
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));  // Adds padding around the right panel

        // Create 3 sub-panels, each with an animation of rhombuses
     // Create 3 sub-panels, each with different content
        for (int i = 1; i <= 3; i++) {
            AnimatedPanel subPanel = new AnimatedPanel();  // Creates a new animated panel
            subPanel.setBorder(BorderFactory.createTitledBorder("Panel " + i));  // Adds a titled border with the panel number
            
            // Add different content to each panel based on the loop iteration
            switch (i) {
                case 1:
                    JLabel label = new JLabel("This is Panel 1 with a text label."); // Add a text label to Panel 1
                    subPanel.add(label);  // Add the label to the panel
                    break;
                case 2:

                case 3:
                    JTextField textField = new JTextField("Type here...", 15);  // Add a text field to Panel 3
                    subPanel.add(textField);  // Add the text field to the panel
                    break;
            }
            
            rightPanel.add(subPanel);  // Adds the sub-panel to the right panel
        }

        // Adds the right panel to the main panel in the center
        add(rightPanel, BorderLayout.CENTER);  // Adds the right panel to the center (next to the left panel)
    }
}



