package net.ion.amazon.s3.vfs.provider;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileNameParser;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.VfsComponentContext;

public class S3FileNameParser extends AbstractFileNameParser {

	private static final S3FileNameParser instance = new S3FileNameParser();


	public static S3FileNameParser getInstance() {
		return instance;
	}

	private S3FileNameParser() {
	}

	public FileName parseUri(final VfsComponentContext context, final FileName base, final String filename) throws FileSystemException {
		StringBuilder name = new StringBuilder();

		String scheme = UriParser.extractScheme(filename, name);
		UriParser.canonicalizePath(name, 0, name.length(), this);

		// Normalize separators in the path
		UriParser.fixSeparators(name);

		// Normalise the path
		FileType fileType = UriParser.normalisePath(name);

		// Extract bucket name
		final String bucketName = UriParser.extractFirstElement(name);

		return new S3FileName(scheme, bucketName, name.toString(), fileType);
	}

}
