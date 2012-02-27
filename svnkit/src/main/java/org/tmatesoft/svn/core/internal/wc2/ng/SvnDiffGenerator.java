import java.io.ByteArrayInputStream;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc2.SvnTarget;
    private SvnTarget originalTarget1;
    private SvnTarget originalTarget2;
    private SvnTarget baseTarget;
    private SvnTarget relativeToTarget;
    private SvnTarget repositoryRoot;
    private String externalDiffCommand;
    private SVNDiffOptions diffOptions;
    private boolean fallbackToAbsolutePath;
    private ISVNOptions options;

    private String getDisplayPath(SvnTarget target) {
        String relativePath;
        if (baseTarget == null) {
            relativePath = null;
        } else {
            String targetString = target.getPathOrUrlDecodedString();
            String baseTargetString = baseTarget.getPathOrUrlDecodedString();
            relativePath = SVNPathUtil.getRelativePath(baseTargetString, targetString);
        }

        return relativePath != null ? relativePath : target.getPathOrUrlString();
    }
        this.originalTarget1 = null;
        this.originalTarget2 = null;
    public void setBaseTarget(SvnTarget baseTarget) {
        this.baseTarget = baseTarget;
    }

    public void setUseGitFormat(boolean useGitFormat) {
        this.useGitFormat = useGitFormat;
    }

    public void init(SvnTarget originalTarget1, SvnTarget originalTarget2) {
        this.originalTarget1 = originalTarget1;
        this.originalTarget2 = originalTarget2;
    }

    public void setRepositoryRoot(SvnTarget repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    public void displayDeletedDirectory(SvnTarget target, String revision1, String revision2, OutputStream outputStream) throws SVNException {
    public void displayAddedDirectory(SvnTarget target, String revision1, String revision2, OutputStream outputStream) throws SVNException {
    public void displayPropsChanged(SvnTarget target, String revision1, String revision2, boolean dirWasAdded, SVNProperties originalProps, SVNProperties propChanges, OutputStream outputStream) throws SVNException {
        String displayPath = getDisplayPath(target);
        String targetString1 = originalTarget1.getPathOrUrlDecodedString();
        String targetString2 = originalTarget2.getPathOrUrlDecodedString();


            if (useGitFormat) {
                targetString1 = adjustRelativeToReposRoot(targetString1);
                targetString2 = adjustRelativeToReposRoot(targetString2);
            }

            String newTargetString = displayPath;
            String newTargetString1 = targetString1;
            String newTargetString2 = targetString2;

            String commonAncestor = SVNPathUtil.getCommonPathAncestor(newTargetString1, newTargetString2);
            int commonLength = commonAncestor == null ? 0 : commonAncestor.length();

            newTargetString1 = newTargetString1.substring(commonLength);
            newTargetString2 = newTargetString2.substring(commonLength);

            newTargetString1 = computeLabel(newTargetString, newTargetString1);
            newTargetString2 = computeLabel(newTargetString, newTargetString2);

            if (relativeToTarget != null) {
                //TODO
            String label1 = getLabel(newTargetString1, revision1);
            String label2 = getLabel(newTargetString2, revision2);
            boolean shouldStopDisplaying = displayHeader(outputStream, displayPath, false, SvnDiffCallback.OperationKind.Modified);
            if (useGitFormat) {
                displayGitDiffHeader(outputStream, SvnDiffCallback.OperationKind.Modified, displayPath, displayPath, null);
            }
                String copyFromPath = null;
                SvnDiffCallback.OperationKind operationKind = SvnDiffCallback.OperationKind.Modified;
                label1 = getGitDiffLabel1(operationKind, targetString1, targetString2, copyFromPath, revision1);
                label2 = getGitDiffLabel2(operationKind, targetString1, targetString2, copyFromPath, revision2);
                displayGitDiffHeader(outputStream, operationKind, targetString1, targetString2, copyFromPath);
            displayHeaderFields(outputStream, label1, label2);
        displayPropertyChangesOn(useGitFormat ? targetString1 : displayPath, outputStream);
    private String adjustRelativeToReposRoot(String targetString) {
        if (repositoryRoot != null) {
            String repositoryRootString = repositoryRoot.getPathOrUrlDecodedString();
            String relativePath = SVNPathUtil.getRelativePath(repositoryRootString, targetString);
            return relativePath == null ? "" : relativePath;
        }
        return targetString;
    }

    private String computeLabel(String targetString, String originalTargetString) {
        if (originalTargetString.length() == 0) {
            return targetString;
        } else if (originalTargetString.charAt(0) == '/') {
            return targetString + "\t(..." + originalTargetString + ")";
        } else {
            return targetString + "\t(.../" + originalTargetString + ")";
        }
    }

    public void displayContentChanged(SvnTarget target, File leftFile, File rightFile, String revision1, String revision2, String mimeType1, String mimeType2, SvnDiffCallback.OperationKind operation, File copyFromPath, OutputStream outputStream) throws SVNException {
        String displayPath = getDisplayPath(target);
        String targetString1 = originalTarget1.getPathOrUrlDecodedString();
        String targetString2 = originalTarget2.getPathOrUrlDecodedString();

        if (useGitFormat) {
            targetString1 = adjustRelativeToReposRoot(targetString1);
            targetString2 = adjustRelativeToReposRoot(targetString2);
        String newTargetString = displayPath;
        String newTargetString1 = targetString1;
        String newTargetString2 = targetString2;
        String commonAncestor = SVNPathUtil.getCommonPathAncestor(newTargetString1, newTargetString2);
        int commonLength = commonAncestor == null ? 0 : commonAncestor.length();

        newTargetString1 = newTargetString1.substring(commonLength);
        newTargetString2 = newTargetString2.substring(commonLength);

        newTargetString1 = computeLabel(newTargetString, newTargetString1);
        newTargetString2 = computeLabel(newTargetString, newTargetString2);

        if (relativeToTarget != null) {
            //TODO
        }

        String label1 = getLabel(newTargetString1, revision1);
        String label2 = getLabel(newTargetString2, revision2);
            boolean shouldStopDisplaying = displayHeader(outputStream, displayPath, rightFile == null, operation);
            if (useGitFormat) {
                displayGitDiffHeader(outputStream, operation, displayPath, displayPath, null);
            }
            visitedPaths.add(displayPath);
            if (shouldStopDisplaying) {


            boolean shouldStopDisplaying = displayHeader(outputStream, displayPath, rightFile == null, operation);
            if (useGitFormat) {
                displayGitDiffHeader(outputStream, operation, displayPath, displayPath, null);
            }
            internalDiff(outputStream, displayPath, leftFile, rightFile, label1, label2, operation);
    private void internalDiff(OutputStream outputStream, String displayPath, File file1, File file2, String label1, String label2, SvnDiffCallback.OperationKind operation) throws SVNException {
        String header = getHeaderString(displayPath, label1, label2, operation);
            properties.put(QDiffGeneratorFactory.IGNORE_EOL_PROPERTY, Boolean.valueOf(getDiffOptions().isIgnoreEOLStyle()));
            if (getDiffOptions().isIgnoreAllWhitespace()) {
            } else if (getDiffOptions().isIgnoreAmountOfWhitespace()) {

    private String getHeaderString(String displayPath, String label1, String label2, SvnDiffCallback.OperationKind operation) throws SVNException {
            boolean stopDisplaying = displayHeader(byteArrayOutputStream, displayPath, false, operation);
            if (useGitFormat) {
                displayGitDiffHeader(byteArrayOutputStream, operation, displayPath, displayPath, null);
            }
            if (stopDisplaying) {
            Collection svnDiffOptionsCollection = getDiffOptions().toOptionsCollection();
        return externalDiffCommand;

                byte[] originalValueBytes = getPropertyAsBytes(originalValue, getEncoding());
                byte[] newValueBytes = getPropertyAsBytes(newValue, getEncoding());

                if (originalValueBytes == null) {
                    originalValueBytes = new byte[0];
                } else {
                    originalValueBytes = maybeAppendEOL(originalValueBytes);
                if (newValueBytes == null) {
                    newValueBytes = new byte[0];
                } else {
                    newValueBytes = maybeAppendEOL(newValueBytes);

                QDiffUniGenerator.setup();
                Map properties = new SVNHashMap();

                properties.put(QDiffGeneratorFactory.IGNORE_EOL_PROPERTY, Boolean.valueOf(getDiffOptions().isIgnoreEOLStyle()));
                if (getDiffOptions().isIgnoreAllWhitespace()) {
                    properties.put(QDiffGeneratorFactory.IGNORE_SPACE_PROPERTY, QDiffGeneratorFactory.IGNORE_ALL_SPACE);
                } else if (getDiffOptions().isIgnoreAmountOfWhitespace()) {
                    properties.put(QDiffGeneratorFactory.IGNORE_SPACE_PROPERTY, QDiffGeneratorFactory.IGNORE_SPACE_CHANGE);
                }

                QDiffGenerator generator = new QDiffUniGenerator(properties, "");
                EmptyDetectionWriter writer = new EmptyDetectionWriter(new OutputStreamWriter(outputStream, getEncoding()));
                QDiffManager.generateTextDiff(new ByteArrayInputStream(originalValueBytes), new ByteArrayInputStream(newValueBytes),
                        getEncoding(), writer, generator);
                writer.flush();
    private byte[] maybeAppendEOL(byte[] buffer) {
        if (buffer.length == 0) {
            return buffer;
        }

        byte lastByte = buffer[buffer.length - 1];
        if (lastByte == SVNProperty.EOL_CR_BYTES[0]) {
            return buffer;
        } else if (lastByte != SVNProperty.EOL_LF_BYTES[0]) {
            final byte[] newBuffer = new byte[buffer.length + getEOL().length];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            System.arraycopy(getEOL(), 0, newBuffer, buffer.length, getEOL().length);
            return newBuffer;
        } else {
            return buffer;
        }
    }

    private String getGitDiffLabel1(SvnDiffCallback.OperationKind operationKind, String path1, String path2, String copyFromPath, String revision) {
        if (operationKind == SvnDiffCallback.OperationKind.Deleted) {
            return getLabel("a/" + path1, revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Copied) {
            return getLabel("a/" + copyFromPath, revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Added) {
            return getLabel("/dev/null", revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Modified) {
            return getLabel("a/" + path1, revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Moved) {
            return getLabel("a/" + copyFromPath, revision);
        }
        throw new IllegalArgumentException("Unsupported operation: " + operationKind);
    }

    private String getGitDiffLabel2(SvnDiffCallback.OperationKind operationKind, String path1, String path2, String copyFromPath, String revision) {
        if (operationKind == SvnDiffCallback.OperationKind.Deleted) {
            return getLabel("/dev/null", revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Copied) {
            return getLabel("b/" + path2, revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Added) {
            return getLabel("b/" + path2, revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Modified) {
            return getLabel("b/" + path2, revision);
        } else if (operationKind == SvnDiffCallback.OperationKind.Moved) {
            return getLabel("b/" + path2, revision);
        }
        throw new IllegalArgumentException("Unsupported operation: " + operationKind);
    }

    private void displayGitDiffHeader(OutputStream outputStream, SvnDiffCallback.OperationKind operationKind, String path1, String path2, String copyFromPath) throws SVNException {
        if (operationKind == SvnDiffCallback.OperationKind.Deleted) {
            displayGitDiffHeaderDeleted(outputStream, path1, path2, copyFromPath);
        } else if (operationKind == SvnDiffCallback.OperationKind.Copied) {
            displayGitDiffHeaderCopied(outputStream, path1, path2, copyFromPath);
        } else if (operationKind == SvnDiffCallback.OperationKind.Added) {
            displayGitDiffHeaderAdded(outputStream, path1, path2, copyFromPath);
        } else if (operationKind == SvnDiffCallback.OperationKind.Modified) {
            displayGitDiffHeaderModified(outputStream, path1, path2, copyFromPath);
        } else if (operationKind == SvnDiffCallback.OperationKind.Moved) {
            displayGitDiffHeaderRenamed(outputStream, path1, path2, copyFromPath);
        }
    }

    private void displayGitDiffHeaderAdded(OutputStream outputStream, String path1, String path2, String copyFromPath) throws SVNException {
        try {
            displayString(outputStream, "diff --git a/");
            displayString(outputStream, path1);
            displayString(outputStream, " b/");
            displayString(outputStream, path2);
            displayEOL(outputStream);
            displayString(outputStream, "new file mode 10644");
            displayEOL(outputStream);
        } catch (IOException e) {
            wrapException(e);
        }
    }

    private void displayGitDiffHeaderDeleted(OutputStream outputStream, String path1, String path2, String copyFromPath) throws SVNException {
        try {
            displayString(outputStream, "diff --git a/");
            displayString(outputStream, path1);
            displayString(outputStream, " b/");
            displayString(outputStream, path2);
            displayEOL(outputStream);
            displayString(outputStream, "deleted file mode 10644");
            displayEOL(outputStream);
        } catch (IOException e) {
            wrapException(e);
        }
    }

    private void displayGitDiffHeaderCopied(OutputStream outputStream, String path1, String path2, String copyFromPath) throws SVNException {
        try {
            displayString(outputStream, "diff --git a/");
            displayString(outputStream, copyFromPath);
            displayString(outputStream, " b/");
            displayString(outputStream, path2);
            displayEOL(outputStream);
            displayString(outputStream, "copy from ");
            displayString(outputStream, copyFromPath);
            displayEOL(outputStream);
            displayString(outputStream, "copy to ");
            displayString(outputStream, path2);
            displayEOL(outputStream);
        } catch (IOException e) {
            wrapException(e);
        }
    }

    private void displayGitDiffHeaderRenamed(OutputStream outputStream, String path1, String path2, String copyFromPath) throws SVNException {
        try {
            displayString(outputStream, "diff --git a/");
            displayString(outputStream, copyFromPath);
            displayString(outputStream, " b/");
            displayString(outputStream, path2);
            displayEOL(outputStream);
            displayString(outputStream, "rename from ");
            displayString(outputStream, copyFromPath);
            displayEOL(outputStream);
            displayString(outputStream, "rename to ");
            displayString(outputStream, path2);
            displayEOL(outputStream);
        } catch (IOException e) {
            wrapException(e);
        }
    }

    private void displayGitDiffHeaderModified(OutputStream outputStream, String path1, String path2, String copyFromPath) throws SVNException {
        try {
            displayString(outputStream, "diff --git a/");
            displayString(outputStream, path1);
            displayString(outputStream, " b/");
            displayString(outputStream, path2);
            displayEOL(outputStream);
        } catch (IOException e) {
            wrapException(e);
        }
    protected boolean displayHeader(OutputStream os, String path, boolean deleted, SvnDiffCallback.OperationKind operation) throws SVNException {
    public SVNDiffOptions getDiffOptions() {
        if (diffOptions == null) {
            diffOptions = new SVNDiffOptions();
        return diffOptions;
    }

    public void setExternalDiffCommand(String externalDiffCommand) {
        this.externalDiffCommand = externalDiffCommand;
    }

    public void setRawDiffOptions(List<String> rawDiffOptions) {
        this.rawDiffOptions = rawDiffOptions;
    }

    public void setDiffOptions(SVNDiffOptions diffOptions) {
        this.diffOptions = diffOptions;
    }

    public void setDiffDeleted(boolean diffDeleted) {
        this.diffDeleted = diffDeleted;
    }

    public void setBasePath(File absoluteFile) {
        setBaseTarget(SvnTarget.fromFile(absoluteFile));
    }

    public void setFallbackToAbsolutePath(boolean fallbackToAbsolutePath) {
        this.fallbackToAbsolutePath = fallbackToAbsolutePath;
    }

    public void setOptions(ISVNOptions options) {
        this.options = options;