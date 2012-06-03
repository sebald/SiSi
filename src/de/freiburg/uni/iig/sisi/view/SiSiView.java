package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;

public class SiSiView {
	private DataBindingContext bindingContext;

	protected Shell shell;
	protected SiSiViewController controller;
	
	private Composite mainComposite;
	private Button btnCheckButton_1;
	private Spinner spinner_2;

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
		shell.setSize(595, 403);
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
		
		Composite ConfigComposite = new Composite(shell, SWT.NONE);
		FillLayout fl_ConfigComposite = new FillLayout(SWT.HORIZONTAL);
		fl_ConfigComposite.marginWidth = 5;
		fl_ConfigComposite.marginHeight = 5;
		fl_ConfigComposite.spacing = 5;
		ConfigComposite.setLayout(fl_ConfigComposite);
		mainComposite = ConfigComposite;
		
		Group grpViolationConfiguration = new Group(ConfigComposite, SWT.NONE);
		grpViolationConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		grpViolationConfiguration.setText("Violate Safety Requirements");
		grpViolationConfiguration.setLayout(new GridLayout(1, true));
		
		Composite SafetyRequirementComposite = new Composite(grpViolationConfiguration, SWT.NONE);
		SafetyRequirementComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_SafetyRequirementComposite = new GridLayout(2, false);
		gl_SafetyRequirementComposite.marginWidth = 0;
		gl_SafetyRequirementComposite.marginHeight = 0;
		SafetyRequirementComposite.setLayout(gl_SafetyRequirementComposite);
		
		btnCheckButton_1 = new Button(SafetyRequirementComposite, SWT.CHECK);
		btnCheckButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCheckButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCheckButton_1.setText("Check Button");
		
		spinner_2 = new Spinner(SafetyRequirementComposite, SWT.BORDER);
		spinner_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spinner_2.setEnabled(false);
		
		Composite composite = new Composite(grpViolationConfiguration, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		
		Button button = new Button(composite, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		button.setText("Check Button");
		
		Spinner spinner_1 = new Spinner(composite, SWT.BORDER);
		spinner_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spinner_1.setEnabled(false);
		
		Group grpProcessModelConfiguration = new Group(ConfigComposite, SWT.NONE);
		grpProcessModelConfiguration.setText("Process Model Configuration");
		grpProcessModelConfiguration.setLayout(new FillLayout(SWT.HORIZONTAL));
		

	}
	

	protected void generateConfigCompositeFor(String path) throws ParserConfigurationException, SAXException, IOException {
		controller.loadModel(path);
		mainComposite.dispose();
		
		// create new main composite
		Composite ConfigComposite = new Composite(shell, SWT.NONE);
		FillLayout fl_ConfigComposite = new FillLayout(SWT.HORIZONTAL);
		fl_ConfigComposite.marginWidth = 5;
		fl_ConfigComposite.marginHeight = 5;
		fl_ConfigComposite.spacing = 5;
		ConfigComposite.setLayout(fl_ConfigComposite);
		mainComposite = ConfigComposite;
		
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
//			createSingleSafetyRequirementConfig(usageControl, grpViolationConfiguration);
		}		
		
		// update the shell
		shell.layout();
	}
	
	protected void createSingleSafetyRequirementConfig(Policy policy, Group grp) {
		Composite singleSafetyRequirementComposite = new Composite(grp, SWT.NONE);
		singleSafetyRequirementComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_SafetyRequirementComposite = new GridLayout(2, false);
		gl_SafetyRequirementComposite.marginWidth = 0;
		gl_SafetyRequirementComposite.marginHeight = 0;
		singleSafetyRequirementComposite.setLayout(gl_SafetyRequirementComposite);
		
		Button btnCheckButton = new Button(singleSafetyRequirementComposite, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCheckButton.setText("Check Button");
		
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
