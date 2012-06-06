package de.freiburg.uni.iig.sisi.view;

import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import de.freiburg.uni.iig.sisi.log.EventLog;

public class LogView extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			LogView shell = new LogView(new SiSiViewController());
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private SiSiViewController controller;
	private Text rawDataText;

	/**
	 * Create the shell.
	 * @param display
	 */
	public LogView(SiSiViewController controller) {
		super(Display.getDefault(), SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.VERTICAL));
		this.controller = controller;
			
		Composite mainComposite = new Composite(this, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		
		ToolBar toolBar = new ToolBar(mainComposite, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		ToolItem tltmSave = new ToolItem(toolBar, SWT.NONE);
		tltmSave.setImage(new Image(this.getDisplay(), "imgs/save.png"));
		
		ScrolledComposite scrolledFolderComposite = new ScrolledComposite(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledFolderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		scrolledFolderComposite.setExpandHorizontal(true);
		scrolledFolderComposite.setExpandVertical(true);
		
		TabFolder tabFolder = new TabFolder(scrolledFolderComposite, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		
		TabItem tbtmEvents = new TabItem(tabFolder, SWT.NONE);
		tbtmEvents.setText("Events");
		
		TabItem tbtmViolations = new TabItem(tabFolder, SWT.NONE);
		tbtmViolations.setText("Violations");
		
		TabItem tbtmRawData = new TabItem(tabFolder, SWT.NONE);
		tbtmRawData.setText("Raw Data");
		rawDataText = new Text(tabFolder, SWT.V_SCROLL);
		tbtmRawData.setControl(rawDataText);
		scrolledFolderComposite.setContent(tabFolder);
		scrolledFolderComposite.setMinSize(tabFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		ScrolledComposite scrolledTreeComposite = new ScrolledComposite(mainComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledTreeComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_scrolledTreeComposite.minimumWidth = 150;
		gd_scrolledTreeComposite.widthHint = 150;
		scrolledTreeComposite.setLayoutData(gd_scrolledTreeComposite);
		scrolledTreeComposite.setExpandHorizontal(true);
		scrolledTreeComposite.setExpandVertical(true);
		
		Tree tree = new Tree(scrolledTreeComposite, SWT.NONE);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				writeLogToTabs((String) ((Tree) e.getSource()).getSelection()[0].getData());
			}
		});
		
		TreeItem trtmFullEventLog = new TreeItem(tree, SWT.NONE);
		trtmFullEventLog.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		trtmFullEventLog.setImage(new Image(this.getDisplay(), "imgs/report.png"));
		trtmFullEventLog.setText("Event Log");
		trtmFullEventLog.setData("all");
		
		if( this.controller.getLogGenerator() != null ) {
			for (Entry<String, EventLog> logEntry : this.controller.getLogGenerator().getEventLogs().entrySet()) {
				createTreeEntry(logEntry, trtmFullEventLog);
			}
		}
		
		TreeItem trtmChild = new TreeItem(trtmFullEventLog, SWT.NONE);
		trtmChild.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/obj16/file_obj.gif"));
		trtmChild.setText("999-999-999");
		
		trtmFullEventLog.setExpanded(true);
		scrolledTreeComposite.setContent(tree);
		scrolledTreeComposite.setMinSize(tree.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		createContents();
	}
	
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SiSi - Log");
		setSize(853, 735);
		setImage(new Image(this.getDisplay(), "imgs/shell.png"));
		
		//center
	    Monitor primary = Display.getDefault().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    setLocation(x, y);

	}

	protected void createTreeEntry(Entry<String, EventLog> logEntry, TreeItem parent) {
		TreeItem trtmChild = new TreeItem(parent, SWT.NONE);
		trtmChild.setText(logEntry.getKey());
		trtmChild.setData(logEntry.getKey());
		trtmChild.setImage(new Image(this.getDisplay(), "imgs/singleLog.png"));
	}
	
	protected void writeLogToTabs(String id) {
		// print full log?
		if(id.equals("all")) {
			rawDataText.setText(controller.getLogGenerator().getFullLog());
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
