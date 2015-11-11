package mobi.shush.goflick.cache;

class Resource implements Meta {
	public Resource(String resource, String location, long size, long lastAccess, long dateCreated) {
		this.resource = resource;
		this.location = location;
		this.size = size;
		this.lastaccess = lastAccess;
		this.death = dateCreated;
	}

	public Resource(String resource, String location, Meta meta) {
		this.resource = resource;
		this.location = location;
		updateMeta(meta);
	}

	public void updateMeta(Meta meta) {
		if (meta == null) return;
		this.size = meta.getSize();
		this.lastaccess = meta.getLastAccess();
		this.death = meta.getDateCreated();
	}

	private String resource;
	private String location;
	private long size;
	private long lastaccess;
	private long death;

	public String getResource( ) {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getLocation( ) {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getDateCreated( ) {
		return death;
	}

	public void setDateCreated(long date) {
		this.death = date;
	}

	public long getSize( ) {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLastAccess( ) {
		return lastaccess;
	}

	public void setLastAccess(long lastaccess) {
		this.lastaccess = lastaccess;
	}
}
