package org.apache.http.entity.mime;


public interface ProgressListener
{
	void transferred(long num);
}