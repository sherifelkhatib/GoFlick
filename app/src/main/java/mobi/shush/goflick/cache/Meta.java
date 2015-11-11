package mobi.shush.goflick.cache;

/**
 * Interface provided by Cacher to policies providing them with meta data of
 * cache items to aid their decision.
 * 
 * @author Sherif elKhatib
 */
public interface Meta {
	/**
	 * @return the date on which this cache item was created
	 */
	public long getDateCreated();

	/**
	 * @return the size of this cache item
	 */
	public long getSize();

	/**
	 * @return the date on which this cache item was last accessed
	 */
	public long getLastAccess();

	public static class Builder {
		private long dateCreated;
		private long size;
		private long lastAccess;

		public Builder(Meta m) {
			this.dateCreated = m.getDateCreated();
			this.size = m.getSize();
			this.lastAccess = m.getLastAccess();
		}

		public Builder setDateCreated(long date) {
			this.dateCreated = date;
			return this;
		}

		public Builder setSize(long size) {
			this.size = size;
			return this;
		}

		public Builder setLastAccess(long date) {
			this.lastAccess = date;
			return this;
		}

		public Meta build( ) {
			return new Meta() {
				@Override
				public long getSize( ) {
					return size;
				}

				@Override
				public long getLastAccess( ) {
					return lastAccess;
				}

				@Override
				public long getDateCreated( ) {
					return dateCreated;
				}
			};
		}
	}
}