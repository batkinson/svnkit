/*
 * Created on 25.05.2005
 */
package org.tmatesoft.svn.cli.command;

import java.io.File;
import java.io.PrintStream;

import org.tmatesoft.svn.cli.SVNCommand;
import org.tmatesoft.svn.core.io.SVNCancelException;
import org.tmatesoft.svn.core.io.SVNLock;
import org.tmatesoft.svn.core.io.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNEventListener;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.util.DebugLog;

public class SVNCommandEventProcessor implements ISVNEventListener {

    private boolean myIsExternal;
    private boolean myIsChanged;
    private boolean myIsExternalChanged;
    private boolean myIsCheckout;
    private boolean myIsExport;

    private final PrintStream myPrintStream;
    private PrintStream myErrStream;

    public SVNCommandEventProcessor(PrintStream out, PrintStream err, boolean checkout) {
        this(out, err, checkout, false);
    }

    public SVNCommandEventProcessor(PrintStream out, PrintStream err, boolean checkout, boolean export) {
        myPrintStream = out;
        myErrStream = err;
        myIsCheckout = checkout;
        myIsExport = export;
    }

    public void svnEvent(SVNEvent event, double progress) {
        if (event.getAction() == SVNEventAction.REVERT) {
            SVNCommand.println(myPrintStream, "Reverted '" + SVNCommand.getPath(event.getFile()) + "'");
        } else if (event.getAction() == SVNEventAction.FAILED_REVERT) {
            SVNCommand.println(myPrintStream, "Failed to revert '" + SVNCommand.getPath(event.getFile()) + "' -- try updating instead.");
        } else if (event.getAction() == SVNEventAction.LOCKED) {        
            String path = event.getPath();
            if (event.getFile() != null) {
                path = SVNCommand.getPath(event.getFile());
            }
            SVNLock lock = event.getLock();
            SVNCommand.println(myPrintStream, "'" + path + "' locked by '" + lock.getOwner() + "'.");
        } else if (event.getAction() == SVNEventAction.UNLOCKED) {
            String path = event.getPath();
            if (event.getFile() != null) {
                path = SVNCommand.getPath(event.getFile());
            }
            SVNCommand.println(myPrintStream, "'" + path + "' unlocked.");
        } else if (event.getAction() == SVNEventAction.UNLOCK_FAILED) {
            SVNCommand.println(myErrStream, "error: " + event.getErrorMessage());
        } else if (event.getAction() == SVNEventAction.LOCK_FAILED) {
            SVNCommand.println(myErrStream, "error: " + event.getErrorMessage());
        } else if (event.getAction() == SVNEventAction.UPDATE_ADD) {
            if (myIsExternal) {
                myIsExternalChanged = true;
            } else {
                myIsChanged = true;
            }
            UpdateCommand.println(myPrintStream, "A    " + SVNCommand.getPath(event.getFile()));
        } else if (event.getAction() == SVNEventAction.UPDATE_DELETE) {
            if (myIsExternal) {
                myIsExternalChanged = true;
            } else {
                myIsChanged = true;
            }
            UpdateCommand.println(myPrintStream, "D    " + UpdateCommand.getPath(event.getFile()));
        } else if (event.getAction() == SVNEventAction.UPDATE_UPDATE) {
            StringBuffer sb = new StringBuffer();
            if (event.getNodeKind() != SVNNodeKind.DIR) {
                if (event.getContentsStatus() == SVNStatusType.CHANGED) {
                    sb.append("U");
                } else if (event.getContentsStatus() == SVNStatusType.CONFLICTED) {
                    sb.append("C");
                } else if (event.getContentsStatus() == SVNStatusType.MERGED) {
                    sb.append("G");
                } else {
                    sb.append(" ");
                }
            } else {
                sb.append(' ');
            }
            if (event.getPropertiesStatus() == SVNStatusType.CHANGED) {
                sb.append("U");
            } else if (event.getPropertiesStatus() == SVNStatusType.CONFLICTED) {
                sb.append("C");
            } else if (event.getPropertiesStatus() == SVNStatusType.CONFLICTED) {
                sb.append("M");
            } else {
                sb.append(" ");
            }
            if (sb.toString().trim().length() != 0) {
                if (myIsExternal) {
                    myIsExternalChanged = true;
                } else { 
                    myIsChanged = true;
                }
            }
            if (event.getLockStatus() == SVNStatusType.LOCK_UNLOCKED) {
                sb.append("B");
            } else {
                sb.append(" ");
            }
            if (sb.toString().trim().length() > 0) {
                UpdateCommand.println(myPrintStream, sb.toString() + "  " + UpdateCommand.getPath(event.getFile()));
            }
        } else if (event.getAction() == SVNEventAction.UPDATE_COMPLETED) {                    
            if (!myIsExternal) {
                if (myIsChanged) {
                    if (myIsCheckout) {
                        UpdateCommand.println(myPrintStream, "Checked out revision " + event.getRevision() + ".");
                    } else if (myIsExport) {
                        UpdateCommand.println(myPrintStream, "Export complete.");
                    } else {
                        UpdateCommand.println(myPrintStream, "Updated to revision " + event.getRevision() + ".");
                    }
                } else {
                    if (myIsExport ) {
                        UpdateCommand.println(myPrintStream, "Export complete.");
                    } else {
                    	DebugLog.error(new Exception("update completed"));
                        UpdateCommand.println(myPrintStream, "At revision " + event.getRevision() + ".");
                    }
                }
            } else {
                if (myIsExternalChanged) {
                    if (myIsCheckout) {
                        UpdateCommand.println(myPrintStream, "Checked out external at revision " + event.getRevision() + ".");
                    } else if (myIsExport) {
                        UpdateCommand.println(myPrintStream, "Export complete.");
                    } else {
                        UpdateCommand.println(myPrintStream, "Updated external to revision " + event.getRevision() + ".");
                    }
                } else {
                    UpdateCommand.println(myPrintStream, "External at revision " + event.getRevision() + ".");
                }
                UpdateCommand.println(myPrintStream);
                myIsExternalChanged = false;
                myIsExternal = false;
            }
        } else if (event.getAction() == SVNEventAction.UPDATE_EXTERNAL) {
            UpdateCommand.println(myPrintStream);
            String path = event.getPath().replace('/', File.separatorChar);
            if (myIsCheckout) {
                UpdateCommand.println(myPrintStream, "Fetching external item into '" + path + "'");
            } else {
                UpdateCommand.println(myPrintStream, "Updating external item at '" + path + "'");
            }
            myIsExternal = true;
        } else if (event.getAction() == SVNEventAction.STATUS_EXTERNAL) {
            UpdateCommand.println(myPrintStream);
            String path = event.getPath().replace('/', File.separatorChar);
            UpdateCommand.println(myPrintStream, "Performing status on external item at '" + path + "'");
            myIsExternal = true;
        } else if (event.getAction() == SVNEventAction.RESTORE) {
            UpdateCommand.println(myPrintStream, "Restored '" + UpdateCommand.getPath(event.getFile()) + "'");
        } else if (event.getAction() == SVNEventAction.ADD) {
            SVNCommand.println(myPrintStream, "A    " + SVNCommand.getPath(event.getFile()));
        } else if (event.getAction() == SVNEventAction.DELETE) {
            SVNCommand.println(myPrintStream, "D    " + SVNCommand.getPath(event.getFile()));
        } else if (event.getAction() == SVNEventAction.SKIP) {
            SVNCommand.println(myPrintStream, "Skipped '" + SVNCommand.getPath(event.getFile()) + "'");
        } 

    }

    public void checkCancelled() throws SVNCancelException {
    }
}