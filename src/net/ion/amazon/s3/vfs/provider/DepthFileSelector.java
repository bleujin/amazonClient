package net.ion.amazon.s3.vfs.provider;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;

public class DepthFileSelector implements FileSelector {
	private final int maxDepth;
	private final int minDepth;

	public DepthFileSelector() { // * Create a file selector that will select ALL files.
		this(0, Integer.MAX_VALUE);
	}

	public DepthFileSelector(int depth) { //* Create a file selector that will select all files up to and including the directory depth.
		this(0, depth);
	}

	public DepthFileSelector(int min, int max) { //Create a file selector that will select all files between min and max directory depth
		minDepth = min;
		maxDepth = max;
	}

	public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
		int depth = fileSelectInfo.getDepth();
		return depth >= minDepth && depth <= maxDepth;
	}

	public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
		return fileSelectInfo.getDepth() < maxDepth;
	}
}
