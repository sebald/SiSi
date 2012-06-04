package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.variant.NetDeviation.DeviationType;

public class SiSiView {
	private ArrayList<DataBindingContext> bindingContext = new ArrayList<DataBindingContext>();

	protected Shell shell;
	protected SiSiViewController controller;
	
	private ScrolledComposite mainComposite;
	private Composite activeComposite;
	private Text saveLogPathText;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					SiSiView window = new SiSiView();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		controller = new SiSiViewController();
		
		Display display = Display.getDefault();
		createContents(display);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @param display 
	 */
	protected void createContents(Display display) {
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setSize(513, 559);
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
				dialog.setFilterPath("C:/");

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
		mntmNewItem.setText("Seperator1");
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().dispose();
				System.exit(0);
			}
		});
		mntmExit.setText("Exit");
		
		mainComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		mainComposite.setExpandHorizontal(true);
		mainComposite.setExpandVertical(true);
		
		activeComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gl_activeComposite = new GridLayout(2, true);
		gl_activeComposite.marginWidth = 10;
		gl_activeComposite.marginHeight = 10;
		activeComposite.setLayout(gl_activeComposite);
		
		Composite loadingInfoComposite = new Composite(activeComposite, SWT.BORDER);
		loadingInfoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		loadingInfoComposite.setLayout(new GridLayout(2, false));
		
		Label imgLoadingInformation = new Label(loadingInfoComposite, SWT.NONE);
		imgLoadingInformation.setImage(SWTResourceManager.getImage("D:\\Eclipse Workspaces\\MasterThesis\\SiSi\\imgs\\info.png"));
		
		Label lblLoadingInformation = new Label(loadingInfoComposite, SWT.CENTER);
		lblLoadingInformation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblLoadingInformation.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		lblLoadingInformation.setText("Loading Information");
		
		Label lblSimulationConfiguration = new Label(activeComposite, SWT.NONE);
		GridData gd_lblSimulationConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblSimulationConfiguration.verticalIndent = 10;
		lblSimulationConfiguration.setLayoutData(gd_lblSimulationConfiguration);
		lblSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblSimulationConfiguration.setText("Simulation Configuration");
		
		Composite grpSimulationConfiguration = new Composite(activeComposite, SWT.NONE);
		grpSimulationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridLayout gl_grpSimulationConfiguration = new GridLayout(2, false);
		gl_grpSimulationConfiguration.marginHeight = 0;
		gl_grpSimulationConfiguration.marginWidth = 0;
		grpSimulationConfiguration.setLayout(gl_grpSimulationConfiguration);
		
		Label lblNumberOfIterations = new Label(grpSimulationConfiguration, SWT.NONE);
		lblNumberOfIterations.setToolTipText("Number of Run with the Attacker Specification below");
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
		
		Button btnConsiderSafety = new Button(activeComposite, SWT.CHECK);
		btnConsiderSafety.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		GridData gd_btnConsiderSafety = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnConsiderSafety.horizontalIndent = 10;
		btnConsiderSafety.setLayoutData(gd_btnConsiderSafety);
		btnConsiderSafety.setText("Ignore Safety Requirements");
		
		Label lblAttackerSpecification = new Label(activeComposite, SWT.NONE);
		GridData gd_lblAttackerSpecification = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblAttackerSpecification.verticalIndent = 20;
		lblAttackerSpecification.setLayoutData(gd_lblAttackerSpecification);
		lblAttackerSpecification.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblAttackerSpecification.setText("Attacker Specification");
		
		Group grpViolationConfiguration = new Group(activeComposite, SWT.NONE);
		grpViolationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpViolationConfiguration.setLayout(new GridLayout(2, false));
		grpViolationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpViolationConfiguration.setText("Violation Configuration");
		
		Label lblRunsWithoutViolations = new Label(grpViolationConfiguration, SWT.NONE);
		GridData gd_lblRunsWithoutViolations = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblRunsWithoutViolations.horizontalIndent = 3;
		lblRunsWithoutViolations.setLayoutData(gd_lblRunsWithoutViolations);
		lblRunsWithoutViolations.setText("Runs without Violations");
		
		Spinner spinnerRunsWithoutViolations = new Spinner(grpViolationConfiguration, SWT.BORDER);
		spinnerRunsWithoutViolations.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
			}
		});
		spinnerRunsWithoutViolations.setMinimum(1);
		spinnerRunsWithoutViolations.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Label lblAuthorizationViolations = new Label(grpViolationConfiguration, SWT.NONE);
		GridData gd_lblAuthorizationViolations = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblAuthorizationViolations.horizontalIndent = 3;
		lblAuthorizationViolations.setLayoutData(gd_lblAuthorizationViolations);
		lblAuthorizationViolations.setText("Runs violating Authorizations");
		
		Spinner spinnerAuthorizationViolations = new Spinner(grpViolationConfiguration, SWT.BORDER);
		spinnerAuthorizationViolations.setToolTipText("Number of Runs with this Violation");
		spinnerAuthorizationViolations.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnVioloationFor = new Button(grpViolationConfiguration, SWT.CHECK);
		GridData gd_btnVioloationFor = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnVioloationFor.horizontalIndent = 3;
		btnVioloationFor.setLayoutData(gd_btnVioloationFor);
		btnVioloationFor.setText("Runs violating #p01");
		
		Spinner spinner_2 = new Spinner(grpViolationConfiguration, SWT.BORDER);
		spinner_2.setMinimum(1);
		spinner_2.setEnabled(false);
		spinner_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Group grpDeviationConfiguration = new Group(activeComposite, SWT.NONE);
		grpDeviationConfiguration.setLayout(new GridLayout(2, false));
		grpDeviationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpDeviationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpDeviationConfiguration.setText("Process Model Variants");
		
		Label lblRunsWithOriginal = new Label(grpDeviationConfiguration, SWT.NONE);
		GridData gd_lblRunsWithOriginal = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblRunsWithOriginal.horizontalIndent = 3;
		lblRunsWithOriginal.setLayoutData(gd_lblRunsWithOriginal);
		lblRunsWithOriginal.setText("Runs with original Model");
		
		Spinner spinnerRunsWithOriginalModel = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinnerRunsWithOriginalModel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateSkippingDeviation = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateSkippingDeviation = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateSkippingDeviation.horizontalIndent = 3;
		btnCreateSkippingDeviation.setLayoutData(gd_btnCreateSkippingDeviation);
		btnCreateSkippingDeviation.setText("Create Skipping Deviations");
		
		Spinner spinnerSkippingDeviation = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinnerSkippingDeviation.setMinimum(1);
		spinnerSkippingDeviation.setEnabled(false);
		spinnerSkippingDeviation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateSwappingDeviation = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateSwappingDeviation = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateSwappingDeviation.horizontalIndent = 3;
		btnCreateSwappingDeviation.setLayoutData(gd_btnCreateSwappingDeviation);
		btnCreateSwappingDeviation.setText("Create Swapping Deviations");
		
		Spinner spinner_4 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_4.setMinimum(1);
		spinner_4.setEnabled(false);
		spinner_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateAndxorDeviations = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateAndxorDeviations = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateAndxorDeviations.horizontalIndent = 3;
		btnCreateAndxorDeviations.setLayoutData(gd_btnCreateAndxorDeviations);
		btnCreateAndxorDeviations.setText("Create AND2XOR Deviations");
		
		Spinner spinner_5 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_5.setMinimum(1);
		spinner_5.setEnabled(false);
		spinner_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateXorandDeviations = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateXorandDeviations = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCreateXorandDeviations.horizontalIndent = 3;
		btnCreateXorandDeviations.setLayoutData(gd_btnCreateXorandDeviations);
		btnCreateXorandDeviations.setText("Create XOR2AND Deviations");
		
		Spinner spinner_6 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_6.setMinimum(1);
		spinner_6.setEnabled(false);
		spinner_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Label lblLogConfiguration = new Label(activeComposite, SWT.NONE);
		GridData gd_lblLogConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_lblLogConfiguration.verticalIndent = 20;
		lblLogConfiguration.setLayoutData(gd_lblLogConfiguration);
		lblLogConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblLogConfiguration.setText("Log Configuration");
		new Label(activeComposite, SWT.NONE);
		
		Composite selectLogModeComposite = new Composite(activeComposite, SWT.NONE);
		selectLogModeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		selectLogModeComposite.setLayout(new GridLayout(2, false));
		
		Label lblSelectLogMode = new Label(selectLogModeComposite, SWT.NONE);
		GridData gd_lblSelectLogMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectLogMode.horizontalIndent = 5;
		lblSelectLogMode.setLayoutData(gd_lblSelectLogMode);
		lblSelectLogMode.setText("Select Log Mode:");
		
		Combo combo = new Combo(selectLogModeComposite, SWT.READ_ONLY);
		combo.setItems(new String[] {"CSV", "MXML"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(0);
		
		Composite saveLogComposite = new Composite(activeComposite, SWT.NONE);
		saveLogComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_saveLogComposite = new GridLayout(2, false);
		gl_saveLogComposite.horizontalSpacing = 0;
		saveLogComposite.setLayout(gl_saveLogComposite);
		
		saveLogPathText = new Text(saveLogComposite, SWT.BORDER);
		saveLogPathText.setText("logs/");
		saveLogPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSelectLogSaveDir = new Button(saveLogComposite, SWT.CENTER);
		GridData gd_btnSelectLogSaveDir = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectLogSaveDir.minimumWidth = 70;
		gd_btnSelectLogSaveDir.widthHint = 70;
		btnSelectLogSaveDir.setLayoutData(gd_btnSelectLogSaveDir);
		btnSelectLogSaveDir.setText("Select...");
		
		Button btnSeperateLogFile = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnSeperateLogFile = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_btnSeperateLogFile.horizontalIndent = 10;
		btnSeperateLogFile.setLayoutData(gd_btnSeperateLogFile);
		btnSeperateLogFile.setText("Seperate Log Files");
		
		Button btnRunSimulation = new Button(activeComposite, SWT.CENTER);
		btnRunSimulation.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		GridData gd_btnRunSimulation = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2);
		gd_btnRunSimulation.minimumWidth = 180;
		gd_btnRunSimulation.heightHint = 50;
		gd_btnRunSimulation.widthHint = 180;
		gd_btnRunSimulation.minimumHeight = 50;
		gd_btnRunSimulation.verticalIndent = 20;
		btnRunSimulation.setLayoutData(gd_btnRunSimulation);
		btnRunSimulation.setText("Run Simulation");
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
		
		// create violation configuration
		createViolationConfigurationComposite(controller.getProcessModel().getSafetyRequirements());
		
		// create model configuration
		createProcessModelConfiguration();
		
		Label lblLogConfiguration = new Label(activeComposite, SWT.NONE);
		GridData gd_lblLogConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_lblLogConfiguration.verticalIndent = 20;
		lblLogConfiguration.setLayoutData(gd_lblLogConfiguration);
		lblLogConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblLogConfiguration.setText("Log Configuration");
		new Label(activeComposite, SWT.NONE);
		
		// log configuration
		Composite selectLogModeComposite = new Composite(activeComposite, SWT.NONE);
		selectLogModeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		selectLogModeComposite.setLayout(new GridLayout(2, false));
		
		Label lblSelectLogMode = new Label(selectLogModeComposite, SWT.NONE);
		GridData gd_lblSelectLogMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectLogMode.horizontalIndent = 5;
		lblSelectLogMode.setLayoutData(gd_lblSelectLogMode);
		lblSelectLogMode.setText("Select Log Mode:");
		
		Combo combo = new Combo(selectLogModeComposite, SWT.READ_ONLY);
		combo.setItems(new String[] {"CSV", "MXML"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(0);

		Composite saveLogComposite = new Composite(activeComposite, SWT.NONE);
		saveLogComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_saveLogComposite = new GridLayout(2, false);
		gl_saveLogComposite.horizontalSpacing = 0;
		saveLogComposite.setLayout(gl_saveLogComposite);		
		
		Text saveLogPathText = new Text(saveLogComposite, SWT.BORDER);
		saveLogPathText.setText("logs/");
		saveLogPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSelectLogSaveDir = new Button(saveLogComposite, SWT.CENTER);
		GridData gd_btnSelectLogSaveDir = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectLogSaveDir.minimumWidth = 70;
		gd_btnSelectLogSaveDir.widthHint = 70;
		btnSelectLogSaveDir.setLayoutData(gd_btnSelectLogSaveDir);
		btnSelectLogSaveDir.setText("Select...");		

		Button btnSeperateLogFile = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnSeperateLogFile = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_btnSeperateLogFile.horizontalIndent = 10;
		btnSeperateLogFile.setLayoutData(gd_btnSeperateLogFile);
		btnSeperateLogFile.setText("Seperate Log Files");
		
		Button btnRunSimulation = new Button(activeComposite, SWT.CENTER);
		btnRunSimulation.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		GridData gd_btnRunSimulation = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2);
		gd_btnRunSimulation.minimumWidth = 180;
		gd_btnRunSimulation.heightHint = 50;
		gd_btnRunSimulation.widthHint = 180;
		gd_btnRunSimulation.minimumHeight = 50;
		gd_btnRunSimulation.verticalIndent = 20;
		btnRunSimulation.setLayoutData(gd_btnRunSimulation);
		btnRunSimulation.setText("Run Simulation");		
		
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
		
		createCheckBoxSpinnerCombo(DeviationType.SKIPPING, "Create Skipping Deviations", grpDeviationConfiguration);
		createCheckBoxSpinnerCombo(DeviationType.SWAPPING, "Create Swapping Deviations", grpDeviationConfiguration);
		createCheckBoxSpinnerCombo(DeviationType.AND2XOR, "Create AND2XOR Deviations", grpDeviationConfiguration);
		createCheckBoxSpinnerCombo(DeviationType.XOR2AND, "Create XOR2AND Deviations", grpDeviationConfiguration);
	}

	protected void createViolationConfigurationComposite(SafetyRequirements safetyRequirements) {
		Group grpViolationConfiguration = new Group(activeComposite, SWT.NONE);
		grpViolationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpViolationConfiguration.setLayout(new GridLayout(2, false));
		grpViolationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpViolationConfiguration.setText("Violation Configuration");

		Label lblRunsWithoutViolations = new Label(grpViolationConfiguration, SWT.NONE);
		GridData gd_lblRunsWithoutViolations = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblRunsWithoutViolations.horizontalIndent = 3;
		lblRunsWithoutViolations.setLayoutData(gd_lblRunsWithoutViolations);
		lblRunsWithoutViolations.setText("Runs without Violations");
		
		Spinner spinnerRunsWithoutViolations = new Spinner(grpViolationConfiguration, SWT.BORDER);
		spinnerRunsWithoutViolations.setMinimum(1);
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
		
		for (Policy policy : controller.getProcessModel().getSafetyRequirements().getPolicies()) {
			String text = "Runs violating #"+policy.getId();
			createCheckBoxSpinnerCombo(policy, text, grpViolationConfiguration);
		}
		for (UsageControl uc : controller.getProcessModel().getSafetyRequirements().getUsageControls()) {
			String text = "Runs violating #"+uc.getId();
			createCheckBoxSpinnerCombo(uc, text, grpViolationConfiguration);
		}
	}

	protected void createSimulationConfigraiotnComposite() {
		Composite grpSimulationConfiguration = new Composite(activeComposite, SWT.NONE);
		grpSimulationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridLayout gl_grpSimulationConfiguration = new GridLayout(2, false);
		gl_grpSimulationConfiguration.marginHeight = 0;
		gl_grpSimulationConfiguration.marginWidth = 0;
		grpSimulationConfiguration.setLayout(gl_grpSimulationConfiguration);
		
		Label lblNumberOfIterations = new Label(grpSimulationConfiguration, SWT.NONE);
		lblNumberOfIterations.setToolTipText("Number of Run with the Attacker Specification below");
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
		
		Button btnConsiderSafety = new Button(activeComposite, SWT.CHECK);
		GridData gd_btnConsiderSafety = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnConsiderSafety.horizontalIndent = 10;
		btnConsiderSafety.setLayoutData(gd_btnConsiderSafety);
		btnConsiderSafety.setText("Ignore Safety Requirements");
		btnConsiderSafety.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					controller.updateConsiderSafetyRequirements(!((Button) e.getSource()).getSelection());
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
		btnCheckButton.setData(object);
		spinner.setData(object);		
		
		// bind
		bindingContext.add(bindCheckToSpinner(btnCheckButton, spinner));
		
		
		// add listener
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("restriction")
			@Override
			public void widgetSelected(SelectionEvent e) {
				// add to configuration
				if( ((Button) e.getSource()).getSelection() ) {
					for (DataBindingContext o : bindingContext) {
						Binding b = (Binding) o.getBindings().get(0);
						if( ((SWTObservableValueDecorator) b.getModel()).getWidget() instanceof Spinner ) {
							Spinner s = (Spinner) ((SWTObservableValueDecorator) b.getModel()).getWidget();
							if( e.getSource() == ((SWTObservableValueDecorator) b.getTarget()).getWidget() )
								controller.updateConfigParameter(s.getData(), s.getSelection());
						}
					}
				// remove from configuration
				} else {
					for (DataBindingContext o : bindingContext) {
						Binding b = (Binding) o.getBindings().get(0);
						if( ((SWTObservableValueDecorator) b.getModel()).getWidget() instanceof Spinner ) {
							Spinner s = (Spinner) ((SWTObservableValueDecorator) b.getModel()).getWidget();
							if( e.getSource() == ((SWTObservableValueDecorator) b.getTarget()).getWidget() )
								controller.updateConfigParameter(s.getData(), 0);
						}
					}					
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
	
	protected DataBindingContext bindCheckToSpinner(Button button, Spinner spinner) {
		DataBindingContext bindingContext = new DataBindingContext();
		IObservableValue observeSelectionObserveWidget = SWTObservables.observeSelection(button);
		IObservableValue observeEnabledObserveWidget = SWTObservables.observeEnabled(spinner);
		bindingContext.bindValue(observeSelectionObserveWidget, observeEnabledObserveWidget, null, null);
		return bindingContext;
	}

}
