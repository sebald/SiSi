package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;

public class SiSiView {
	private DataBindingContext bindingContext;

	protected Shell shell;
	protected SiSiViewController controller;
	
	private Composite activeComposite;
	private Composite mainComposite;
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
		createContents();
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
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(608, 522);
		shell.setText("SiSi");
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
		
		MenuItem mntmStartSimulation = new MenuItem(menu, SWT.NONE);
		mntmStartSimulation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					controller.runSimulation();
				} catch (SimulationExcpetion | IOException exception) {
					errorMessageBox("Error during Simulation.", exception);
				}
			}
		});
		mntmStartSimulation.setText("Start Simulation");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		mainComposite = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_mainComposite = new GridLayout(2, true);
		gl_mainComposite.marginWidth = 10;
		gl_mainComposite.marginHeight = 10;
		mainComposite.setLayout(gl_mainComposite);
		
		Composite loadingInfoComposite = new Composite(mainComposite, SWT.BORDER);
		loadingInfoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		loadingInfoComposite.setLayout(new GridLayout(2, false));
		
		Label imgLoadingInformation = new Label(loadingInfoComposite, SWT.NONE);
		imgLoadingInformation.setImage(SWTResourceManager.getImage("D:\\Eclipse Workspaces\\MasterThesis\\SiSi\\imgs\\info.png"));
		
		Label lblLoadingInformation = new Label(loadingInfoComposite, SWT.CENTER);
		lblLoadingInformation.setImage(SWTResourceManager.getImage("D:\\Eclipse Workspaces\\MasterThesis\\SiSi\\imgs\\info.png"));
		lblLoadingInformation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblLoadingInformation.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		lblLoadingInformation.setText("Loading Information");
		
		Label lblSimulationConfiguration = new Label(mainComposite, SWT.NONE);
		GridData gd_lblSimulationConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblSimulationConfiguration.verticalIndent = 10;
		lblSimulationConfiguration.setLayoutData(gd_lblSimulationConfiguration);
		lblSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblSimulationConfiguration.setText("Simulation Configuration");
		
		Composite grpSimulationConfiguration = new Composite(mainComposite, SWT.NONE);
		grpSimulationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpSimulationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridLayout gl_grpSimulationConfiguration = new GridLayout(3, false);
		gl_grpSimulationConfiguration.marginHeight = 0;
		gl_grpSimulationConfiguration.marginWidth = 0;
		grpSimulationConfiguration.setLayout(gl_grpSimulationConfiguration);
		
		Label lblNumberOfRuns = new Label(grpSimulationConfiguration, SWT.NONE);
		GridData gd_lblNumberOfRuns = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNumberOfRuns.horizontalIndent = 3;
		gd_lblNumberOfRuns.minimumWidth = 85;
		lblNumberOfRuns.setLayoutData(gd_lblNumberOfRuns);
		lblNumberOfRuns.setBounds(0, 0, 55, 15);
		lblNumberOfRuns.setText("Number of Runs:");
		
		Spinner spinner = new Spinner(grpSimulationConfiguration, SWT.BORDER);
		spinner.setMaximum(1000);
		spinner.setMinimum(1);
		GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinner.horizontalIndent = 5;
		spinner.setLayoutData(gd_spinner);
		spinner.setBounds(0, 0, 47, 22);
		
		Button btnConsiderSafety = new Button(grpSimulationConfiguration, SWT.CHECK);
		GridData gd_btnConsiderSafety = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnConsiderSafety.verticalIndent = 2;
		gd_btnConsiderSafety.horizontalIndent = 25;
		btnConsiderSafety.setLayoutData(gd_btnConsiderSafety);
		btnConsiderSafety.setText("Consider Safety Requirements");
		
		Label lblAttackerSpecification = new Label(mainComposite, SWT.NONE);
		GridData gd_lblAttackerSpecification = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_lblAttackerSpecification.verticalIndent = 20;
		lblAttackerSpecification.setLayoutData(gd_lblAttackerSpecification);
		lblAttackerSpecification.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblAttackerSpecification.setText("Attacker Specification");
		
		Group grpViolationConfiguration_1 = new Group(mainComposite, SWT.NONE);
		grpViolationConfiguration_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpViolationConfiguration_1.setLayout(new GridLayout(2, false));
		grpViolationConfiguration_1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpViolationConfiguration_1.setText("Violation Configuration");
		
		Label lblCreateAuthorizationViolations = new Label(grpViolationConfiguration_1, SWT.NONE);
		GridData gd_lblCreateAuthorizationViolations = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblCreateAuthorizationViolations.horizontalIndent = 3;
		lblCreateAuthorizationViolations.setLayoutData(gd_lblCreateAuthorizationViolations);
		lblCreateAuthorizationViolations.setText("Create Authorization Violations");
		
		Spinner spinner_1 = new Spinner(grpViolationConfiguration_1, SWT.BORDER);
		spinner_1.setToolTipText("Number of Runs with this Violation");
		spinner_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateVioloationFor = new Button(grpViolationConfiguration_1, SWT.CHECK);
		GridData gd_btnCreateVioloationFor = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateVioloationFor.horizontalIndent = 5;
		btnCreateVioloationFor.setLayoutData(gd_btnCreateVioloationFor);
		btnCreateVioloationFor.setText("Create Violoations for #p01");
		
		Spinner spinner_2 = new Spinner(grpViolationConfiguration_1, SWT.BORDER);
		spinner_2.setEnabled(false);
		spinner_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Group grpDeviationConfiguration = new Group(mainComposite, SWT.NONE);
		grpDeviationConfiguration.setLayout(new GridLayout(2, false));
		grpDeviationConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		grpDeviationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpDeviationConfiguration.setText("Process Model Variants");
		
		Button btnCreateSkippingDeviation = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateSkippingDeviation = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateSkippingDeviation.horizontalIndent = 5;
		btnCreateSkippingDeviation.setLayoutData(gd_btnCreateSkippingDeviation);
		btnCreateSkippingDeviation.setText("Create Skipping Deviations");
		
		Spinner spinner_3 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_3.setEnabled(false);
		spinner_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateSwappingDeviation = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateSwappingDeviation = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateSwappingDeviation.horizontalIndent = 5;
		btnCreateSwappingDeviation.setLayoutData(gd_btnCreateSwappingDeviation);
		btnCreateSwappingDeviation.setText("Create Swapping Deviations");
		
		Spinner spinner_4 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_4.setEnabled(false);
		spinner_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateAndxorDeviations = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateAndxorDeviations = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCreateAndxorDeviations.horizontalIndent = 5;
		btnCreateAndxorDeviations.setLayoutData(gd_btnCreateAndxorDeviations);
		btnCreateAndxorDeviations.setText("Create AND2XOR Deviations");
		
		Spinner spinner_5 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_5.setEnabled(false);
		spinner_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnCreateXorandDeviations = new Button(grpDeviationConfiguration, SWT.CHECK);
		GridData gd_btnCreateXorandDeviations = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCreateXorandDeviations.horizontalIndent = 5;
		btnCreateXorandDeviations.setLayoutData(gd_btnCreateXorandDeviations);
		btnCreateXorandDeviations.setText("Create XOR2AND Deviations");
		
		Spinner spinner_6 = new Spinner(grpDeviationConfiguration, SWT.BORDER);
		spinner_6.setEnabled(false);
		spinner_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Label lblLogConfiguration = new Label(mainComposite, SWT.NONE);
		GridData gd_lblLogConfiguration = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_lblLogConfiguration.verticalIndent = 20;
		lblLogConfiguration.setLayoutData(gd_lblLogConfiguration);
		lblLogConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblLogConfiguration.setText("Log Configuration");
		new Label(mainComposite, SWT.NONE);
		
		Composite selectLogModeComposite = new Composite(mainComposite, SWT.NONE);
		selectLogModeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		selectLogModeComposite.setLayout(new GridLayout(2, false));
		
		Label lblSelectLogMode = new Label(selectLogModeComposite, SWT.NONE);
		lblSelectLogMode.setText("Select Log Mode:");
		
		Combo combo = new Combo(selectLogModeComposite, SWT.READ_ONLY);
		combo.setItems(new String[] {"CSV", "MXML"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(0);
		
		Composite saveLogComposite = new Composite(mainComposite, SWT.NONE);
		saveLogComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_saveLogComposite = new GridLayout(2, false);
		gl_saveLogComposite.horizontalSpacing = 0;
		saveLogComposite.setLayout(gl_saveLogComposite);
		
		saveLogPathText = new Text(saveLogComposite, SWT.BORDER);
		saveLogPathText.setText("Select dir to store");
		saveLogPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSelectLogSaveDir = new Button(saveLogComposite, SWT.CENTER);
		GridData gd_btnSelectLogSaveDir = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectLogSaveDir.minimumWidth = 70;
		gd_btnSelectLogSaveDir.widthHint = 70;
		btnSelectLogSaveDir.setLayoutData(gd_btnSelectLogSaveDir);
		btnSelectLogSaveDir.setText("Select...");
		
		Button btnRunSimulation = new Button(mainComposite, SWT.CENTER);
		btnRunSimulation.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		GridData gd_btnRunSimulation = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2);
		gd_btnRunSimulation.minimumWidth = 180;
		gd_btnRunSimulation.heightHint = 50;
		gd_btnRunSimulation.widthHint = 180;
		gd_btnRunSimulation.minimumHeight = 50;
		gd_btnRunSimulation.verticalIndent = 20;
		btnRunSimulation.setLayoutData(gd_btnRunSimulation);
		btnRunSimulation.setText("Run Simulation");
		scrolledComposite.setContent(mainComposite);
		scrolledComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}
	

	protected void generateConfigCompositeFor(String path) throws ParserConfigurationException, SAXException, IOException {
		controller.loadModel(path);
		activeComposite.dispose();
		
		// create new main composite
		ScrolledComposite scrolledComposite = new ScrolledComposite(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		activeComposite = scrolledComposite;
		
		// config composite
		Composite ConfigComposite = new Composite(scrolledComposite, SWT.NONE);
		FillLayout fl_ConfigComposite = new FillLayout(SWT.HORIZONTAL);
		fl_ConfigComposite.marginWidth = 5;
		fl_ConfigComposite.marginHeight = 5;
		fl_ConfigComposite.spacing = 5;
		ConfigComposite.setLayout(fl_ConfigComposite);
		
		// group for the violation configuration (e.g. what mutans should be generated)
		Group grpViolationConfiguration = new Group(ConfigComposite, SWT.NONE);
		grpViolationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpViolationConfiguration.setText("Violate Safety Requirements");
		grpViolationConfiguration.setLayout(new GridLayout(1, true));
		
		// add an option item for every policy + usage control
		for (Policy policy : controller.getProcessModel().getSafetyRequirements().getPolicies()) {
			createSingleSafetyRequirementConfig(policy, grpViolationConfiguration);
		}
		for (UsageControl usageControl : controller.getProcessModel().getSafetyRequirements().getUsageControls()) {
			createSingleSafetyRequirementConfig(usageControl, grpViolationConfiguration);
		}		
		
		// group for the process model configuration (e.g. what variants should be generated)
		Group grpProcessModelConfiguration = new Group(ConfigComposite, SWT.NONE);
		grpProcessModelConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpProcessModelConfiguration.setText("Process Model Configuration");
		grpProcessModelConfiguration.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// update the shell
		shell.layout();
	}
	
	protected void createSingleSafetyRequirementConfig(ModelObject modelObject, Group grp) {
		Composite singleSafetyRequirementComposite = new Composite(grp, SWT.NONE);
		singleSafetyRequirementComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_SafetyRequirementComposite = new GridLayout(2, false);
		gl_SafetyRequirementComposite.marginWidth = 0;
		gl_SafetyRequirementComposite.marginHeight = 0;
		singleSafetyRequirementComposite.setLayout(gl_SafetyRequirementComposite);
		
		Button btnCheckButton = new Button(singleSafetyRequirementComposite, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCheckButton.setText("ID: #" + modelObject.getId() + " (Type: " + modelObject.getClass().getSimpleName() + ")");
		
		Spinner spinner = new Spinner(singleSafetyRequirementComposite, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spinner.setEnabled(false);
		
		bindingContext = bindCheckToSpinner(btnCheckButton, spinner);
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
