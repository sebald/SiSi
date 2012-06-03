package de.freiburg.uni.iig.sisi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;

public class SiSi {

	private Shell shell;
	private ProcessModel processModel = null;
	
	private Label nothingLoadedLabel;

	public SiSi(Display display) {

		this.shell = new Shell(display);
		shell.setText("SiSi");
		shell.setImage(new Image(shell.getDisplay(), "imgs/shell.png"));
		
		init();
		//center(shell);

		//shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public ProcessModel getProcessModel() {
		return processModel;
	}

	public void setProcessModel(ProcessModel processModel) {
		this.processModel = processModel;
	}	
		
	
	protected void init() {
		createMenu();
		
		// info that no model is loaded
		shell.setSize(300, 200);
		
		nothingLoadedLabel = new Label(shell, SWT.CENTER);
		nothingLoadedLabel.setText("No Process Model loaded." + System.lineSeparator() + "Load a Model to start.");
		nothingLoadedLabel.setBounds(45, 45, 200, 30);
	
	}

	protected void createMenu() {
		Menu menuBar = new Menu(shell, SWT.BAR);
		MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE);
		cascadeFileMenu.setText("File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		cascadeFileMenu.setMenu(fileMenu);

		// open file
		MenuItem openItem = new MenuItem(fileMenu, SWT.PUSH);
		openItem.setText("Open File");
		openItem.setImage(new Image(shell.getDisplay(), "imgs/open.png"));
		openItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);

				String[] filterNames = new String[] { "PNML", "All Files (*)" };
				String[] filterExtensions = new String[] { "*.pnml", "*" };
				dialog.setFilterPath("C:/");

				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);

				String path = dialog.open();
				if( path != null ) {
					try {
						loadModel(path);
					} catch (ParserConfigurationException | SAXException | IOException e) {
						errorMessageBox("Could not load File.", e);
					}
				}
			}
		});

		// load KBV example
		MenuItem exampleItem = new MenuItem(fileMenu, SWT.PUSH);
		exampleItem.setText("Load Example");
		exampleItem.setImage(new Image(shell.getDisplay(), "imgs/example.png"));
		exampleItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
					try {
						loadModel("examples/kbv.pnml");
					} catch (ParserConfigurationException | SAXException | IOException e) {
						errorMessageBox("Could not load Example.", e);
					}
			}
		});		
		
		// exit program
		MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("Exit");
		exitItem.setImage(new Image(shell.getDisplay(), "imgs/close.png"));
		exitItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().dispose();
				System.exit(0);
			}
		});

		shell.setMenuBar(menuBar);		
	}
	
	protected void createCheckBox(ModelObject modelObject) {
		Button b = new Button(shell, SWT.CHECK);
		b.setText("Requirement #"+modelObject.getId()+":");
		b.pack();
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
			}
		});
	}

	protected void center(Shell shell) {
		Rectangle bds = shell.getDisplay().getBounds();
		Point p = shell.getSize();
		int nLeft = (bds.width - p.x) / 2;
		int nTop = (bds.height - p.y) / 2;
		shell.setBounds(nLeft, nTop, p.x, p.y);
	}

	protected void errorMessageBox(String msg, Exception e) {
		MessageBox mb = new MessageBox(shell, SWT.ERROR);
        mb.setText("SiSi Error: " + e.getClass().getSimpleName());
        mb.setMessage(e.getMessage());
        mb.open();
	}
	
	private void loadModel(String path) throws ParserConfigurationException, SAXException, IOException{
		setProcessModel(new ProcessModel(path));
		
		// hide info label
		nothingLoadedLabel.dispose();
		
		// set new layout
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);
		
		Group mutationGroup = new Group(shell, SWT.NONE);
		mutationGroup.setText("Config Mutations");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan= 2;
		mutationGroup.setLayoutData(gridData);
		mutationGroup.setLayout(new RowLayout(SWT.VERTICAL));
		createMutationConfig(mutationGroup);
	
		Group variantGroup = new Group(shell, SWT.NONE);
		variantGroup.setText("Config Variations");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan= 2;
		variantGroup.setLayoutData(gridData);
		variantGroup.setLayout(new RowLayout(SWT.VERTICAL));
		createVariantConfig(variantGroup);
		
		shell.pack();
	}
	
	private void createMutationConfig(Group group) {
		Label maxMutationLabel = new Label(group, SWT.LEFT);
		maxMutationLabel.setText("Max. # Authorization Mutants:");
		
		Spinner spinner = new Spinner (group, SWT.BORDER);
		spinner.setMinimum(0);
		spinner.setMaximum(100);
		spinner.setSelection(0);
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		
		for (Policy policy : getProcessModel().getSafetyRequirements().getPolicies()) {
			createCheckBox(policy);
		}
		for (UsageControl uc : getProcessModel().getSafetyRequirements().getUsageControls()) {
			createCheckBox(uc);
		}
	}

	private void createVariantConfig(Group variantGroup) {
		Text text = new Text(variantGroup, SWT.NONE);
		text.setText("Another test");
	}	
	
	public static void main(String[] args) {
		Display display = new Display();
		new SiSi(display);
		display.dispose();
	}
}
