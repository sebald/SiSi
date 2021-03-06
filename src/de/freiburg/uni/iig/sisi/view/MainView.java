 package de.freiburg.uni.iig.sisi.view;

 import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.SiSiController;
import de.freiburg.uni.iig.sisi.model.net.variant.NetDeviation.DeviationType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;

public class MainView {
	protected Shell shell;
	protected SiSiController controller = new SiSiController();
	
	private String selectedDir = new File("").getAbsolutePath();
	
	private ScrolledComposite mainComposite;
	private Composite activeComposite;

	private Button btnSaveLogsAutomatically;
	private Text saveLogPathText;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainView window = new MainView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {		
		Display display = Display.getDefault();
		createContents(display);
		center(display);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Center the window on primary monitor.
	 */
	private void center(Display display) {
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shell.setLocation(x, y);
	}
	
	/**
	 * Create contents of the window.
	 * @param display 
	 */
	protected void createContents(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setSize(539, 648);
		shell.setText("SiSi - Security-aware Event Log Generator");
		shell.setImage(new Image(shell.getDisplay(), "imgs/shell.png"));
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("File");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem mntmOpenFile = new MenuItem(menu_1, SWT.NONE);
		mntmOpenFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);

				String[] filterNames = new String[] { "PNML", "All Files (*)" };
				String[] filterExtensions = new String[] { "*.pnml", "*" };
				dialog.setFilterPath(System.getProperty("user.dir"));

				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);

