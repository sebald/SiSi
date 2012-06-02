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
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;

public class SiSi {

	private Shell shell;
	private ProcessModel simulationModel = null;
	
	private Label nothingLoadedLabel;
	private Label bigNotificationLabel;

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
	
	protected void createCheckBoxes() {
		for (int i = 0; i < 10; i++) {
			Button b = new Button(shell, SWT.CHECK);
			b.setText("This is cb #" + i);
			b.pack();

			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println(e);
				}
			});
		}
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
		simulationModel = new ProcessModel(path);
		
		// hide info label
		nothingLoadedLabel.dispose();
		
		// set new layout
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);
		
		Group group = new Group(shell, SWT.NONE);
		group.setText("Config Mutations");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan= 2;
		group.setLayoutData(gridData);
		group.setLayout(new RowLayout(SWT.VERTICAL));
		Text text = new Text(group, SWT.NONE);
		text.setText("Another test");
	
		group = new Group(shell, SWT.NONE);
		group.setText("Config Variations");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan= 2;
		group.setLayoutData(gridData);
		group.setLayout(new RowLayout(SWT.VERTICAL));
		text = new Text(group, SWT.NONE);
		text.setText("Another test");
		
		shell.pack();
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		new SiSi(display);
		display.dispose();
	}	
	
}
