package org.apache.http.entity.mime;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.FileEntity;

public class ProgressFileEntity extends FileEntity {
	private final ProgressListener listener;

	public ProgressFileEntity(File file, String contentType, final ProgressListener listener) throws UnsupportedEncodingException {
		super(file, contentType);
		this.listener = listener;
	}
	
	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

}
