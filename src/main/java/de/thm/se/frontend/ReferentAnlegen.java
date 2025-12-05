package de.thm.se.frontend;

import javax.swing.*;
import java.awt.*;

public class ReferentAnlegen extends JFrame {
    public ReferentAnlegen() {
        setTitle("Referent/in Formular");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Farben
        Color border = new Color(0, 93, 120);     
        Color background = new Color(252, 251, 251); //252, 251, 251)

        //getContentPane().setBackground(Color.WHITE);

        // Hauptpanel mit Formular und Hintergrundfarbe + Rahmen
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(background); // z.B. new Color(250,243,224)
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 3, true),
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));

        
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        int fieldWidth = 300, fieldHeight = 28;

        // 0: Überschrift (zentriert)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; // falls du irgendwann was rechts ergänzen willst
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Referent/in");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22));
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // ab jetzt Einzelspalten

        // 1: Bild (eigene Zeile, linksbündig)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setBorder(BorderFactory.createLineBorder(border, 1, true));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setText("<Bild>");
        mainPanel.add(imageLabel, gbc);

        gbc.gridwidth = 1;

        // Leere Zelle rechts vom Bild, damit Felder richtig unter das Bild rutschen (optional)
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(Box.createHorizontalStrut(1), gbc); // Unsichtbarer Platzhalter

        // 2: Titel (unter dem Bild & linksbündig)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblTitel = new JLabel("Titel:");
        mainPanel.add(lblTitel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> cbTitel = new JComboBox<>(new String[]{"", "Prof.", "Prof. Dr.", "Dr."});
        cbTitel.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        mainPanel.add(cbTitel, gbc);

        // 3: Name
        gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblName = new JLabel("Name:");
        mainPanel.add(lblName, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField tfName = new JTextField();
        tfName.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        mainPanel.add(tfName, gbc);

        // 4: Vorname
        gbc.gridy = 4; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblVorname = new JLabel("Vorname:");
        mainPanel.add(lblVorname, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField tfVorname = new JTextField();
        tfVorname.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        mainPanel.add(tfVorname, gbc);

        // 5: Adresse
        gbc.gridy = 5; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblAdresse = new JLabel("Adresse:");
        mainPanel.add(lblAdresse, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextArea adresseArea = new JTextArea(3, 22);
        adresseArea.setLineWrap(true);
        adresseArea.setWrapStyleWord(true);
        JScrollPane adresseScroll = new JScrollPane(adresseArea);
        adresseScroll.setPreferredSize(new Dimension(fieldWidth, 65));
        mainPanel.add(adresseScroll, gbc);

        // 6: Telefon
        gbc.gridy = 6; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblTelefon = new JLabel("Telefon:");
        mainPanel.add(lblTelefon, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField tfTelefon = new JTextField();
        tfTelefon.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        mainPanel.add(tfTelefon, gbc);

        // 7: Email
        gbc.gridy = 7; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblMail = new JLabel("E-Mail:");
        mainPanel.add(lblMail, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField tfMail = new JTextField();
        tfMail.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        mainPanel.add(tfMail, gbc);

        // 8: Buttons horizontal, unter den Feldern
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        JButton cancelBtn = new JButton("Cancel");
        JButton submitBtn = new JButton("Submit");
        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        buttonPanel.setBackground(background);
        buttonPanel.setOpaque(true);
        mainPanel.add(buttonPanel, gbc);

        // Hauptpanel ins Fenster (mit Abstand nach oben, mittig)
        setLayout(new GridBagLayout());
        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0; frameGbc.gridy = 0;
        frameGbc.weightx = 1.0; frameGbc.weighty = 1.0;
        frameGbc.anchor = GridBagConstraints.NORTH;
        frameGbc.insets = new Insets(40, 0, 0, 0);
        add(mainPanel, frameGbc);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReferentAnlegen::new);
    }
}