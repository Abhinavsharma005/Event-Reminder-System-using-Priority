import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class EventReminder extends Frame implements ActionListener {
    // GUI Components
    private TextField txtName;
    private Choice chPriority;
    private TextField txtTime;
    private TextArea txtDesc;
    private Button btnAdd;
    private Button btnClear;
    private Button btnView;
    private Label lblStatus;
    
    // Right panel scroll container elements
    private Panel listItemsPanel;
    private Panel listContainer;
    private ScrollPane scrollPane;
    private Panel rightCard;
    
    // DB Connection URL
    private static final String DB_URL = "jdbc:sqlite:events.db";
    
    // Set to track already alerted event IDs in the current session
    private final Set<Integer> alertedEvents = new HashSet<>();

    public EventReminder() {
        // Set frame title
        super("Event Reminder System");
        
        // Initialize database and migration
        initDatabase();
        
        // Custom Theme Colors (Replicating the provided UI screenshot design)
        Color frameBg = new Color(230, 235, 245); // Soft light blue-grey
        Color navyCol = new Color(12, 35, 90);    // Bold Navy Blue
        Color textCol = Color.BLACK;
        Color inputBgCol = Color.WHITE;
        
        Color addBtnBg = new Color(46, 155, 68);    // Rich Green
        Color clearBtnBg = new Color(241, 196, 15);  // Rich Yellow
        Color viewBtnBg = new Color(34, 112, 224);   // Rich Blue
        
        Font titleFont = new Font("Segoe UI", Font.BOLD, 26);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        
        // Configure Frame
        setBackground(frameBg);
        setLayout(new BorderLayout(15, 15));
        
        // 1. NORTH: Title Panel (🔔 Event Reminder System 🔔)
        Panel titlePanel = new Panel();
        titlePanel.setBackground(frameBg);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        Label titleLabel = new Label("\uD83D\uDD14 Event Reminder System \uD83D\uDD14");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(navyCol);
        titleLabel.setBackground(frameBg);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // 2. CENTER: Main Content Container (Holds Left and Right Cards side-by-side)
        Panel mainContentPanel = new Panel(new GridLayout(1, 2, 15, 15));
        mainContentPanel.setBackground(frameBg);
        
        // ================= LEFT COLUMN: INPUT CARD =================
        CardPanel leftCard = new CardPanel(new BorderLayout(10, 15), 20, 20, 20, 20);
        
        // Inner content of Left Card
        Panel leftContent = new Panel(new BorderLayout(10, 12));
        leftContent.setBackground(Color.WHITE);
        
        // Inputs Grid (Name, Priority, Time)
        Panel inputsGrid = new Panel(new GridLayout(3, 2, 10, 12));
        inputsGrid.setBackground(Color.WHITE);
        
        Label lblName = new Label("Event Name:");
        lblName.setFont(labelFont);
        lblName.setForeground(textCol);
        lblName.setBackground(Color.WHITE);
        txtName = new TextField();
        txtName.setFont(inputFont);
        txtName.setBackground(inputBgCol);
        txtName.setForeground(textCol);
        
        Label lblPriority = new Label("Priority:");
        lblPriority.setFont(labelFont);
        lblPriority.setForeground(textCol);
        lblPriority.setBackground(Color.WHITE);
        chPriority = new Choice();
        chPriority.add("High");
        chPriority.add("Medium");
        chPriority.add("Low");
        chPriority.setFont(inputFont);
        chPriority.setBackground(inputBgCol);
        chPriority.setForeground(textCol);
        
        Label lblTime = new Label("Time (HH:mm):");
        lblTime.setFont(labelFont);
        lblTime.setForeground(textCol);
        lblTime.setBackground(Color.WHITE);
        txtTime = new TextField();
        txtTime.setFont(inputFont);
        txtTime.setBackground(inputBgCol);
        txtTime.setForeground(textCol);
        // Default text: current time
        LocalTime timeNow = LocalTime.now();
        txtTime.setText(String.format("%02d:%02d", timeNow.getHour(), timeNow.getMinute()));
        
        inputsGrid.add(lblName);
        inputsGrid.add(txtName);
        inputsGrid.add(lblPriority);
        inputsGrid.add(chPriority);
        inputsGrid.add(lblTime);
        inputsGrid.add(txtTime);
        
        // Description Area Panel
        Panel descPanel = new Panel(new BorderLayout(5, 5));
        descPanel.setBackground(Color.WHITE);
        
        Label lblDesc = new Label("Description:");
        lblDesc.setFont(labelFont);
        lblDesc.setForeground(textCol);
        lblDesc.setBackground(Color.WHITE);
        
        txtDesc = new TextArea("", 6, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);
        txtDesc.setFont(inputFont);
        txtDesc.setBackground(inputBgCol);
        txtDesc.setForeground(textCol);
        
        descPanel.add(lblDesc, BorderLayout.NORTH);
        descPanel.add(txtDesc, BorderLayout.CENTER);
        
        // Assemble Top of Left Content (Inputs + Description)
        Panel leftFieldsPanel = new Panel(new BorderLayout(10, 10));
        leftFieldsPanel.setBackground(Color.WHITE);
        leftFieldsPanel.add(inputsGrid, BorderLayout.NORTH);
        leftFieldsPanel.add(descPanel, BorderLayout.CENTER);
        
        leftContent.add(leftFieldsPanel, BorderLayout.CENTER);
        
        // Buttons Panel
        Panel buttonPanel = new Panel(new BorderLayout(0, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        Panel topBtnGrid = new Panel(new GridLayout(1, 2, 12, 10));
        topBtnGrid.setBackground(Color.WHITE);
        
        btnAdd = new Button("\u2795 Add Event"); // Plus Emoji
        btnAdd.setFont(buttonFont);
        btnAdd.setBackground(addBtnBg);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(this);
        
        btnClear = new Button("\uD83E\uDDF9 Clear"); // Broom Emoji
        btnClear.setFont(buttonFont);
        btnClear.setBackground(clearBtnBg);
        btnClear.setForeground(Color.BLACK);
        btnClear.addActionListener(this);
        
        topBtnGrid.add(btnAdd);
        topBtnGrid.add(btnClear);
        
        btnView = new Button("\u2630 View Events"); // List icon/menu Emoji
        btnView.setFont(buttonFont);
        btnView.setBackground(viewBtnBg);
        btnView.setForeground(Color.WHITE);
        btnView.addActionListener(this);
        
        buttonPanel.add(topBtnGrid, BorderLayout.NORTH);
        buttonPanel.add(btnView, BorderLayout.SOUTH);
        
        leftContent.add(buttonPanel, BorderLayout.SOUTH);
        leftCard.add(leftContent, BorderLayout.CENTER);
        mainContentPanel.add(leftCard);
        
        // ================= RIGHT COLUMN: EVENT LIST CARD =================
        rightCard = new CardPanel(new BorderLayout(10, 10), 20, 20, 20, 20);
        
        // Header of Right Card
        Panel rightHeaderPanel = new Panel(new BorderLayout(5, 8));
        rightHeaderPanel.setBackground(Color.WHITE);
        
        Label lblListTitle = new Label("Event List", Label.CENTER);
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblListTitle.setForeground(navyCol);
        lblListTitle.setBackground(Color.WHITE);
        
        Panel separator = new Panel();
        separator.setBackground(new Color(200, 205, 215));
        separator.setPreferredSize(new Dimension(100, 1));
        
        rightHeaderPanel.add(lblListTitle, BorderLayout.CENTER);
        rightHeaderPanel.add(separator, BorderLayout.SOUTH);
        
        rightCard.add(rightHeaderPanel, BorderLayout.NORTH);
        
        // ScrollPane & Dynamic list containers
        scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.setBackground(Color.WHITE);
        
        listContainer = new Panel(new BorderLayout());
        listContainer.setBackground(Color.WHITE);
        
        listItemsPanel = new Panel(new GridLayout(0, 1, 0, 15));
        listItemsPanel.setBackground(Color.WHITE);
        
        listContainer.add(listItemsPanel, BorderLayout.NORTH);
        scrollPane.add(listContainer);
        
        rightCard.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom Status Label
        lblStatus = new Label("Ready", Label.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(navyCol);
        lblStatus.setBackground(Color.WHITE);
        rightCard.add(lblStatus, BorderLayout.SOUTH);
        
        mainContentPanel.add(rightCard);
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Handle window closing event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        // Frame properties
        setSize(950, 600);
        setLocationRelativeTo(null); // Center window on screen
        setVisible(true);
        
        // Start Background Reminder Thread
        startReminderDaemon();
        
        // Automatically load existing events on startup
        viewEvents();
    }
    
    // Add custom padding around the main Frame container
    @Override
    public Insets getInsets() {
        // top, left, bottom, right
        // 45px top padding is required so content is not drawn under standard OS title bar.
        return new Insets(45, 15, 15, 15);
    }
    
    private void initDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                
                // Create table
                String sql = "CREATE TABLE IF NOT EXISTS events (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "name TEXT NOT NULL," +
                             "priority TEXT NOT NULL," +
                             "description TEXT" +
                             ");";
                stmt.execute(sql);
                
                // Migration: try to add reminder_time column if it does not exist
                try {
                    stmt.execute("ALTER TABLE events ADD COLUMN reminder_time TEXT;");
                } catch (SQLException e) {
                    // Column already exists, safe to ignore
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found in Classpath!");
        } catch (SQLException e) {
            System.err.println("Database error during initialization: " + e.getMessage());
        }
    }
    
    // Background reminder daemon
    private void startReminderDaemon() {
        Thread daemon = new Thread(() -> {
            while (true) {
                try {
                    checkReminders();
                    Thread.sleep(15000); // Check every 15 seconds
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();
    }
    
    // Query database and trigger popups for matching times
    private void checkReminders() {
        LocalTime now = LocalTime.now();
        String currentTime = String.format("%02d:%02d", now.getHour(), now.getMinute());
        
        String sql = "SELECT id, name, priority, description FROM events WHERE reminder_time = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, currentTime);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String priority = rs.getString("priority");
                    String desc = rs.getString("description");
                    
                    // Trigger popup if this event has not been alerted in the current session
                    if (!alertedEvents.contains(id)) {
                        alertedEvents.add(id);
                        
                        final String finalName = name;
                        final String finalPriority = priority;
                        final String finalDesc = desc != null ? desc : "";
                        
                        // Execute Dialog on Event Dispatch Thread (EDT)
                        EventQueue.invokeLater(() -> {
                            ReminderDialog dialog = new ReminderDialog(EventReminder.this, finalName, finalPriority, finalDesc);
                            dialog.setVisible(true);
                        });
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking reminders: " + e.getMessage());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            addEvent();
        } else if (e.getSource() == btnClear) {
            clearForm();
        } else if (e.getSource() == btnView) {
            viewEvents();
        }
    }
    
    private void addEvent() {
        String name = txtName.getText().trim();
        String priority = chPriority.getSelectedItem();
        String time = txtTime.getText().trim();
        String desc = txtDesc.getText().trim();
        
        if (name.isEmpty()) {
            lblStatus.setText("\u274C Validation Error: Event Name is required!");
            lblStatus.setForeground(new Color(220, 53, 69));
            return;
        }
        
        if (!time.isEmpty() && !time.matches("\\d{2}:\\d{2}")) {
            lblStatus.setText("\u274C Validation Error: Time must be in HH:mm format (e.g. 17:30)!");
            lblStatus.setForeground(new Color(220, 53, 69));
            return;
        }
        
        String sql = "INSERT INTO events (name, priority, description, reminder_time) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, priority);
            pstmt.setString(3, desc);
            pstmt.setString(4, time);
            pstmt.executeUpdate();
            
            lblStatus.setText("\u2705 Success: Event '" + name + "' added!");
            lblStatus.setForeground(new Color(46, 155, 68));
            
            // Clear inputs
            txtName.setText("");
            txtDesc.setText("");
            // Reset time to current time
            LocalTime timeNow = LocalTime.now();
            txtTime.setText(String.format("%02d:%02d", timeNow.getHour(), timeNow.getMinute()));
            
            // Automatically refresh display list
            viewEvents();
            
        } catch (SQLException e) {
            lblStatus.setText("\u274C DB Error: " + e.getMessage());
            lblStatus.setForeground(new Color(220, 53, 69));
        }
    }
    
    private void clearForm() {
        txtName.setText("");
        chPriority.select(0);
        LocalTime timeNow = LocalTime.now();
        txtTime.setText(String.format("%02d:%02d", timeNow.getHour(), timeNow.getMinute()));
        txtDesc.setText("");
        lblStatus.setText("Form cleared.");
        lblStatus.setForeground(new Color(12, 35, 90));
    }
    
    private void viewEvents() {
        String sql = "SELECT id, name, priority, description, reminder_time FROM events ORDER BY id DESC";
        
        // Clear existing cards
        listItemsPanel.removeAll();
        
        int count = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                String priority = rs.getString("priority");
                String desc = rs.getString("description");
                String time = rs.getString("reminder_time");
                
                // Construct a beautifully formatted Event Card using nested AWT FlowLayout panels
                Panel card = new Panel(new GridLayout(0, 1, 2, 2));
                card.setBackground(Color.WHITE);
                
                // Color configuration for priority
                Color priorityColor;
                if ("High".equalsIgnoreCase(priority)) {
                    priorityColor = new Color(220, 53, 69); // Red
                } else if ("Medium".equalsIgnoreCase(priority)) {
                    priorityColor = new Color(243, 156, 18); // Orange/Yellow
                } else {
                    priorityColor = new Color(46, 155, 68); // Green
                }
                
                Color labelNavy = new Color(12, 35, 90);
                
                Font cardBoldFont = new Font("Segoe UI", Font.BOLD, 13);
                Font cardRegularFont = new Font("Segoe UI", Font.PLAIN, 13);
                
                // 1. Event Name Row
                Panel nameRow = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                nameRow.setBackground(Color.WHITE);
                Label lblNameLabel = new Label("Event: ");
                lblNameLabel.setFont(cardBoldFont);
                lblNameLabel.setForeground(labelNavy);
                lblNameLabel.setBackground(Color.WHITE);
                Label lblNameVal = new Label(name);
                lblNameVal.setFont(cardBoldFont);
                lblNameVal.setForeground(Color.BLACK);
                lblNameVal.setBackground(Color.WHITE);
                nameRow.add(lblNameLabel);
                nameRow.add(lblNameVal);
                card.add(nameRow);
                
                // 2. Priority Row
                Panel priorityRow = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                priorityRow.setBackground(Color.WHITE);
                Label lblPriorityLabel = new Label("Priority: ");
                lblPriorityLabel.setFont(cardBoldFont);
                lblPriorityLabel.setForeground(labelNavy);
                lblPriorityLabel.setBackground(Color.WHITE);
                Label lblPriorityVal = new Label(priority);
                lblPriorityVal.setFont(cardBoldFont);
                lblPriorityVal.setForeground(priorityColor);
                lblPriorityVal.setBackground(Color.WHITE);
                priorityRow.add(lblPriorityLabel);
                priorityRow.add(lblPriorityVal);
                card.add(priorityRow);
                
                // 3. Time Row (if available)
                if (time != null && !time.isEmpty()) {
                    Panel timeRow = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                    timeRow.setBackground(Color.WHITE);
                    Label lblTimeLabel = new Label("Time: ");
                    lblTimeLabel.setFont(cardBoldFont);
                    lblTimeLabel.setForeground(labelNavy);
                    lblTimeLabel.setBackground(Color.WHITE);
                    Label lblTimeVal = new Label(time);
                    lblTimeVal.setFont(cardRegularFont);
                    lblTimeVal.setForeground(Color.BLACK);
                    lblTimeVal.setBackground(Color.WHITE);
                    timeRow.add(lblTimeLabel);
                    timeRow.add(lblTimeVal);
                    card.add(timeRow);
                }
                
                // 4. Description Row
                Panel descRow = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                descRow.setBackground(Color.WHITE);
                Label lblDescLabel = new Label("Description: ");
                lblDescLabel.setFont(cardBoldFont);
                lblDescLabel.setForeground(labelNavy);
                lblDescLabel.setBackground(Color.WHITE);
                Label lblDescVal = new Label(desc != null ? desc : "");
                lblDescVal.setFont(cardRegularFont);
                lblDescVal.setForeground(Color.BLACK);
                lblDescVal.setBackground(Color.WHITE);
                descRow.add(lblDescLabel);
                descRow.add(lblDescVal);
                card.add(descRow);
                
                // 5. Divider Line
                Panel lineRow = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                lineRow.setBackground(Color.WHITE);
                Label lblDivider = new Label("--------------------------------------------------------------------------------");
                lblDivider.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                lblDivider.setForeground(new Color(200, 205, 215));
                lblDivider.setBackground(Color.WHITE);
                lineRow.add(lblDivider);
                card.add(lineRow);
                
                listItemsPanel.add(card);
            }
            
            if (count == 0) {
                Label lblEmpty = new Label("No events found. Add some events to display.", Label.CENTER);
                lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                lblEmpty.setForeground(Color.GRAY);
                lblEmpty.setBackground(Color.WHITE);
                listItemsPanel.add(lblEmpty);
            }
            
            lblStatus.setText("Loaded " + count + " events.");
            lblStatus.setForeground(new Color(12, 35, 90));
            
            // Re-render and layout the dynamically modified list container
            listItemsPanel.validate();
            listContainer.validate();
            scrollPane.validate();
            rightCard.validate();
            validate();
            repaint();
            
        } catch (SQLException e) {
            lblStatus.setText("\u274C DB Error: " + e.getMessage());
            lblStatus.setForeground(new Color(220, 53, 69));
        }
    }
    
    // Subclassed Panel to serve as a visual Card with background and padding
    private static class CardPanel extends Panel {
        private final Insets insets;
        
        public CardPanel(LayoutManager layout, int top, int left, int bottom, int right) {
            super(layout);
            this.insets = new Insets(top, left, bottom, right);
            setBackground(Color.WHITE);
        }
        
        @Override
        public Insets getInsets() {
            return insets;
        }
    }
    
    public static void main(String[] args) {
        new EventReminder();
    }
}

// AWT Custom Dialog to show the event alerts
class ReminderDialog extends Dialog {
    public ReminderDialog(Frame owner, String eventName, String priority, String desc) {
        super(owner, "\u23F0 Event Reminder!", true);
        
        Color warningBg = new Color(255, 243, 205); // Soft yellow
        Color warningFg = new Color(133, 100, 4);   // Gold-brown
        
        setBackground(warningBg);
        setLayout(new BorderLayout(15, 15));
        
        Label lblTitle = new Label("\uD83D\uDD14 EVENT REMINDER \uD83D\uDD14", Label.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(warningFg);
        lblTitle.setBackground(warningBg);
        add(lblTitle, BorderLayout.NORTH);
        
        Panel centerPanel = new Panel(new GridLayout(3, 1, 5, 5));
        centerPanel.setBackground(warningBg);
        
        Label lblName = new Label("Event: " + eventName, Label.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.BLACK);
        lblName.setBackground(warningBg);
        
        Label lblPriority = new Label("Priority: " + priority, Label.CENTER);
        lblPriority.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPriority.setBackground(warningBg);
        if ("High".equalsIgnoreCase(priority)) {
            lblPriority.setForeground(new Color(220, 53, 69));
        } else if ("Medium".equalsIgnoreCase(priority)) {
            lblPriority.setForeground(new Color(243, 156, 18));
        } else {
            lblPriority.setForeground(new Color(46, 155, 68));
        }
        
        Label lblDesc = new Label("Desc: " + desc, Label.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.DARK_GRAY);
        lblDesc.setBackground(warningBg);
        
        centerPanel.add(lblName);
        centerPanel.add(lblPriority);
        centerPanel.add(lblDesc);
        add(centerPanel, BorderLayout.CENTER);
        
        Button btnOk = new Button("OK, Got it!");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(new Color(46, 155, 68));
        btnOk.setForeground(Color.WHITE);
        btnOk.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        
        Panel btnPanel = new Panel();
        btnPanel.setBackground(warningBg);
        btnPanel.add(btnOk);
        add(btnPanel, BorderLayout.SOUTH);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
                dispose();
            }
        });
        
        setSize(380, 200);
        setLocationRelativeTo(owner);
    }
}
