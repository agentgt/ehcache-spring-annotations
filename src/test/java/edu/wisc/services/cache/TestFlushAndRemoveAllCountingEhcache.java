/**
 * 
 */
package edu.wisc.services.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandler;
import net.sf.ehcache.extension.CacheExtension;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.statistics.CacheUsageListener;
import net.sf.ehcache.statistics.LiveCacheStatistics;
import net.sf.ehcache.statistics.sampled.SampledCacheStatistics;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterManager;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class TestFlushAndRemoveAllCountingEhcache implements Ehcache {

	public static final String FLUSH_REMOVE_COUNT_CACHE_NAME = "flushRemoveCountingCacheName";
	private int flushCount = 0;
	private int removeAllCount = 0;
	
	/**
	 * @return the flushCount
	 */
	public int getFlushCount() {
		return flushCount;
	}

	/**
	 * @return the removeAllCount
	 */
	public int getRemoveAllCount() {
		return removeAllCount;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#bootstrap()
	 */
	@Override
	public void bootstrap() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#calculateInMemorySize()
	 */
	@Override
	public long calculateInMemorySize() throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#clearStatistics()
	 */
	@Override
	public void clearStatistics() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#disableDynamicFeatures()
	 */
	@Override
	public void disableDynamicFeatures() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#dispose()
	 */
	@Override
	public void dispose() throws IllegalStateException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#evictExpiredElements()
	 */
	@Override
	public void evictExpiredElements() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#flush()
	 */
	@Override
	public void flush() throws IllegalStateException, CacheException {
		this.flushCount++;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#get(java.io.Serializable)
	 */
	@Override
	public Element get(Serializable arg0) throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#get(java.lang.Object)
	 */
	@Override
	public Element get(Object arg0) throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getAllWithLoader(java.util.Collection, java.lang.Object)
	 */
	@Override
	public Map getAllWithLoader(Collection arg0, Object arg1)
			throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getAverageGetTime()
	 */
	@Override
	public float getAverageGetTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getBootstrapCacheLoader()
	 */
	@Override
	public BootstrapCacheLoader getBootstrapCacheLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getCacheConfiguration()
	 */
	@Override
	public CacheConfiguration getCacheConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getCacheEventNotificationService()
	 */
	@Override
	public RegisteredEventListeners getCacheEventNotificationService() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getCacheExceptionHandler()
	 */
	@Override
	public CacheExceptionHandler getCacheExceptionHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getCacheManager()
	 */
	@Override
	public CacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getDiskStoreSize()
	 */
	@Override
	public int getDiskStoreSize() throws IllegalStateException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getGuid()
	 */
	@Override
	public String getGuid() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getInternalContext()
	 */
	@Override
	public Object getInternalContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getKeys()
	 */
	@Override
	public List getKeys() throws IllegalStateException, CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getKeysNoDuplicateCheck()
	 */
	@Override
	public List getKeysNoDuplicateCheck() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getKeysWithExpiryCheck()
	 */
	@Override
	public List getKeysWithExpiryCheck() throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getLiveCacheStatistics()
	 */
	@Override
	public LiveCacheStatistics getLiveCacheStatistics()
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getMemoryStoreSize()
	 */
	@Override
	public long getMemoryStoreSize() throws IllegalStateException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getName()
	 */
	@Override
	public String getName() {
		return FLUSH_REMOVE_COUNT_CACHE_NAME;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getQuiet(java.io.Serializable)
	 */
	@Override
	public Element getQuiet(Serializable arg0) throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getQuiet(java.lang.Object)
	 */
	@Override
	public Element getQuiet(Object arg0) throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getRegisteredCacheExtensions()
	 */
	@Override
	public List<CacheExtension> getRegisteredCacheExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getRegisteredCacheLoaders()
	 */
	@Override
	public List<CacheLoader> getRegisteredCacheLoaders() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getRegisteredCacheWriter()
	 */
	@Override
	public CacheWriter getRegisteredCacheWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getSampledCacheStatistics()
	 */
	@Override
	public SampledCacheStatistics getSampledCacheStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getSize()
	 */
	@Override
	public int getSize() throws IllegalStateException, CacheException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getSizeBasedOnAccuracy(int)
	 */
	@Override
	public int getSizeBasedOnAccuracy(int arg0)
			throws IllegalArgumentException, IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getStatistics()
	 */
	@Override
	public Statistics getStatistics() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getStatisticsAccuracy()
	 */
	@Override
	public int getStatisticsAccuracy() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getStatus()
	 */
	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getWithLoader(java.lang.Object, net.sf.ehcache.loader.CacheLoader, java.lang.Object)
	 */
	@Override
	public Element getWithLoader(Object arg0, CacheLoader arg1, Object arg2)
			throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#getWriterManager()
	 */
	@Override
	public CacheWriterManager getWriterManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#initialise()
	 */
	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isClusterCoherent()
	 */
	@Override
	public boolean isClusterCoherent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isDisabled()
	 */
	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isElementInMemory(java.io.Serializable)
	 */
	@Override
	public boolean isElementInMemory(Serializable arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isElementInMemory(java.lang.Object)
	 */
	@Override
	public boolean isElementInMemory(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isElementOnDisk(java.io.Serializable)
	 */
	@Override
	public boolean isElementOnDisk(Serializable arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isElementOnDisk(java.lang.Object)
	 */
	@Override
	public boolean isElementOnDisk(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isExpired(net.sf.ehcache.Element)
	 */
	@Override
	public boolean isExpired(Element arg0) throws IllegalStateException,
			NullPointerException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isKeyInCache(java.lang.Object)
	 */
	@Override
	public boolean isKeyInCache(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isNodeCoherent()
	 */
	@Override
	public boolean isNodeCoherent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isSampledStatisticsEnabled()
	 */
	@Override
	public boolean isSampledStatisticsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isStatisticsEnabled()
	 */
	@Override
	public boolean isStatisticsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#isValueInCache(java.lang.Object)
	 */
	@Override
	public boolean isValueInCache(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#load(java.lang.Object)
	 */
	@Override
	public void load(Object arg0) throws CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#loadAll(java.util.Collection, java.lang.Object)
	 */
	@Override
	public void loadAll(Collection arg0, Object arg1) throws CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#put(net.sf.ehcache.Element)
	 */
	@Override
	public void put(Element arg0) throws IllegalArgumentException,
			IllegalStateException, CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#put(net.sf.ehcache.Element, boolean)
	 */
	@Override
	public void put(Element arg0, boolean arg1)
			throws IllegalArgumentException, IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#putQuiet(net.sf.ehcache.Element)
	 */
	@Override
	public void putQuiet(Element arg0) throws IllegalArgumentException,
			IllegalStateException, CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#putWithWriter(net.sf.ehcache.Element)
	 */
	@Override
	public void putWithWriter(Element arg0) throws IllegalArgumentException,
			IllegalStateException, CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#registerCacheExtension(net.sf.ehcache.extension.CacheExtension)
	 */
	@Override
	public void registerCacheExtension(CacheExtension arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#registerCacheLoader(net.sf.ehcache.loader.CacheLoader)
	 */
	@Override
	public void registerCacheLoader(CacheLoader arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#registerCacheUsageListener(net.sf.ehcache.statistics.CacheUsageListener)
	 */
	@Override
	public void registerCacheUsageListener(CacheUsageListener arg0)
			throws IllegalStateException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#registerCacheWriter(net.sf.ehcache.writer.CacheWriter)
	 */
	@Override
	public void registerCacheWriter(CacheWriter arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#remove(java.io.Serializable)
	 */
	@Override
	public boolean remove(Serializable arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#remove(java.io.Serializable, boolean)
	 */
	@Override
	public boolean remove(Serializable arg0, boolean arg1)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#remove(java.lang.Object, boolean)
	 */
	@Override
	public boolean remove(Object arg0, boolean arg1)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#removeAll()
	 */
	@Override
	public void removeAll() throws IllegalStateException, CacheException {
		this.removeAllCount++;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#removeAll(boolean)
	 */
	@Override
	public void removeAll(boolean arg0) throws IllegalStateException,
			CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#removeCacheUsageListener(net.sf.ehcache.statistics.CacheUsageListener)
	 */
	@Override
	public void removeCacheUsageListener(CacheUsageListener arg0)
			throws IllegalStateException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#removeQuiet(java.io.Serializable)
	 */
	@Override
	public boolean removeQuiet(Serializable arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#removeQuiet(java.lang.Object)
	 */
	@Override
	public boolean removeQuiet(Object arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#removeWithWriter(java.lang.Object)
	 */
	@Override
	public boolean removeWithWriter(Object arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setBootstrapCacheLoader(net.sf.ehcache.bootstrap.BootstrapCacheLoader)
	 */
	@Override
	public void setBootstrapCacheLoader(BootstrapCacheLoader arg0)
			throws CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setCacheExceptionHandler(net.sf.ehcache.exceptionhandler.CacheExceptionHandler)
	 */
	@Override
	public void setCacheExceptionHandler(CacheExceptionHandler arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setCacheManager(net.sf.ehcache.CacheManager)
	 */
	@Override
	public void setCacheManager(CacheManager arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setDisabled(boolean)
	 */
	@Override
	public void setDisabled(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setDiskStorePath(java.lang.String)
	 */
	@Override
	public void setDiskStorePath(String arg0) throws CacheException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setName(java.lang.String)
	 */
	@Override
	public void setName(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setNodeCoherent(boolean)
	 */
	@Override
	public void setNodeCoherent(boolean arg0)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setSampledStatisticsEnabled(boolean)
	 */
	@Override
	public void setSampledStatisticsEnabled(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setStatisticsAccuracy(int)
	 */
	@Override
	public void setStatisticsAccuracy(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setStatisticsEnabled(boolean)
	 */
	@Override
	public void setStatisticsEnabled(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#setTransactionManagerLookup(net.sf.ehcache.transaction.manager.TransactionManagerLookup)
	 */
	@Override
	public void setTransactionManagerLookup(TransactionManagerLookup arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#unregisterCacheExtension(net.sf.ehcache.extension.CacheExtension)
	 */
	@Override
	public void unregisterCacheExtension(CacheExtension arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#unregisterCacheLoader(net.sf.ehcache.loader.CacheLoader)
	 */
	@Override
	public void unregisterCacheLoader(CacheLoader arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#unregisterCacheWriter()
	 */
	@Override
	public void unregisterCacheWriter() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.Ehcache#waitUntilClusterCoherent()
	 */
	@Override
	public void waitUntilClusterCoherent() throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	

}
