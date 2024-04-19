import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HospitalManagementSystem extends JFrame implements ActionListener {
    // JDBC URL, username, and password of PostgreSQL server
    private static final String URL = "jdbc:postgresql://localhost:5432/hospital";
    private static final String USER = "postgres";
    private static final String PASSWORD = "tehran96";

    // Components
    private JTextField patientIdField, patientNameField, patientAgeField, searchField;
    private JButton addButton, updateButton, deleteButton, viewButton, searchButton, viewDoctorsButton, makeAppointmentButton;
    private JTextArea displayArea;
    private Connection connection;
    private Statement statement;

    public HospitalManagementSystem() {
        super("PUDUGAI HOSPITAL");

        // Initialize components
        patientIdField = new JTextField(10);
        patientNameField = new JTextField(20);
        patientAgeField = new JTextField(10);
        searchField = new JTextField(10);
        addButton = new JButton("Add Patient");
        updateButton = new JButton("Update Patient");
        deleteButton = new JButton("Delete Patient");
        viewButton = new JButton("View Patients");
        searchButton = new JButton("Search Patient");
        viewDoctorsButton = new JButton("View Doctors");
        makeAppointmentButton = new JButton("Make Appointment");
        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);

        // Add action listeners
        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        viewButton.addActionListener(this);
        searchButton.addActionListener(this);
        viewDoctorsButton.addActionListener(this);
        makeAppointmentButton.addActionListener(this);
        

        // Layout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputPanel.add(new JLabel("ID: "), gbc);
        gbc.gridy++;
        inputPanel.add(new JLabel("Name: "), gbc);
        gbc.gridy++;
        inputPanel.add(new JLabel("Age: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        inputPanel.add(patientIdField, gbc);
        gbc.gridy++;
        inputPanel.add(patientNameField, gbc);
        gbc.gridy++;
        inputPanel.add(patientAgeField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewDoctorsButton);
        buttonPanel.add(makeAppointmentButton);

        inputPanel.add(buttonPanel, gbc);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JScrollPane scrollPane = new JScrollPane(displayArea);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputPanel, BorderLayout.NORTH);
        contentPane.add(searchPanel, BorderLayout.CENTER);
        contentPane.add(scrollPane, BorderLayout.SOUTH);

        // Styling
        Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        patientIdField.setFont(boldFont);
        patientNameField.setFont(boldFont);
        patientAgeField.setFont(boldFont);
        addButton.setFont(boldFont);
        updateButton.setFont(boldFont);
        deleteButton.setFont(boldFont);
        viewButton.setFont(boldFont);
        searchButton.setFont(boldFont);
        viewDoctorsButton.setFont(boldFont);
        makeAppointmentButton.setFont(boldFont);
        displayArea.setFont(boldFont);

        displayArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchField.setPreferredSize(new Dimension(150, 30));
        patientIdField.setPreferredSize(new Dimension(150, 30));

 

        // Establish database connection
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS patients (id SERIAL PRIMARY KEY, name VARCHAR(100), age INTEGER)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addPatient();
        } else if (e.getSource() == updateButton) {
            updatePatient();
        } else if (e.getSource() == deleteButton) {
            deletePatient();
        } else if (e.getSource() == viewButton) {
            viewPatients();
        } else if (e.getSource() == searchButton) {
            searchPatient();
        } else if (e.getSource() == viewDoctorsButton) {
            viewDoctors();
        } else if (e.getSource() == makeAppointmentButton) {
            makeAppointment();
        }
    }

    private void addPatient() {
        String name = patientNameField.getText();
        int age = Integer.parseInt(patientAgeField.getText());
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO patients (name, age) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Patient added successfully.");
            patientNameField.setText("");
            patientAgeField.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updatePatient() {
        int id = Integer.parseInt(patientIdField.getText());
        String name = patientNameField.getText();
        int age = Integer.parseInt(patientAgeField.getText());
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE patients SET name=?, age=? WHERE id=?");
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setInt(3, id);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found with ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deletePatient() {
        int id = Integer.parseInt(patientIdField.getText());
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM patients WHERE id=?");
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Patient deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found with ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewPatients() {
        displayArea.setText(""); // Clear display area
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM patients");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                displayArea.append("ID: " + id + ", Name: " + name + ", Age: " + age + "\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchPatient() {
        int id = Integer.parseInt(searchField.getText());
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM patients WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                JOptionPane.showMessageDialog(this, "Patient found:\nID: " + id + "\nName: " + name + "\nAge: " + age);
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found with ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewDoctors() {
        // Dummy implementation for viewing doctors
        String[] doctors = {"Dr. John Doe", "Dr. Jane Smith", "Dr. Michael Johnson"};
        StringBuilder doctorsList = new StringBuilder("Available Doctors:\n");
        for (String doctor : doctors) {
            doctorsList.append(doctor).append("\n");
        }
        JOptionPane.showMessageDialog(this, doctorsList.toString());
    }

    private void makeAppointment() {
        // Dummy implementation for making appointment
        JOptionPane.showMessageDialog(this, "Appointment made successfully.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalManagementSystem::new);
    }
}
