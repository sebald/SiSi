package de.freiburg.uni.iig.sisi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.simulation.SimulationModel;

public class SiSi {

	private Shell shell;
	private SimulationModel simulationModel = null;

	public SiSi(Display display) {

		this.shell = new Shell(display);
		shell.setText("SiSi");
		shell.setSize(800, 600);

		this.init();
		//this.center(shell);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void init() {

		// create menu bar
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
				try {
					simulationModel = new SimulationModel(path);
				} catch (ParserConfigurationException | SAXException | IOException exception) {
					exception.printStackTrace();
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

		// set layout vertical
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		shell.setLayout(fillLayout);

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

	public static void main(String[] args) {
		Display display = new Display();
		new SiSi(display);
		display.dispose();
	}	
	
}
