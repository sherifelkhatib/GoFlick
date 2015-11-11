package org.apache.http.entity.mime;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends FilterOutputStream
{

	private final ProgressListener listener;
	private long transferred;

	public CountingOutputStream(final OutputStream out, final ProgressListener listener)
	{
		super(out);
		this.listener = listener;
		this.transferred = 0;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		out.write(b, off, len);
		this.transferred += len;
		this.listener.transferred(this.transferred);
	}

	@Override
	public void write(int b) throws IOException
	{
		out.write(b);
		this.transferred++;
		this.listener.transferred(this.transferred);
	}
}