package net.ion.amazon.s3.vfs.provider;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;

public class S3FileName extends AbstractFileName {

	private final String s3BucketId;

	protected S3FileName(final String scheme, final String bucketId, final String path, final FileType type) {
		super(scheme, path, type);
		this.s3BucketId = bucketId;
	}

	public String getBucketId() {
		return s3BucketId;
	}

	@Override
	public FileName createName(String absPath, FileType type) {
		return new S3FileName(getScheme(), s3BucketId, absPath, type);
	}

	@Override
	protected void appendRootUri(StringBuilder buffer, boolean addPassword) {
		buffer.append(getScheme());
		buffer.append("://");
		buffer.append(s3BucketId);
	}
}
