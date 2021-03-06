package de.freiburg.uni.iig.sisi.view;

import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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

import de.freiburg.uni.iig.sisi.SiSiController;
import de.freiburg.uni.iig.sisi.log.EventLog;
import de.freiburg.uni.iig.sisi.log.LogGenerator.FileMode;
import de.freiburg.uni.iig.sisi.log.MutationEvent;
import de.freiburg.uni.iig.sisi.log.ProcessInstanceInformation;
import de.freiburg.uni.iig.sisi.log.SimulationEvent;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.AuthorizationMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.PolicyMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.UsageControlMutant;

public class LogView extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			LogView shell = new LogView(new SiSiController());
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


	private final SiSiController controller;
	private Text rawDataText;
	private Table eventsTable;
	private Tree tree;
	private Text violationDataText;
	private Text deviationDataText;

	/**
	 * Create the shell.
	 * @param display
	 */
	public LogView(SiSiController controller) {
		super(Display.getDefault(), SWT.SHELL_TRIM);
		setLayout(new FillLayout(SWT.VERTICAL));
		this.controller = controller;
			
		Composite mainComposite = new Composite(this, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		
		ToolBar toolBar = new ToolBar(mainComposite, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		ToolItem tltmSave = new ToolItem(toolBar, SWT.NONE);
		tltmSave.setToolTipText("Save currently selected Log");
		tltmSave.setText("Save");
		tltmSave.setImage(new Image(this.getDisplay(), "imgs/save.png"));
		tltmSave.setData(this.controller.getSaveToPath());
		tltmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
		        fd.setText("Save Log(s) to...");
		        fd.setFilterPath((String) ((ToolItem) e.getSource()).getData());
		        String[] filterExt = { "*.log", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        saveFileTo(selected);				
			}
		});
		
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
		tbtmViolations.setText("Violation Data");
		
		violationDataText = new Text(tabFolder, SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		violationDataText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		violationDataText.setFont(SWTResourceManager.getFont("Courier New", 11, SWT.NORMAL));
		tbtmViolations.setControl(violationDataText);
		
		// tab deviations
		TabItem tbtmDeviationData = new TabItem(tabFolder, SWT.NONE);
		tbtmDeviationData.setText("Deviation Data");
		
		deviationDataText = new Text(tabFolder, SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		deviationDataText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		deviationDataText.setFont(SWTResourceManager.getFont("Courier New", 11, SWT.NORMAL));
		tbtmDeviationData.setControl(deviationDataText);
		
		// tab raw data
		TabItem tbtmRawData = new TabItem(tabFolder, SWT.NONE);
		tbtmRawData.setText("Raw Data");
		rawDataText = new Text(tabFolder, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		rawDataText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
		tree = new Tree(scrolledTreeComposite, SWT.NONE);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				writeLogToTabs((String) ((Tree) e.getSource()).getSelection()[0].getData());
			}
		});
		
		TreeItem trtmFullEventLog = new TreeItem(tree, SWT.NONE);
		trtmFullEventLog.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		trtmFullEventLog.setImage(new Image(this.getDisplay(), "imgs/report.png"));
		trtmFullEventLog.setText("Composite Log");
		trtmFullEventLog.setData("all");
		
		// add single runs to tree 
		if( this.controller.getLogGenerator() != null ) {
			for (Entry<String, EventLog> logEntry : this.controller.getLogGenerator().getEventLogs().entrySet()) {
				createTreeEntry(logEntry, trtmFullEventLog);
			}
		}
		scrolledTreeComposite.setContent(tree);
		scrolledTreeComposite.setMinSize(tree.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		createContents();
		
		// select "all"
		tree.setSelection(trtmFullEventLog);
		trtmFullEventLog.setExpanded(true);
		writeLogToTabs("all");
		resizeTable(eventsTable);
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
		// clear + reset table
		eventsTable.clearAll();
		eventsTable.setItemCount(0);
		
		// print full log?
		if(id.equals("all")) {
			// event table
			for (EventLog log : controller.getLogGenerator().getEventLogs().values()) {
				createEventTableItems(log, eventsTable);
			}
			// violation data
			String violationData = "";
			for (MutationEvent mutationEvent : controller.getLogGenerator().getMutationLog().values() ) {
				violationData += mutationEvent.toString() + System.lineSeparator() + System.lineSeparator();
			}
			violationDataText.setText(violationData);
			// deviation data
			String deviationData = "";
			for (ProcessInstanceInformation instanceInformation : controller.getLogGenerator().getModelMap().values()) {
				deviationData += instanceInformation.toString() + System.lineSeparator() + System.lineSeparator();
			}
			deviationDataText.setText(deviationData);			
			// raw data
			if( controller.getLogGenerator().getFileMode() == FileMode.CSV ) {
				rawDataText.setText(controller.getLogGenerator().logsToCSV());
			} else {
				rawDataText.setText(controller.getLogGenerator().logsToMXML());
			}
			
		} else {
			// event table
			createEventTableItems(controller.getLogGenerator().getEventLogs().get(id), eventsTable);
			// violation data
			if( controller.getLogGenerator().getMutationLog().containsKey(id) )
				violationDataText.setText(controller.getLogGenerator().getMutationLog().get(id).toString());
			// deviation data
			if( controller.getLogGenerator().getModelMap().containsKey(id) )
				deviationDataText.setText(controller.getLogGenerator().getModelMap().get(id).toString());
			// raw data
			if( controller.getLogGenerator().getFileMode() == FileMode.CSV ) {
				rawDataText.setText(controller.getLogGenerator().logToCSV(id));
			} else {
				rawDataText.setText(controller.getLogGenerator().logToMXML(id));
			}
		}
	}
	
	private void createEventTableItems(EventLog log, Table table){
		// is there some violation?
		String mutatedTask1 = "";
		String mutatedTask2 = ""; // for policy/uc violations
		if( controller.getLogGenerator().getMutationLog().containsKey(log.getEvents().getFirst().getSimulationID()) ) {
			MutationEvent mutationEvent = controller.getLogGenerator().getMutationLog().get(log.getEvents().getFirst().getSimulationID());
			if( mutationEvent.getMutant() instanceof AuthorizationMutant ) {
				mutatedTask1 = mutationEvent.getObjectViolated().getId();
			} else if( mutationEvent.getMutant() instanceof PolicyMutant ) {
				mutatedTask1 = ((Policy) mutationEvent.getMutant().getActivator()).getObjective().getId();
				mutatedTask2 = ((Policy) mutationEvent.getMutant().getActivator()).getEventually().getId();
			} else if ( mutationEvent.getMutant() instanceof UsageControlMutant ) {
				mutatedTask1 = ((UsageControl) mutationEvent.getMutant().getActivator()).getObjective().getId();
				mutatedTask2 = ((UsageControl) mutationEvent.getMutant().getActivator()).getEventually().getId();
			}
		}		
		for (SimulationEvent e : log.getEvents()) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(new String[] {e.getSimulationID(), e.getTransition().getId(), e.getTransition().getName(), e.getSubject().getName(), e.getUsedObjects().toString()});
			if( mutatedTask1.equals(e.getTransition().getId()) || mutatedTask2.equals(e.getTransition().getId()) )
				tableItem.setBackground(new Color(getDisplay(), new RGB(255, 229, 229) ));
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

	protected void saveFileTo(String path) {
		controller.saveLog((String) tree.getSelection()[0].getData(), path);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
