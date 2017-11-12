import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class Main {
	JFileChooser chooser;
	String choosertitle;
	private JFrame frame;
	
	private JTextField txtEnterDestination;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 705, 479);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");

		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		datePicker.getJFormattedTextField().setText("Starting Date");
		SpringLayout springLayout = (SpringLayout) datePicker.getLayout();
		springLayout.putConstraint(SpringLayout.SOUTH, datePicker.getJFormattedTextField(), 0, SpringLayout.SOUTH,
				datePicker);
		datePicker.setSize(200, 200);
		datePicker.setBounds(23, 124, 202, 31);
		frame.getContentPane().add(datePicker);

		JTextPane textPane = new JTextPane();
		textPane.setBounds(18, 168, 509, 251);
		frame.getContentPane().add(textPane);

		txtEnterDestination = new JTextField();
		txtEnterDestination.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});
		txtEnterDestination.setBounds(411, 77, 116, 22);
		frame.getContentPane().add(txtEnterDestination);
		txtEnterDestination.setColumns(10);

		JCheckBox chckbxIncludeHeader = new JCheckBox("Include Header");
		chckbxIncludeHeader.setBounds(125, 74, 132, 25);
		frame.getContentPane().add(chckbxIncludeHeader);

		JCheckBox chckbxNse = new JCheckBox("NSE");
		chckbxNse.setSelected(true);
		chckbxNse.setBounds(18, 44, 113, 25);
		frame.getContentPane().add(chckbxNse);

		JCheckBox chckbxBse = new JCheckBox("BSE");
		chckbxBse.setSelected(true);
		chckbxBse.setBounds(18, 74, 113, 25);
		frame.getContentPane().add(chckbxBse);

		JCheckBox chckbxIncludeIndices = new JCheckBox("Include Indices");
		chckbxIncludeIndices.setSelected(true);
		chckbxIncludeIndices.setBounds(125, 44, 132, 25);
		frame.getContentPane().add(chckbxIncludeIndices);

		JButton btnFetchBhavCopy = new JButton("Fetch Bhav Copy");
		btnFetchBhavCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				StringTokenizer st = new StringTokenizer(datePicker.getJFormattedTextField().getText(), "-");
				String yyyy = st.nextToken();
				String month = st.nextToken();
				String mm = "0";
				if (month.equals("01")) {
					mm = "JAN";
				} else if (month.equals("02")) {
					mm = "FEB";
				} else if (month.equals("03")) {
					mm = "MAR";
				} else if (month.equals("04")) {
					mm = "APR";
				} else if (month.equals("05")) {
					mm = "MAY";
				} else if (month.equals("06")) {
					mm = "JUN";
				} else if (month.equals("07")) {
					mm = "JUL";
				} else if (month.equals("08")) {
					mm = "AUG";
				} else if (month.equals("09")) {
					mm = "SEP";
				} else if (month.equals("10")) {
					mm = "OCT";
				} else if (month.equals("11")) {
					mm = "NOV";
				} else if (month.equals("12")) {
					mm = "DEC";
				}
				String dd = st.nextToken();

				if (chckbxNse.isSelected()) {
					FetchParse fp = new FetchParse(
							"https://www.nseindia.com/content/historical/EQUITIES/" + yyyy + "/" + mm + "/cm" + dd + mm
									+ yyyy + "bhav.csv.zip",
							txtEnterDestination.getText(), "NSE", yyyy + month + dd, chckbxIncludeHeader.isSelected(),chckbxIncludeIndices.isSelected(), dd + month + yyyy);
					fp.start();

					try {
						fp.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (chckbxBse.isSelected()) {

					// "http://www.bseindia.com/download/BhavCopy/Equity/EQ" +dd
					// + mm + yyyy.charAt(2) + yyyy.charAt(3) + "_CSV.ZIP"

					System.out.println("http://www.bseindia.com/download/BhavCopy/Equity/EQ" + dd + mm + yyyy.charAt(2)
							+ yyyy.charAt(3) + "_CSV.ZIP");
					FetchParseBSE fp = new FetchParseBSE(
							"http://www.bseindia.com/download/BhavCopy/Equity/EQ" + dd + month + yyyy.charAt(2)
									+ yyyy.charAt(3) + "_CSV.ZIP",
							txtEnterDestination.getText(), "BSE", yyyy + month + dd, chckbxIncludeHeader.isSelected(),
							chckbxIncludeIndices.isSelected());
					fp.start();

					try {
						fp.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		btnFetchBhavCopy.setBounds(548, 394, 127, 25);
		frame.getContentPane().add(btnFetchBhavCopy);

		JTextPane txtpnDestination = new JTextPane();
		txtpnDestination.setText("Destination :");
		txtpnDestination.setBounds(277, 77, 119, 22);
		frame.getContentPane().add(txtpnDestination);

		Button button = new Button("Set Path");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle(choosertitle);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(false);
				//
				if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
					txtEnterDestination.setText(chooser.getSelectedFile().getAbsolutePath());
				} else {
					System.out.println("No Selection ");
				}
			}
		});
		button.setBounds(534, 77, 79, 24);
		frame.getContentPane().add(button);

	}
}
