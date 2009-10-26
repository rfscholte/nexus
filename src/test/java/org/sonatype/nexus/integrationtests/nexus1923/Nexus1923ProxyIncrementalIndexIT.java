package org.sonatype.nexus.integrationtests.nexus1923;

import java.io.File;

import org.codehaus.plexus.util.FileUtils;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class Nexus1923ProxyIncrementalIndexIT
    extends AbstractNexus1923
{
    public Nexus1923ProxyIncrementalIndexIT()
        throws Exception
    {
        super();
    }
    
    @Test
    public void validateIncrementalIndexesDownloaded()
        throws Exception
    {
        File hostedRepoStorageDirectory = getHostedRepositoryStorageDirectory();
        
        //First create our hosted repository
        createHostedRepository();
        //And hosted repository task
        String hostedReindexId = createHostedReindexTask();
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        FileUtils.copyDirectoryStructure( getTestFile( FIRST_ARTIFACT ), 
            hostedRepoStorageDirectory );
        
        reindexHostedRepository( hostedReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        //validate that after reindex is done we have an incremental chunk in the hosted repo
        AssertJUnit.assertTrue( getHostedRepositoryIndex().exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "1" ).exists() );
        AssertJUnit.assertFalse( getHostedRepositoryIndexIncrement( "2" ).exists() );
        
        //Now create our proxy repository
        createProxyRepository();
        
        //will download the initial index because repo has download remote set to true
        TaskScheduleUtil.waitForAllTasksToStop();
        
        String proxyReindexId = createProxyReindexTask();
        
        reindexProxyRepository( proxyReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        AssertJUnit.assertTrue( getProxyRepositoryIndex().exists() );
        AssertJUnit.assertFalse( getProxyRepositoryIndexIncrement( "1" ).exists() );
        
        //Now make sure that the search is properly working
        searchForArtifactInProxyIndex( FIRST_ARTIFACT, true );
        searchForArtifactInProxyIndex( SECOND_ARTIFACT, false );
        searchForArtifactInProxyIndex( THIRD_ARTIFACT, false );
        searchForArtifactInProxyIndex( FOURTH_ARTIFACT, false );
        searchForArtifactInProxyIndex( FIFTH_ARTIFACT, false );
        
        //Now add items to hosted, and reindex to create incremental chunk
        FileUtils.copyDirectoryStructure( getTestFile( SECOND_ARTIFACT ), 
            hostedRepoStorageDirectory );
        reindexHostedRepository( hostedReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        //validate that after reindex is done we have an incremental chunk in the hosted repo
        AssertJUnit.assertTrue( getHostedRepositoryIndex().exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "1" ).exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "2" ).exists() );
        AssertJUnit.assertFalse( getHostedRepositoryIndexIncrement( "3" ).exists() );
        
        //now download via the proxy repo
        reindexProxyRepository( proxyReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        //validate that after reindex is done we have an incremental chunk of our own in the proxy repo
        AssertJUnit.assertTrue( getProxyRepositoryIndex().exists() );
        AssertJUnit.assertTrue( getProxyRepositoryIndexIncrement( "1" ).exists() );
        AssertJUnit.assertFalse( getProxyRepositoryIndexIncrement( "2" ).exists() );
        
        //Now make sure that the search is properly working
        searchForArtifactInProxyIndex( FIRST_ARTIFACT, true );
        searchForArtifactInProxyIndex( SECOND_ARTIFACT, true );
        searchForArtifactInProxyIndex( THIRD_ARTIFACT, false );
        searchForArtifactInProxyIndex( FOURTH_ARTIFACT, false );
        searchForArtifactInProxyIndex( FIFTH_ARTIFACT, false );
        
        // Now make the hosted have 3 more index chunks
        FileUtils.copyDirectoryStructure( getTestFile( THIRD_ARTIFACT ), 
            hostedRepoStorageDirectory );
        reindexHostedRepository( hostedReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        //validate that after reindex is done we have an incremental chunk in the hosted repo
        AssertJUnit.assertTrue( getHostedRepositoryIndex().exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "1" ).exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "2" ).exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "3" ).exists() );
        AssertJUnit.assertFalse( getHostedRepositoryIndexIncrement( "4" ).exists() );
        
        FileUtils.copyDirectoryStructure( getTestFile( FOURTH_ARTIFACT ), 
            hostedRepoStorageDirectory );
        reindexHostedRepository( hostedReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        //validate that after reindex is done we have an incremental chunk in the hosted repo
        AssertJUnit.assertTrue( getHostedRepositoryIndex().exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "1" ).exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "2" ).exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "3" ).exists() );
        AssertJUnit.assertTrue( getHostedRepositoryIndexIncrement( "4" ).exists() );
        AssertJUnit.assertFalse( getHostedRepositoryIndexIncrement( "5" ).exists() );
        
        reindexProxyRepository( proxyReindexId );
        
        TaskScheduleUtil.waitForAllTasksToStop();
        
        //validate that after reindex is done we have an incremental chunk of our own in the proxy repo
        //of course only 2 indexes, as these published indexes should NOT line up 1 to 1 with the hosted repo
        AssertJUnit.assertTrue( getProxyRepositoryIndex().exists() );
        AssertJUnit.assertTrue( getProxyRepositoryIndexIncrement( "1" ).exists() );
        AssertJUnit.assertTrue( getProxyRepositoryIndexIncrement( "2" ).exists() );
        AssertJUnit.assertFalse( getProxyRepositoryIndexIncrement( "3" ).exists() );
        
        //Now make sure that the search is properly working
        searchForArtifactInProxyIndex( FIRST_ARTIFACT, true );
        searchForArtifactInProxyIndex( SECOND_ARTIFACT, true );
        searchForArtifactInProxyIndex( THIRD_ARTIFACT, true );
        searchForArtifactInProxyIndex( FOURTH_ARTIFACT, true );
        searchForArtifactInProxyIndex( FIFTH_ARTIFACT, false );
    }
}
