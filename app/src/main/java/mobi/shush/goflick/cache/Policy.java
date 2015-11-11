package mobi.shush.goflick.cache;

/**
 * Policy that decides on read operations done by Cacher.
 * 
 * @author Sherif elKhatib
 */
public interface Policy {
	/**
	 * Called by Cacher when a read operation is requested.
	 *            Meta of cache item
	 * @param time
	 *            Time of the request of this read operation
	 * @return {@link Result} that defines actions that should be done by
	 *         Cacher
	 */
	Result read(Meta meta, long time);

	/**
	 * Class that represents policy actions that should be done by Cacher on a
	 * Read operation
	 * 
	 * @author Sherif elKhatib
	 * 
	 */
	public interface Result {
		/**
		 * Returns the Updated meta data of a cache item <br />
		 * This will only be called if {@link #shouldUpdate()} returned true.
		 * 
		 * @return {@link Meta} instance if Meta should be changed
		 */
		/*
		 * public Meta getNewMeta();
		 */
		/**
		 * Called by cacher to decide whether this cache item should be read or
		 * not.
		 * 
		 * @return <i>true</i> if cacher should read the cache item;<i>false</i>
		 *         if not
		 */
		public boolean canRead();

		/**
		 * Called by cacher to decide whether Cacher should update the Meta of
		 * this cache or not.
		 * 
		 * @return <i>true</i> if meta should be updated by Cacher;<i>false</i>
		 *         if not
		 */
		public boolean shouldUpdate();

		/**
		 * Called by cacher to decide whether this cache item should be deleted
		 * or not.
		 * 
		 * @return <i>true</i> if cacher should delete the cache
		 *         item;<i>false</i> if not
		 */
		public boolean shouldDelete();

		public static class Builder {
			private boolean shouldUseCache = false;
			private boolean shouldUpdateMeta = true;
			private boolean shouldDeleteCache = false;

			public Builder setUseCache(boolean use) {
				this.shouldUseCache = use;
				return this;
			}

			public Builder setDeleteCache(boolean delete) {
				this.shouldDeleteCache = delete;
				return this;
			}

			public Builder setUpdateCache(boolean update) {
				this.shouldUpdateMeta = update;
				return this;
			}

			public Result build( ) {
				return new Result() {
					@Override
					public boolean shouldUpdate( ) {
						return shouldUpdateMeta;
					}

					@Override
					public boolean shouldDelete( ) {
						return shouldDeleteCache;
					}

					@Override
					public boolean canRead( ) {
						return shouldUseCache;
					}
					// @Override
					// public Meta getNewMeta() { return new Meta() {
					// @Override
					// public long getSize() { return meta.getSize(); }
					// @Override
					// public long getLastAccess() { return
					// meta.getLastAccess(); }
					// @Override
					// public long getDateCreated() { return
					// meta.getDateCreated(); }
					// };
					// }
				};
			}
		}
	}

	public static final class Factory {
		/**
		 * Policy used when you never want to cache.
		 * 
		 * @return {@link Policy}
		 */
		public static Policy NeverUseCache( ) {
			return new Policy() {
				@Override
				public Result read(Meta meta, long time) {
					return new Result.Builder().setUseCache(false).setUpdateCache(false).setDeleteCache(false).build();
				}
			};
		}

		/**
		 * Policy used when you always want to use the cache without any
		 * restriction.
		 * 
		 * @return {@link Policy}
		 */
		public static Policy AlwaysUseCache( ) {
			return new Policy() {
				@Override
				public Result read(Meta meta, long time) {
					return new Result.Builder().setUseCache(true).setUpdateCache(false).setDeleteCache(false).build();
				}
			};
		}

		/**
		 * Policy used when you want to access cache only when they are fresh
		 * enough.
		 * 
		 * @param age
		 *            How fresh do you want them to allow access (in
		 *            milliseconds)
		 * @return {@link Policy}
		 */
		public static Policy UseCacheWhenAgeLessThan(final long age) {
			return new Policy() {
				@Override
				public Result read(Meta meta, long time) {
					return new Result.Builder().setUseCache(time - meta.getDateCreated() < age).setUpdateCache(false).setDeleteCache(time - meta.getDateCreated() > age).build();
				}
			};
		}

		/**
		 * Policy used when you want to access cache only when they were
		 * recently accessed.
		 * 
		 * @param age
		 *            How recently accessed do you want them to allow access (in
		 *            milliseconds)
		 * @return {@link Policy}
		 */
		public static Policy UseCacheWhenLastAccessNotOlderThan(final long age) {
			return new Policy() {
				@Override
				public Result read(Meta meta, long time) {
					return new Result.Builder().setUseCache(time - meta.getLastAccess() < age).setUpdateCache(time - meta.getLastAccess() < age).setDeleteCache(time - meta.getLastAccess() > age).build();
				}
			};
		}

		/**
		 * Returns a Policy that ANDs 2 policies. For example, if you want a
		 * policy that allows access if age of cache is less than 10 day but
		 * only if it was accessed in the last 2 days, you can use the
		 * following: <br />
		 * <br />
		 * <code>
		 * Policy.and(
		 * <br />
		 * 			Policy.UseCacheWhenAgeLessThan(10 *24*60*60*1000), //10 days
		 * <br />
		 * 			Policy.UseCacheWhenLastAccessNotOlderThan(2 *24*60*60*1000) //2 days
		 * <br />
		 * 		);
		 * </code>
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		public static Policy and(final Policy a, final Policy b) {
			return new Policy() {
				@Override
				public Result read(final Meta meta, final long time) {
					return new Result() {
						@Override
						public boolean canRead( ) {
							return a.read(meta, time).canRead() && b.read(meta, time).canRead();
						}

						@Override
						public boolean shouldUpdate( ) {
							return a.read(meta, time).shouldUpdate() && b.read(meta, time).shouldUpdate();
						}

						@Override
						public boolean shouldDelete( ) {
							return a.read(meta, time).shouldDelete() && b.read(meta, time).shouldDelete();
						}
					};
				}
			};
		}

		/**
		 * Returns a Policy that ORs 2 policies. For example, if you want a
		 * policy that allows access if age of cache is less than 10 day or if
		 * it was accessed in the last 2 days, you can use the following: <br />
		 * <br />
		 * <code>
		 * Policy.or(
		 * <br />
		 * 			Policy.UseCacheWhenAgeLessThan(10 *24*60*60*1000), //10 days
		 * <br />
		 * 			Policy.UseCacheWhenLastAccessNotOlderThan(2 *24*60*60*1000) //2 days
		 * <br />
		 * 		);
		 * </code>
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		public static Policy or(final Policy a, final Policy b) {
			return new Policy() {
				@Override
				public Result read(final Meta meta, final long time) {
					return new Result() {
						@Override
						public boolean canRead( ) {
							return a.read(meta, time).canRead() || b.read(meta, time).canRead();
						}

						@Override
						public boolean shouldUpdate( ) {
							return a.read(meta, time).shouldUpdate() || b.read(meta, time).shouldUpdate();
						}

						@Override
						public boolean shouldDelete( ) {
							return a.read(meta, time).shouldDelete() || b.read(meta, time).shouldDelete();
						}
					};
				}
			};
		}
	}
}