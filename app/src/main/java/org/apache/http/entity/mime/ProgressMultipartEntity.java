package org.apache.http.entity.mime;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
 
public class ProgressMultipartEntity extends MultipartEntity
{
 
	private final ProgressListener listener;
 
	public ProgressMultipartEntity(final ProgressListener listener)
	{
		super();
		this.listener = listener;
	}
 
	public ProgressMultipartEntity(final HttpMultipartMode mode, final ProgressListener listener)
	{
		super(mode);
		this.listener = listener;
	}
 
	public ProgressMultipartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener)
	{
		super(mode, boundary, charset);
		this.listener = listener;
	}
 
	@Override
	public void writeTo(final OutputStream outstream) throws IOException
	{
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}
}