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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import de.freiburg.uni.iig.sisi.log.EventLog;
import de.freiburg.uni.iig.sisi.simulation.SimulationEvent;

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
	private Table eventsTable;

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
		
		// tab events (table)
		TabItem tbtmEvents = new TabItem(tabFolder, SWT.NONE);
		tbtmEvents.setText("Events");
		
		eventsTable = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		eventsTable.setLinesVisible(true);
		tbtmEvents.setControl(eventsTable);
		eventsTable.setHeaderVisible(true);
		
		TableColumn tblclmnCaseId = new TableColumn(eventsTable, SWT.NONE);
		tblclmnCaseId.setWidth(100);
		tblclmnCaseId.setText("Case ID");
		TableColumn tblclmnTaskId = new TableColumn(eventsTable, SWT.NONE);
		tblclmnTaskId.setWidth(100);
		tblclmnTaskId.setText("Task ID");
		TableColumn tblclmnTask = new TableColumn(eventsTable, SWT.NONE);
		tblclmnTask.setWidth(100);
		tblclmnTask.setText("Task");
		TableColumn tblclmnSubject = new TableColumn(eventsTable, SWT.NONE);
		tblclmnSubject.setWidth(100);
		tblclmnSubject.setText("Subject");
		TableColumn tblclmnObjectsUsed = new TableColumn(eventsTable, SWT.NONE);
		tblclmnObjectsUsed.setWidth(100);
		tblclmnObjectsUsed.setText("Object(s) used");
		
		// tab violations
		TabItem tbtmViolations = new TabItem(tabFolder, SWT.NONE);
		tbtmViolations.setText("Violations");
		
		// tab raw data
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
		
		// create tree
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
		
		// add single runs to tree 
		if( this.controller.getLogGenerator() != null ) {
			for (Entry<String, EventLog> logEntry : this.controller.getLogGenerator().getEventLogs().entrySet()) {
				createTreeEntry(logEntry, trtmFullEventLog);
			}
		}
			
		trtmFullEventLog.setExpanded(true);
		scrolledTreeComposite.setContent(tree);
		scrolledTreeComposite.setMinSize(tree.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		createContents();
		
		// select "all"
		tree.setSelection(trtmFullEventLog);
		writeLogToTabs("all");
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
		// clear tables
		eventsTable.clearAll();
		eventsTable.setItemCount(0);
		
		// print full log?
		if(id.equals("all")) {
			// event table
			for (EventLog log : controller.getLogGenerator().getEventLogs().values()) {
				createTableItems(log, eventsTable);
			}
			resizeTable(eventsTable);
			
			// raw data
			rawDataText.setText(controller.getLogGenerator().getFullLog());
		} else {
			// event table
			createTableItems(controller.getLogGenerator().getEventLogs().get(id), eventsTable);	
			resizeTable(eventsTable);
			// raw data
			rawDataText.setText(controller.getLogGenerator().logToCSV(id));
		}
	}
	
	private void createTableItems(EventLog log, Table table){
		for (SimulationEvent e : log.getEvents()) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(new String[] {e.getSimulationID(), e.getTransition().getId(), e.getTransition().getName(), e.getSubject().getName(), e.getUsedObjects().toString()});
		}
	}
	
	private static void resizeTable(Table table) {
		int colSize = 0;
	    for (TableColumn column : table.getColumns()) {
	    	column.pack();
	    	colSize += column.getWidth();
	    }
	    int scrollBarWidth = 21;
	    if( table.getItemCount() < 33 )
	    	scrollBarWidth = 0;
	    int spaceLeft = table.getSize().x - colSize - scrollBarWidth;
	    if( spaceLeft > 0 ) {
	    	int additionalSpace = spaceLeft/table.getColumnCount();
	    	for (TableColumn column : table.getColumns()) {
				column.setWidth(column.getWidth()+additionalSpace);
			}
	    }
	}

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