				String path = dialog.open();
				if( path != null ) {
					try {
						generateConfigCompositeFor(path);
					} catch (ParserConfigurationException | SAXException | IOException exception) {
						errorMessageBox("Could not load File.", exception);
					}
				}				
			}
		});
		mntmOpenFile.setImage(new Image(shell.getDisplay(), "imgs/open.png"));
		mntmOpenFile.setText("Open File...");
		
		MenuItem mntmOpenExample = new MenuItem(menu_1, SWT.NONE);
		mntmOpenExample.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					try {
						generateConfigCompositeFor("examples/kbv.pnml");
					} catch (ParserConfigurationException | SAXException | IOException exception) {
						errorMessageBox("Could not load Example.", exception);
					}
			}
		});
		mntmOpenExample.setImage(new Image(shell.getDisplay(), "imgs/example.png"));
		mntmOpenExample.setText("Open Example");
		
		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.SEPARATOR);
		mntmNewItem.setText("Separator1");
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().dispose();
				System.exit(0);
			}
		});
		mntmExit.setImage(new Image(shell.getDisplay(), "imgs/exit.png"));
		mntmExit.setText("Exit");
		
		mainComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		mainComposite.setExpandHorizontal(true);
		mainComposite.setExpandVertical(true);
		
		activeComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gl_activeComposite = new GridLayout(1, true);
		gl_activeComposite.marginWidth = 10;
		gl_activeComposite.marginHeight = 10;
		activeComposite.setLayout(gl_activeComposite);
		
		Label lblWelcomeToSisi = new Label(activeComposite, SWT.NONE);
		lblWelcomeToSisi.setFont(SWTResourceManager.getFont("Segoe UI", 30, SWT.BOLD));
		GridData gd_lblWelcomeToSisi = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblWelcomeToSisi.verticalIndent = 50;
		lblWelcomeToSisi.setLayoutData(gd_lblWelcomeToSisi);
		lblWelcomeToSisi.setText("Welcome to SiSi!");
		
		Label lblSecurityawareEvent = new Label(activeComposite, SWT.NONE);
		lblSecurityawareEvent.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblSecurityawareEvent.setText("- A security-aware Event Log Generator -");
		
		Label lblToGetStarted = new Label(activeComposite, SWT.NONE);
		lblToGetStarted.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.ITALIC));
		GridData gd_lblToGetStarted = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblToGetStarted.verticalIndent = 150;
		lblToGetStarted.setLayoutData(gd_lblToGetStarted);
		lblToGetStarted.setText("To get started load a file or an example");
		
		mainComposite.setContent(activeComposite);
		mainComposite.setMinSize(activeComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	

	protected void generateConfigCompositeFor(String path) throws ParserConfigurationException, SAXException, IOException {
		controller.loadModel(path);
		// dispose old view
		activeComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gl_activeComposite = new GridLayout(2, true);
		gl_activeComposite.marginWidth = 10;
		gl_activeComposite.marginHeight = 10;
		activeComposite.setLayout(gl_activeComposite);
		
		// create info
		createLoadinInformationComposite(controller.getProcessModel().getName());

		Label lblSimulationConfiguration = new Label(activeComposite, SWT.NONE);
		GridData gd_lblSimulationConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblSimulationConfiguration.verticalIndent = 10;
		lblSimulationConfiguration.setLayoutData(gd_lblSimulationConfiguration);
		lblSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblSimulationConfiguration.setText("Simulation Configuration");		
		
		// create simulation configuration
		createSimulationConfigraiotnComposite();
		
		Label lblAttackerSpecification = new Label(activeComposite, SWT.NONE);
		GridData gd_lblAttackerSpecification = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblAttackerSpecification.verticalIndent = 20;
		lblAttackerSpecification.setLayoutData(gd_lblAttackerSpecification);
		lblAttackerSpecification.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblAttackerSpecification.setText("Attacker Specification");
		
		// create model configuration
		createProcessModelConfiguration();		
		
		// create violation configuration
		createViolationConfigurationComposite(controller.getProcessModel().getSafetyRequirements());
		
		// log configuration
		Label lblLogConfiguration = new Label(activeComposite, SWT.NONE);
		GridData gd_lblLogConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblLogConfiguration.verticalIndent = 20;
		lblLogConfiguration.setLayoutData(gd_lblLogConfiguration);
		lblLogConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblLogConfiguration.setText("Log Configuration");
		
		Button btnShowLogsAfter = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnShowLogsAfter = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnShowLogsAfter.horizontalIndent = 10;
		btnShowLogsAfter.setLayoutData(gd_btnShowLogsAfter);
		btnShowLogsAfter.setSelection(true);
		btnShowLogsAfter.setToolTipText("Show Event Logs after the Simulation has finished");
		btnShowLogsAfter.setText("Show Logs afterwards");
		// save show log view and endable/disable autosave
		btnShowLogsAfter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = ((Button) e.getSource()).getSelection();
				controller.setShowLogView(value);
				
				btnSaveLogsAutomatically.setEnabled(value);
				if( !value ) {
					btnSaveLogsAutomatically.setSelection(true);
					controller.setAutoSaveLogs(true);
				}
			}
		});
		
		btnSaveLogsAutomatically = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnSaveLogsAutomatically = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSaveLogsAutomatically.horizontalIndent = 10;
		btnSaveLogsAutomatically.setLayoutData(gd_btnSaveLogsAutomatically);
		btnSaveLogsAutomatically.setToolTipText("Save Logs automatically after Simulation has finished");
		btnSaveLogsAutomatically.setText("Save Logs automatically");
		btnSaveLogsAutomatically.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setAutoSaveLogs(((Button) e.getSource()).getSelection());
			}
		});		
		
		Composite selectLogModeComposite = new Composite(activeComposite, SWT.NONE);
		selectLogModeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		selectLogModeComposite.setLayout(new GridLayout(2, false));
		
		Label lblSelectLogMode = new Label(selectLogModeComposite, SWT.NONE);
		GridData gd_lblSelectLogMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectLogMode.horizontalIndent = 5;
		lblSelectLogMode.setLayoutData(gd_lblSelectLogMode);
		lblSelectLogMode.setText("Select Log Mode:");
		
		Combo combo = new Combo(selectLogModeComposite, SWT.READ_ONLY);
		combo.setItems(new String[] {"MXML", "CSV"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setFileMode(((Combo) e.getSource()).getText());
			}
		});		

		Button btnSeparateLogFile = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnSeperateLogFile = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnSeperateLogFile.horizontalIndent = 10;
		btnSeparateLogFile.setLayoutData(gd_btnSeperateLogFile);
		btnSeparateLogFile.setText("Save Log in separate Files");
		btnSeparateLogFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					controller.setSeperateLogs(((Button) e.getSource()).getSelection());
			}
		});			
		
		Composite saveLogComposite = new Composite(activeComposite, SWT.NONE);
		saveLogComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		GridLayout gl_saveLogComposite = new GridLayout(2, false);
		gl_saveLogComposite.horizontalSpacing = 0;
		saveLogComposite.setLayout(gl_saveLogComposite);		
		
		saveLogPathText = new Text(saveLogComposite, SWT.BORDER);
		saveLogPathText.setText(System.getProperty("user.dir") + System.getProperty("file.separator"));
		saveLogPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		saveLogPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controller.setSaveLogPath(((Text) e.getSource()).getText());
			}
		});		

		Button btnSelectLogSaveDir = new Button(saveLogComposite, SWT.CENTER);
		GridData gd_btnSelectLogSaveDir = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectLogSaveDir.minimumHeight = 30;
		gd_btnSelectLogSaveDir.heightHint = 30;
		gd_btnSelectLogSaveDir.minimumWidth = 70;
		gd_btnSelectLogSaveDir.widthHint = 70;
		btnSelectLogSaveDir.setLayoutData(gd_btnSelectLogSaveDir);
		btnSelectLogSaveDir.setText("Save into...");
		btnSelectLogSaveDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		        DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		        directoryDialog.setFilterPath(selectedDir);
		        directoryDialog.setMessage("Please select a directory and click OK");
		        
		        String dir = directoryDialog.open();
		        if(dir != null) {
		        	saveLogPathText.setText(dir + System.getProperty("file.separator"));
		        }
			}
		});		

		Label seperator = new Label(activeComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_seperator = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_seperator.verticalIndent = 20;
		seperator.setLayoutData(gd_seperator);		
		
		Button btnRunSimulation = new Button(activeComposite, SWT.CENTER);
		btnRunSimulation.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		GridData gd_btnRunSimulation = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2);
		gd_btnRunSimulation.verticalIndent = 10;
		gd_btnRunSimulation.minimumWidth = 180;
		gd_btnRunSimulation.heightHint = 50;
		gd_btnRunSimulation.widthHint = 180;
		gd_btnRunSimulation.minimumHeight = 50;
		btnRunSimulation.setLayoutData(gd_btnRunSimulation);
		btnRunSimulation.setText("Run Simulation");
		btnRunSimulation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					controller.runSimulation();
				} catch (SimulationExcpetion | IOException exception) {
					errorMessageBox("Error during Simulation.", exception);
				}
			}
		});
		
		mainComposite.setContent(activeComposite);
		mainComposite.setMinSize(activeComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void createProcessModelConfiguration() {
		Group grpDeviationConfiguration = new Group(activeComposite, SWT.NONE);
		grpDeviationConfiguration.setLayout(new GridLayout(2, false));
		grpDeviationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpDeviationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpDeviationConfiguration.setText("Process Model Configuration");
		
		Label lblRunsWithOriginal = new Label(grpDeviationConfiguration, SWT.NONE);
		GridData gd_lblRunsWithOriginal = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblRunsWithOriginal.horizontalIndent = 3;
		lblRunsWithOriginal.setLayoutData(gd_lblRunsWithOriginal);
		lblRunsWithOriginal.setText("Runs with original Model");
		
		Spinner spinnerRunsWithOriginalModel = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinnerRunsWithOriginalModel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spinnerRunsWithOriginalModel.setSelection(1);
		spinnerRunsWithOriginalModel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controller.updateDeviationParameter(DeviationType.NONE, ((Spinner) e.getSource()).getSelection());
			}
		});
		// set initial
		controller.updateDeviationParameter(DeviationType.NONE, spinnerRunsWithOriginalModel.getSelection());
		
		createCheckBoxSpinnerCombo(DeviationType.SKIPPING, "Runs with Skipping Deviation", grpDeviationConfiguration);
		createCheckBoxSpinnerCombo(DeviationType.SWAPPING, "Runs with Swapping Deviation", grpDeviationConfiguration);
		createCheckBoxSpinnerCombo(DeviationType.AND2XOR, "Runs with AND2XOR Deviation", grpDeviationConfiguration);
		createCheckBoxSpinnerCombo(DeviationType.XOR2AND, "Runs with XOR2AND Deviation", grpDeviationConfiguration);
	}

	protected void createViolationConfigurationComposite(SafetyRequirements safetyRequirements) {
		Group grpViolationConfiguration = new Group(activeComposite, SWT.NONE);
		grpViolationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpViolationConfiguration.setLayout(new GridLayout(2, false));
		grpViolationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpViolationConfiguration.setText("Violations for each Process Model");

		Label lblRunsWithoutViolations = new Label(grpViolationConfiguration, SWT.NONE);
		GridData gd_lblRunsWithoutViolations = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblRunsWithoutViolations.horizontalIndent = 3;
		lblRunsWithoutViolations.setLayoutData(gd_lblRunsWithoutViolations);
		lblRunsWithoutViolations.setText("Runs without Violations");
		
		Spinner spinnerRunsWithoutViolations = new Spinner(grpViolationConfiguration, SWT.BORDER);
		spinnerRunsWithoutViolations.setMinimum(0);
		spinnerRunsWithoutViolations.setSelection(1);
		spinnerRunsWithoutViolations.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spinnerRunsWithoutViolations.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controller.updateRunsWihtoutViolations(((Spinner) e.getSource()).getSelection());
			}
		});
		
		Label lblAuthorizationViolations = new Label(grpViolationConfiguration, SWT.NONE);
		GridData gd_lblAuthorizationViolations = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblAuthorizationViolations.horizontalIndent = 3;
		lblAuthorizationViolations.setLayoutData(gd_lblAuthorizationViolations);
		lblAuthorizationViolations.setText("Runs violating Authorizations");
		
		Spinner spinnerAuthorizationViolations = new Spinner(grpViolationConfiguration, SWT.BORDER);
		spinnerAuthorizationViolations.setToolTipText("Number of Runs with this Violation");
		spinnerAuthorizationViolations.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spinnerAuthorizationViolations.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controller.updateRunsViolatingAuthorizations(((Spinner) e.getSource()).getSelection());
			}
		});
		
		for (Policy policy : controller.getProcessModel().getSafetyRequirements().getPolicies().values()) {
			String text = "Runs violating #"+policy.getId();
			createCheckBoxSpinnerCombo(policy, text, grpViolationConfiguration);
		}
		for (UsageControl uc : controller.getProcessModel().getSafetyRequirements().getUsageControls().values()) {
			String text = "Runs violating #"+uc.getId();
			createCheckBoxSpinnerCombo(uc, text, grpViolationConfiguration);
		}
	}

	protected void createSimulationConfigraiotnComposite() {
		Composite grpSimulationConfiguration = new Composite(activeComposite, SWT.NONE);
		grpSimulationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		grpSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridLayout gl_grpSimulationConfiguration = new GridLayout(2, false);
		gl_grpSimulationConfiguration.marginHeight = 0;
		gl_grpSimulationConfiguration.marginWidth = 0;
		grpSimulationConfiguration.setLayout(gl_grpSimulationConfiguration);
		
		Label lblNumberOfIterations = new Label(grpSimulationConfiguration, SWT.NONE);
		lblNumberOfIterations.setToolTipText("Number of Iterations with the Attacker Specification below");
		GridData gd_lblNumberOfIterations = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNumberOfIterations.horizontalIndent = 10;
		gd_lblNumberOfIterations.minimumWidth = 85;
		lblNumberOfIterations.setLayoutData(gd_lblNumberOfIterations);
		lblNumberOfIterations.setBounds(0, 0, 55, 15);
		lblNumberOfIterations.setText("Number of Iterations");
		
		Spinner spinner = new Spinner(grpSimulationConfiguration, SWT.BORDER);
		spinner.setMaximum(1000);
		spinner.setMinimum(1);
		GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinner.horizontalIndent = 5;
		spinner.setLayoutData(gd_spinner);
		spinner.setBounds(0, 0, 47, 22);
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controller.updateNumberOfIterations(((Spinner) e.getSource()).getSelection());
			}
		});
		
		Button btnConsiderSafety = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnConsiderSafety = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnConsiderSafety.heightHint = 25;
		gd_btnConsiderSafety.minimumHeight = 25;		
		gd_btnConsiderSafety.horizontalIndent = 10;
		btnConsiderSafety.setLayoutData(gd_btnConsiderSafety);
		btnConsiderSafety.setText("Ignore Safety Requirements");
		btnConsiderSafety.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					controller.updateConsiderSafetyRequirements(!((Button) e.getSource()).getSelection());
			}
		});
		
		Button btnForceViolations = new Button(activeComposite, SWT.CHECK);
		btnForceViolations.setToolTipText("WARNING: Enabling this option can greatly increase simulation time!");
		GridData gd_btnForceViolations = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnForceViolations.minimumHeight = 25;
		gd_btnForceViolations.heightHint = 25;
		gd_btnForceViolations.horizontalIndent = 10;
		btnForceViolations.setLayoutData(gd_btnForceViolations);
		btnForceViolations.setText("Force Violations in Runs");
		btnForceViolations.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					controller.updateForeceViolationConfiguration(((Button) e.getSource()).getSelection());
			}
		});
	}

	protected void createLoadinInformationComposite(String modelName) {
		Composite loadingInfoComposite = new Composite(activeComposite, SWT.BORDER);
		loadingInfoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		loadingInfoComposite.setLayout(new GridLayout(2, false));
		
		Label imgLoadingInformation = new Label(loadingInfoComposite, SWT.NONE);
		imgLoadingInformation.setImage(new Image(shell.getDisplay(), "imgs/info.png"));
		
		Label lblLoadingInformation = new Label(loadingInfoComposite, SWT.CENTER);
		lblLoadingInformation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblLoadingInformation.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		lblLoadingInformation.setText("Loaded the Process Model \""+modelName+"\".");
		
	}

	protected void createCheckBoxSpinnerCombo(Object object, String text, Group grp) {
		Button btnCheckButton = new Button(grp, SWT.CHECK);
		GridData gd_btnCheckButton = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCheckButton.horizontalIndent = 3;
		btnCheckButton.setLayoutData(gd_btnCheckButton);
		btnCheckButton.setText(text);
		
		Spinner spinner = new Spinner(grp, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setEnabled(false);
		spinner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		// add references
		btnCheckButton.setData(spinner);
		spinner.setData(object);		
		
		
		// add listener
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = ((Button) e.getSource());
				Spinner s = (Spinner) b.getData();
				// add to configuration
				if( ((Button) e.getSource()).getSelection() ) {
					controller.updateConfigParameter(s.getData(), s.getSelection());
					s.setEnabled(true);
				// remove from configuration
				} else {
					controller.updateConfigParameter(s.getData(), 0);
					s.setEnabled(false);
				}
			}
		});
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				controller.updateConfigParameter(((Spinner) e.getSource()).getData(), ((Spinner) e.getSource()).getSelection());
			}
		});			
	}	
	
	protected void errorMessageBox(String msg, Exception e) {
		MessageBox mb = new MessageBox(shell, SWT.ERROR);
        mb.setText("SiSi Error: " + e.getClass().getSimpleName());
        mb.setMessage(e.getMessage());
        mb.open();
	}

}
