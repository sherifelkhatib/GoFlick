package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

public class ProgressStringEntity extends StringEntity {
	private final ProgressListener listener;

	public ProgressStringEntity(String s, final ProgressListener listener) throws UnsupportedEncodingException {
		super(s);
		this.listener = listener;
	}
	public ProgressStringEntity(String s, String charset, final ProgressListener listener) throws UnsupportedEncodingException {
		super(s, charset);
		this.listener = listener;
	}
	
	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}
}
