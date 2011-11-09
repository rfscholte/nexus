/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.proxy.storage.local.fs.perf;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.sonatype.nexus.configuration.application.ApplicationConfiguration;
import org.sonatype.nexus.mime.MimeUtil;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.access.AccessManager;
import org.sonatype.nexus.proxy.attributes.AttributeStorage;
import org.sonatype.nexus.proxy.attributes.DefaultAttributesHandler;
import org.sonatype.nexus.proxy.attributes.DefaultFSAttributeStorage;
import org.sonatype.nexus.proxy.attributes.StorageFileItemInspector;
import org.sonatype.nexus.proxy.attributes.StorageItemInspector;
import org.sonatype.nexus.proxy.attributes.perf.internal.MockRepository;
import org.sonatype.nexus.proxy.item.AbstractStorageItem;
import org.sonatype.nexus.proxy.item.DummyRepositoryItemUidFactory;
import org.sonatype.nexus.proxy.item.LinkPersister;
import org.sonatype.nexus.proxy.item.uid.Attribute;
import org.sonatype.nexus.proxy.item.uid.IsMetadataMaintainedAttribute;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSPeer;
import org.sonatype.nexus.proxy.storage.local.fs.FSPeer;
import org.sonatype.nexus.proxy.wastebasket.Wastebasket;
import org.sonatype.plexus.appevents.ApplicationEventMulticaster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@BenchmarkHistoryChart()
@BenchmarkMethodChart()
@AxisRange(min = 0)
public class DefaultFSLocalRepositoryStoragePerformanceTest
{
    @Rule
    public MethodRule benchmarkRun = new BenchmarkRule();
    
    private ApplicationConfiguration applicationConfiguration;

    final private String testFilePath = "content/file.txt";

    private DefaultFSLocalRepositoryStorage localRepositoryStorageUnderTest;

    private Repository repository;

    private long originalLastAccessTime;

    @Before
    public void setup() throws Exception
    {
        File repoStorageDir = new File( "target/"+ getClass().getSimpleName() + "/repo-storage/" );
        String repoLocalURL = repoStorageDir.getAbsoluteFile().toURI().toString();

        // write a test file
        File testFile = new File( repoStorageDir, testFilePath );
        FileUtils.writeStringToFile( testFile, "CONTENT" );

         // Mocks
        Wastebasket wastebasket = mock( Wastebasket.class );
        LinkPersister linkPersister = mock( LinkPersister.class );
        MimeUtil mimeUtil = mock( MimeUtil.class );
        Map<String, Long> repositoryContexts = Maps.newHashMap();

        // Application Config
        applicationConfiguration = mock( ApplicationConfiguration.class );
        when( applicationConfiguration.getWorkingDirectory( eq("proxy/attributes")) ).thenReturn( new File( "target/"+ this.getClass().getSimpleName() +"/attributes" ) );

        // remove any event inspectors from the timing
        List<StorageItemInspector> itemInspectorList = new ArrayList<StorageItemInspector>();
        List<StorageFileItemInspector> fileItemInspectorList = new ArrayList<StorageFileItemInspector>();

        // set the AttributeStorage on the Attribute Handler
        DefaultAttributesHandler attributesHandler = new DefaultAttributesHandler(applicationConfiguration, getAttributeStorage(), itemInspectorList, fileItemInspectorList );

        // Need to use the MockRepository
        repository = new MockRepository( "testRetieveItem-repo", new DummyRepositoryItemUidFactory() );
        repository.setLocalUrl( repoLocalURL );
        repository.setAttributesHandler( attributesHandler );

        // setup the class under test
        localRepositoryStorageUnderTest = new DefaultFSLocalRepositoryStorage( wastebasket, linkPersister, mimeUtil, repositoryContexts, new DefaultFSPeer() );

        // prime the retrieve
        ResourceStoreRequest resourceRequest = new ResourceStoreRequest( testFilePath );
        originalLastAccessTime = localRepositoryStorageUnderTest.retrieveItem( repository, resourceRequest ).getLastRequested();
    }

    private AttributeStorage getAttributeStorage()
    {
        // Mock out the events
        ApplicationEventMulticaster applicationEventMulticaster = mock( ApplicationEventMulticaster.class );
        
        DefaultFSAttributeStorage attributeStorage =  new DefaultFSAttributeStorage( applicationEventMulticaster, applicationConfiguration );
        attributeStorage.initializeWorkingDirectory();

        return attributeStorage;
    }

    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 1 )
    @Test
    public void testRetieveItemWithLastAccessUpdate()
        throws LocalStorageException, ItemNotFoundException
    {
        ResourceStoreRequest resourceRequest = new ResourceStoreRequest( testFilePath );
        resourceRequest.getRequestContext().put( AccessManager.REQUEST_REMOTE_ADDRESS, "127.0.0.1" );

        AbstractStorageItem storageItem = localRepositoryStorageUnderTest.retrieveItem( repository, resourceRequest );

        MatcherAssert.assertThat( storageItem, Matchers.notNullValue() );
        MatcherAssert.assertThat( storageItem.getLastRequested(), Matchers.greaterThan( originalLastAccessTime ) );
    }

    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 1)
    @Test
    public void testRetieveItemWithoutLastAccessUpdate()
        throws LocalStorageException, ItemNotFoundException
    {
        ResourceStoreRequest resourceRequest = new ResourceStoreRequest( testFilePath );

        AbstractStorageItem storageItem = localRepositoryStorageUnderTest.retrieveItem( repository, resourceRequest );

        MatcherAssert.assertThat( storageItem, Matchers.notNullValue() );
        MatcherAssert.assertThat( storageItem.getLastRequested(), Matchers.equalTo( originalLastAccessTime ) );
    }
}