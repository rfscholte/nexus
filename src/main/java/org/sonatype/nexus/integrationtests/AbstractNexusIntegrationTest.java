/**

 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General protected License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General protected License Version 3 for more details.
 * You should have received a copy of the GNU General protected License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.integrationtests;

import static org.testng.AssertJUnit.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.proxy.registry.RepositoryTypeRegistry;
import org.sonatype.nexus.rest.model.GlobalConfigurationResource;
import org.sonatype.nexus.test.launcher.ForkedNexusInstancesFactory;
import org.sonatype.nexus.test.launcher.INexusInstancesFactory;
import org.sonatype.nexus.test.launcher.NexusContext;
import org.sonatype.nexus.test.utils.DeployUtils;
import org.sonatype.nexus.test.utils.FileTestingUtils;
import org.sonatype.nexus.test.utils.GavUtil;
import org.sonatype.nexus.test.utils.MavenProjectFileFilter;
import org.sonatype.nexus.test.utils.NexusConfigUtil;
import org.sonatype.nexus.test.utils.PortUtil;
import org.sonatype.nexus.test.utils.SettingsMessageUtil;
import org.sonatype.nexus.test.utils.TestProperties;
import org.sonatype.nexus.test.utils.XStreamFactory;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.thoughtworks.xstream.XStream;

/**
 * curl --user admin:admin123 --request PUT http://localhost:8081/nexus/service/local/status/command --data START NOTE,
 * this class is not really abstract so I can work around a the <code>@BeforeClass</code>, <code>@AfterClass</code>
 * issues, this should be refactored a little, but it might be ok, if we switch to TestNg
 */
// @Test( sequential = true, timeOut = 120000 )
public class AbstractNexusIntegrationTest
{

    protected static final String REPO_TEST_HARNESS_REPO = "nexus-test-harness-repo";

    protected static final String REPO_TEST_HARNESS_REPO2 = "nexus-test-harness-repo2";

    protected static final String REPO_TEST_HARNESS_RELEASE_REPO = "nexus-test-harness-release-repo";

    protected static final String REPO_TEST_HARNESS_SNAPSHOT_REPO = "nexus-test-harness-snapshot-repo";

    protected static final String REPO_RELEASE_PROXY_REPO1 = "release-proxy-repo-1";

    protected static final String REPO_TEST_HARNESS_SHADOW = "nexus-test-harness-shadow";

    protected PlexusContainer container;

    protected static PlexusContainer staticContainer;

    protected static final String REPOSITORY_RELATIVE_URL = "content/repositories/";

    protected static final String GROUP_REPOSITORY_RELATIVE_URL = "content/groups/";

    protected static Logger log = Logger.getLogger( AbstractNexusIntegrationTest.class );

    protected static final String RELATIVE_CONF_DIR = "runtime/apps/nexus/conf";

    protected String testRepositoryId;

    protected static final String nexusBaseDir;

    @Deprecated
    protected String baseNexusUrl;

    protected String nexusBaseUrl;

    protected String nexusWorkDir;

    protected String WORK_CONF_DIR;

    protected int nexusApplicationPort;

    /**
     * Flag that says if we should verify the config before startup, we do not want to do this for upgrade tests.
     */
    private boolean verifyNexusConfigBeforeStart = true;

    protected File nexusLog;

    private NexusContext context;

    private static boolean INVALID_STATE;

    private static final File testResourcesFolder;

    private static PlexusContainer globalContainer;

    private static INexusInstancesFactory factory;

    static
    {
        testResourcesFolder = new File( TestProperties.getString( "test.resources.folder" ) );
        nexusBaseDir = TestProperties.getString( "nexus.base.dir" );
    }

    protected AbstractNexusIntegrationTest()
    {
        this( "nexus-test-harness-repo" );
    }

    protected AbstractNexusIntegrationTest( String testRepositoryId )
    {
        // we also need to setup a couple fields, that need to be pulled out of a bundle
        this.testRepositoryId = testRepositoryId;
        // this.nexusTestRepoUrl = baseNexusUrl + REPOSITORY_RELATIVE_URL + testRepositoryId + "/";

        // configure the logging
        SLF4JBridgeHandler.install();
    }

    @BeforeSuite
    public static final void setupNexusFactory()
    {
        globalContainer = setupContainer( BeforeSuite.class );
        // must be able to pick another factories
        factory = new ForkedNexusInstancesFactory( globalContainer );
    }

    @AfterSuite
    public static final void tearDownNexusFactory()
        throws Exception
    {
        factory.shutdown();
        factory = null;
        globalContainer.dispose();
        globalContainer = null;
    }

    public boolean isSecureTest()
    {
        return false;
    }

    /**
     * To me this seems like a bad hack around this problem. I don't have any other thoughts though. <BR/>
     * If you see this and think: "Wow, why did he to that instead of XYZ, please let me know." <BR/>
     * The issue is that we want to init the tests once (to start/stop the app) and the <code>@BeforeClass</code> is
     * static, so we don't have access to the package name of the running tests. We are going to use the package name to
     * find resources for additional setup. NOTE: With this setup running multiple Test at the same time is not
     * possible.
     * 
     * @throws Throwable
     */
    @BeforeClass
    public final void createTestEnv()
        throws Throwable
    {
        if ( INVALID_STATE )
        {
            throw new SkipException( "Nexus entered into an invalid state" );
        }

        boolean isSecureTest = isSecureTest();

        try
        {
            TestProperties.setRuntimeProperties( createRuntimeProperties() );
            startExtraServices();

            TestContext testContext = TestContainer.getInstance().getTestContext();
            testContext.setThreadContext( nexusApplicationPort, nexusWorkDir, nexusBaseUrl );

            this.container = setupContainer( getClass() );

            copyTestResources();

            this.copyConfigFiles();

            context = factory.createInstance( nexusApplicationPort, new File( nexusWorkDir ) );

            // set security state
            testContext.useAdminForRequests();
            testContext.setSecureTest( true );
            GlobalConfigurationResource set = SettingsMessageUtil.getCurrentSettings();
            // check if security is as expected
            if ( set.isSecurityEnabled() != isSecureTest )
            {
                set.setSecurityEnabled( isSecureTest );
                SettingsMessageUtil.save( set );
            }
            testContext.setSecureTest( isSecureTest );

            // tell the console what we are doing, now that there is no output its
            log.info( "Running Test: " + this.getClass().getSimpleName() );

            HashMap<String, String> variables = new HashMap<String, String>();
            variables.put( "test-harness-id", this.getTestId() );

            // we need to make sure the config is valid, so we don't need to hunt through log files
            if ( this.verifyNexusConfigBeforeStart )
            {
                NexusConfigUtil.validateConfig();
            }

            // deploy artifacts
            this.deployArtifacts();

            runOnce();
        }
        catch ( Throwable e )
        {
            factory.destroyInstance( context );
            throw e;
        }
    }

    protected void startExtraServices()
        throws Exception
    {
        // to be overwritten
    }

    protected Integer emailServerPort;

    protected Integer proxyServerPort;

    protected Integer webProxyPort;

    protected String proxyBaseURL;

    protected final Properties createRuntimeProperties()
        throws Exception
    {
        nexusWorkDir = TestProperties.getPath( "nexus.work.dir" ) + "-" + getClass().getSimpleName();
        WORK_CONF_DIR = nexusWorkDir + "/conf";
        nexusLog = new File( nexusWorkDir, "nexus.log" );
        nexusApplicationPort = PortUtil.getRandomPort();
        nexusBaseUrl = "http://localhost:" + nexusApplicationPort + "/nexus/";
        baseNexusUrl = nexusBaseUrl;
        emailServerPort = PortUtil.getRandomPort();
        proxyServerPort = PortUtil.getRandomPort();
        webProxyPort = PortUtil.getRandomPort();
        proxyBaseURL = "http://localhost:" + proxyServerPort + "/remote/";

        Properties properties = new Properties();
        properties.put( "nexus.application.port", String.valueOf( nexusApplicationPort ) );
        properties.put( "nexus-application-port", String.valueOf( nexusApplicationPort ) );
        properties.put( "nexus.base.url", nexusBaseUrl );
        properties.put( "nexus-base-url", nexusBaseUrl );
        properties.put( "application-conf", nexusWorkDir + "/conf" );
        properties.put( "nexus.work.dir", nexusWorkDir );
        properties.put( "nexus-work-dir", nexusWorkDir );
        properties.put( "nexus-work", nexusWorkDir );
        properties.put( "security-xml-file", nexusWorkDir + "/conf/security.xml" );
        properties.put( "email-server-port", emailServerPort.toString() );
        properties.setProperty( "nexus-proxy-port", proxyServerPort.toString() );
        properties.setProperty( "proxy.server.port", proxyServerPort.toString() );
        properties.setProperty( "proxy-repo-port", proxyServerPort.toString() );

        properties.setProperty( "proxy.repo.base.url", proxyBaseURL );
        properties.setProperty( "proxy-repo-base-url", proxyBaseURL );
        properties.setProperty( "webproxy.server.port", webProxyPort.toString() );
        properties.setProperty( "webproxy-server-port", webProxyPort.toString() );

        return properties;

    }

    @AfterClass( alwaysRun = true )
    public void destroyTestEnv()
        throws Exception
    {
        TestContainer.getInstance().getTestContext().setThreadContext( null, null, null );

        factory.destroyInstance( context );

        if ( staticContainer != null )
        {
            staticContainer.dispose();
        }
        staticContainer = null;
    }

    protected void copyTestResources()
        throws IOException
    {
        File source = new File( TestProperties.getString( "test.resources.source.folder" ), getTestId() );
        if ( !source.exists() )
        {
            return;
        }

        File destination = new File( testResourcesFolder, getTestId() );

        FileTestingUtils.interpolationDirectoryCopy( source, destination, TestProperties.getAll() );
    }

    protected void copyConfigFiles()
        throws IOException
    {
        Map<String, String> allProps = TestProperties.getAll();
        this.copyConfigFile( "nexus.xml", allProps, WORK_CONF_DIR );

        // copy security config
        this.copyConfigFile( "security.xml", allProps, WORK_CONF_DIR );
        this.copyConfigFile( "security-configuration.xml", allProps, WORK_CONF_DIR );

        this.copyConfigFile( "log4j.properties", allProps, WORK_CONF_DIR );

        this.copyConfigFile( "settings.xml", allProps,
                             new File( testResourcesFolder, getTestId() + "/test-config" ).getAbsolutePath() );
    }

    protected void runOnce()
        throws Exception
    {
        // must override
    }

    /**
     * @deprecated this is no longer necessary, each test does have its own workdir
     * @throws Exception
     */
    @Deprecated
    protected static void cleanWorkDir()
        throws Exception
    {
        // // must wait for all tasks, some do file locking
        // TaskScheduleUtil.waitForAllTasksToStop();
        //
        // Assert.assertTrue( NexusStatusUtil.isNexusStopped() );
        //
        // final File workDir = new File( AbstractNexusIntegrationTest.nexusWorkDir );
        //
        // // to make sure I don't delete all my MP3's and pictures, or totally screw anyone.
        // // check for 'target' and not allow any '..'
        // if ( workDir.getAbsolutePath().lastIndexOf( "target" ) != -1
        // && workDir.getAbsolutePath().lastIndexOf( ".." ) == -1 )
        // {
        // // we cannot delete the plugin-repository or the tests will fail
        //
        // File[] filesToDelete = workDir.listFiles( new FilenameFilter()
        // {
        // protected boolean accept( File dir, String name )
        // {
        // // anything but the plugin-repository directory
        // return ( !name.contains( "plugin-repository" ) );
        // }
        // } );
        //
        // if ( filesToDelete != null )
        // {
        // for ( File fileToDelete : filesToDelete )
        // {
        // // delete work dir
        // if ( fileToDelete != null )
        // {
        // FileUtils.deleteDirectory( fileToDelete );
        // }
        // }
        // }
        //
        // }
    }

    protected void deployArtifacts()
        throws Exception
    {
        TestContainer.getInstance().getTestContext().useAdminForRequests();

        // test the test directory
        File projectsDir = this.getTestResourceAsFile( "projects" );
        log.debug( "projectsDir: " + projectsDir );

        // if null there is nothing to deploy...
        if ( projectsDir != null )
        {

            // we have the parent dir, for each child (one level) we need to grab the pom.xml out of it and parse it,
            // and then deploy the artifact, sounds like fun, right!

            File[] projectFolders = projectsDir.listFiles( MavenProjectFileFilter.INSTANCE );

            for ( int ii = 0; ii < projectFolders.length; ii++ )
            {
                File project = projectFolders[ii];

                // we already check if the pom.xml was in here.
                File pom = new File( project, "pom.xml" );

                MavenXpp3Reader reader = new MavenXpp3Reader();
                FileInputStream fis = new FileInputStream( pom );
                Model model = reader.read( new FileReader( pom ) );
                fis.close();

                // a helpful note so you don't need to dig into the code to much.
                if ( model.getDistributionManagement() == null
                    || model.getDistributionManagement().getRepository() == null )
                {
                    AssertJUnit.fail( "The test artifact is either missing or has an invalid Distribution Management section." );
                }
                String deployUrl = model.getDistributionManagement().getRepository().getUrl();

                // FIXME, this needs to be fluffed up a little, should add the classifier, etc.
                String artifactFileName = model.getArtifactId() + "." + model.getPackaging();
                File artifactFile = new File( project, artifactFileName );

                log.debug( "wow, this is working: " + artifactFile.getName() );

                Gav gav =
                    new Gav( model.getGroupId(), model.getArtifactId(), model.getVersion(), null, model.getPackaging(),
                             0, new Date().getTime(), model.getName(), false, false, null, false, null );

                // the Restlet Client does not support multipart forms:
                // http://restlet.tigris.org/issues/show_bug.cgi?id=71

                // int status = DeployUtils.deployUsingPomWithRest( deployUrl, repositoryId, gav, artifactFile, pom );

                if ( !artifactFile.isFile() )
                {
                    throw new FileNotFoundException( "File " + artifactFile.getAbsolutePath() + " doesn't exists!" );
                }

                File artifactSha1 = new File( artifactFile.getAbsolutePath() + ".sha1" );
                File artifactMd5 = new File( artifactFile.getAbsolutePath() + ".md5" );
                File artifactAsc = new File( artifactFile.getAbsolutePath() + ".asc" );

                File pomSha1 = new File( pom.getAbsolutePath() + ".sha1" );
                File pomMd5 = new File( pom.getAbsolutePath() + ".md5" );
                File pomAsc = new File( pom.getAbsolutePath() + ".asc" );

                try
                {
                    if ( artifactSha1.exists() )
                    {
                        DeployUtils.deployWithWagon( this.container, "http", deployUrl, artifactSha1,
                                                     this.getRelitiveArtifactPath( gav ) + ".sha1" );
                    }
                    if ( artifactMd5.exists() )
                    {
                        DeployUtils.deployWithWagon( this.container, "http", deployUrl, artifactMd5,
                                                     this.getRelitiveArtifactPath( gav ) + ".md5" );
                    }
                    if ( artifactAsc.exists() )
                    {
                        DeployUtils.deployWithWagon( this.container, "http", deployUrl, artifactAsc,
                                                     this.getRelitiveArtifactPath( gav ) + ".asc" );
                    }

                    DeployUtils.deployWithWagon( this.container, "http", deployUrl, artifactFile,
                                                 this.getRelitiveArtifactPath( gav ) );

                    if ( pomSha1.exists() )
                    {
                        DeployUtils.deployWithWagon( this.container, "http", deployUrl, pomSha1,
                                                     this.getRelitivePomPath( gav ) + ".sha1" );
                    }
                    if ( pomMd5.exists() )
                    {
                        DeployUtils.deployWithWagon( this.container, "http", deployUrl, pomMd5,
                                                     this.getRelitivePomPath( gav ) + ".md5" );
                    }
                    if ( pomAsc.exists() )
                    {
                        DeployUtils.deployWithWagon( this.container, "http", deployUrl, pomAsc,
                                                     this.getRelitivePomPath( gav ) + ".asc" );
                    }

                    DeployUtils.deployWithWagon( this.container, "http", deployUrl, pom, this.getRelitivePomPath( gav ) );
                }
                catch ( Exception e )
                {
                    log.error( getTestId() + " Unable to deploy " + artifactFileName, e );
                    throw e;
                }
            }
        }
    }

    @AfterMethod
    public void afterTest()
        throws Exception
    {
        // reset this for each test
        TestContainer.getInstance().getTestContext().useAdminForRequests();
    }

    protected File getOverridableFile( String file )
    {
        // the test can override the test config.
        File testConfigFile = this.getTestResourceAsFile( "test-config/" + file );

        // if the tests doesn't have a different config then use the default.
        // we need to replace every time to make sure no one changes it.
        if ( testConfigFile == null || !testConfigFile.exists() )
        {
            testConfigFile = getResource( "default-configs/" + file );
        }
        else
        {
            log.debug( "This test is using its own " + file + " " + testConfigFile );
        }
        return testConfigFile;
    }

    protected void copyConfigFile( String configFile, String destShortName, Map<String, String> variables, String path )
        throws IOException
    {
        // the test can override the test config.
        File testConfigFile = this.getOverridableFile( configFile );

        File parent = new File( path );
        if ( !parent.isAbsolute() )
        {
            parent = new File( nexusBaseDir, path == null ? RELATIVE_CONF_DIR : path );
        }

        File destFile = new File( parent, destShortName );
        log.debug( "copying " + configFile + " to:  " + destFile );

        FileTestingUtils.interpolationFileCopy( testConfigFile, destFile, variables );

    }

    // Overloaded helpers

    protected void copyConfigFile( String configFile, String path )
        throws IOException
    {
        this.copyConfigFile( configFile, new HashMap<String, String>(), path );
    }

    protected void copyConfigFile( String configFile, Map<String, String> variables, String path )
        throws IOException
    {
        this.copyConfigFile( configFile, configFile, variables, path );

    }

    /**
     * Returns a File if it exists, null otherwise. Files returned by this method must be located in the
     * "src/test/resourcs/nexusXXX/" folder.
     * 
     * @param relativePath path relative to the nexusXXX directory.
     * @return A file specified by the relativePath. or null if it does not exist.
     */
    protected File getTestResourceAsFile( String relativePath )
    {
        String resource = this.getTestId() + "/" + relativePath;
        return getResource( resource );
    }

    protected String getTestId()
    {
        String packageName = this.getClass().getPackage().getName();
        return packageName.substring( packageName.lastIndexOf( '.' ) + 1, packageName.length() );
    }

    /**
     * Returns a File if it exists, null otherwise. Files returned by this method must be located in the
     * "src/test/resourcs/nexusXXX/files/" folder.
     * 
     * @param relativePath path relative to the files directory.
     * @return A file specified by the relativePath. or null if it does not exist.
     */
    protected File getTestFile( String relativePath )
    {
        return this.getTestResourceAsFile( "files/" + relativePath );
    }

    protected static File getResource( String resource )
    {
        log.debug( "Looking for resource: " + resource );
        // URL classURL = Thread.currentThread().getContextClassLoader().getResource( resource );

        File file = new File( testResourcesFolder, resource );

        if ( !file.exists() )
        {
            return null;
        }

        log.debug( "found: " + file );

        return file;
    }

    @AfterClass
    public void killContainer()
        throws Exception
    {
        if ( container != null )
        {
            container.dispose();
        }
        container = null;
    }

    // profiling with yourkit, activate using -P youtkit-profile
    private static Object profiler;

    @BeforeClass
    public final static void startProfiler()
    {
        Class<?> controllerClazz;
        try
        {
            controllerClazz = Class.forName( "com.yourkit.api.Controller" );
        }
        catch ( Exception e )
        {
            log.info( "Profiler not present" );
            return;
        }

        try
        {
            profiler = controllerClazz.newInstance();
            controllerClazz.getMethod( "captureMemorySnapshot" ).invoke( profiler );
        }
        catch ( Exception e )
        {
            Assert.fail( "Profiler was active, but failed", e );
        }
    }

    @AfterClass
    public static void takeSnapshot()
    {
        if ( profiler != null )
        {
            try
            {
                profiler.getClass().getMethod( "forceGC" ).invoke( profiler );
                profiler.getClass().getMethod( "captureMemorySnapshot" ).invoke( profiler );
            }
            catch ( Exception e )
            {
                fail( "Profiler was active, but failed due: " + e.getMessage() );
            }
        }
    }

    protected static PlexusContainer setupContainer( Class<?> baseClass )
    {
        // ----------------------------------------------------------------------------
        // Context Setup
        // ----------------------------------------------------------------------------

        Map<Object, Object> context = new HashMap<Object, Object>();

        context.put( "basedir", getBasedir() );
        context.putAll( TestProperties.getAll() );

        boolean hasPlexusHome = context.containsKey( "plexus.home" );

        if ( !hasPlexusHome )
        {
            File f = new File( getBasedir(), "target/plexus-home" );

            if ( !f.isDirectory() )
            {
                f.mkdir();
            }

            context.put( "plexus.home", f.getAbsolutePath() );
        }

        // ----------------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------------

        ContainerConfiguration containerConfiguration =
            new DefaultContainerConfiguration().setName( "test" ).setContext( context ).setContainerConfiguration(
                                                                                                                   baseClass.getName().replace(
                                                                                                                                                '.',
                                                                                                                                                '/' )
                                                                                                                       + ".xml" );

        try
        {
            return new DefaultPlexusContainer( containerConfiguration );
        }
        catch ( PlexusContainerException e )
        {
            e.printStackTrace();
            fail( "Failed to create plexus container." );
            return null;
        }
    }

    protected static String getBasedir()
    {
        String basedir = System.getProperty( "basedir" );

        if ( basedir == null )
        {
            basedir = new File( "" ).getAbsolutePath();
        }

        return basedir;
    }

    protected Object lookup( String role )
        throws Exception
    {
        return container.lookup( role );
    }

    protected Object lookup( String role, String hint )
        throws Exception
    {
        return container.lookup( role, hint );
    }

    protected <E> E lookup( Class<E> role )
        throws Exception
    {
        return container.lookup( role );
    }

    protected <E> E lookup( Class<E> role, String hint )
        throws Exception
    {
        return container.lookup( role, hint );
    }

    protected String getRelitivePomPath( Gav gav )
        throws FileNotFoundException
    {
        return GavUtil.getRelitivePomPath( gav );
    }

    protected String getRelitiveArtifactPath( Gav gav )
        throws FileNotFoundException
    {
        return GavUtil.getRelitiveArtifactPath( gav );
    }

    protected String getRelitiveArtifactPath( String groupId, String artifactId, String version, String extension,
                                              String classifier )
        throws FileNotFoundException
    {
        return GavUtil.getRelitiveArtifactPath( groupId, artifactId, version, extension, classifier );
    }

    protected File downloadSnapshotArtifact( String repository, Gav gav, File parentDir )
        throws IOException
    {
        // @see http://issues.sonatype.org/browse/NEXUS-599
        // r=<repoId> -- mandatory
        // g=<groupId> -- mandatory
        // a=<artifactId> -- mandatory
        // v=<version> -- mandatory
        // c=<classifier> -- optional
        // p=<packaging> -- optional, jar is taken as default
        // http://localhost:8087/nexus/service/local/artifact/maven/redirect?r=tasks-snapshot-repo&g=nexus&a=artifact&
        // v=1.0-SNAPSHOT
        String serviceURI =
            "service/local/artifact/maven/redirect?r=" + repository + "&g=" + gav.getGroupId() + "&a="
                + gav.getArtifactId() + "&v=" + gav.getVersion();
        Response response = RequestFacade.doGetRequest( serviceURI );
        Status status = response.getStatus();
        AssertJUnit.assertEquals( "Snapshot download should redirect to a new file\n "
            + response.getRequest().getResourceRef().toString() + " \n Error: " + status.getDescription(), 301,
                                  status.getCode() );

        Reference redirectRef = response.getRedirectRef();
        AssertJUnit.assertNotNull( "Snapshot download should redirect to a new file "
            + response.getRequest().getResourceRef().toString(), redirectRef );

        serviceURI = redirectRef.toString();

        File file = FileUtils.createTempFile( gav.getArtifactId(), '.' + gav.getExtension(), parentDir );
        RequestFacade.downloadFile( new URL( serviceURI ), file.getAbsolutePath() );

        return file;
    }

    protected Metadata downloadMetadataFromRepository( Gav gav, String repoId )
        throws IOException, XmlPullParserException
    {
        String url =
            this.getBaseNexusUrl() + REPOSITORY_RELATIVE_URL + repoId + "/" + gav.getGroupId() + "/"
                + gav.getArtifactId() + "/maven-metadata.xml";

        Response response = RequestFacade.sendMessage( new URL( url ), Method.GET, null );
        if ( response.getStatus().isError() )
        {
            return null;
        }

        InputStream stream = response.getEntity().getStream();
        try
        {
            MetadataXpp3Reader metadataReader = new MetadataXpp3Reader();
            return metadataReader.read( stream );
        }
        finally
        {
            IOUtil.close( stream );
        }

    }

    protected File downloadArtifact( Gav gav, String targetDirectory )
        throws IOException
    {
        return this.downloadArtifact( gav.getGroupId(), gav.getArtifactId(), gav.getVersion(), gav.getExtension(),
                                      gav.getClassifier(), targetDirectory );
    }

    protected File downloadArtifact( String groupId, String artifact, String version, String type, String classifier,
                                     String targetDirectory )
        throws IOException
    {
        return this.downloadArtifact( this.getNexusTestRepoUrl(), groupId, artifact, version, type, classifier,
                                      targetDirectory );
    }

    protected File downloadArtifactFromRepository( String repoId, Gav gav, String targetDirectory )
        throws IOException
    {
        return this.downloadArtifact( baseNexusUrl + REPOSITORY_RELATIVE_URL + repoId + "/", gav.getGroupId(),
                                      gav.getArtifactId(), gav.getVersion(), gav.getExtension(), gav.getClassifier(),
                                      targetDirectory );
    }

    protected File downloadArtifactFromGroup( String groupId, Gav gav, String targetDirectory )
        throws IOException
    {
        return this.downloadArtifact( baseNexusUrl + GROUP_REPOSITORY_RELATIVE_URL + groupId + "/", gav.getGroupId(),
                                      gav.getArtifactId(), gav.getVersion(), gav.getExtension(), gav.getClassifier(),
                                      targetDirectory );
    }

    protected File downloadArtifact( String baseUrl, String groupId, String artifact, String version, String type,
                                     String classifier, String targetDirectory )
        throws IOException
    {
        URL url = new URL( baseUrl + this.getRelitiveArtifactPath( groupId, artifact, version, type, classifier ) );

        String classifierPart = ( classifier != null ) ? "-" + classifier : "";
        return this.downloadFile( url, targetDirectory + "/" + artifact + "-" + version + classifierPart + "." + type );
    }

    protected File downloadFile( URL url, String targetFile )
        throws IOException
    {

        return RequestFacade.downloadFile( url, targetFile );
    }

    protected boolean deleteFromRepository( String groupOrArtifactPath )
        throws IOException
    {
        return this.deleteFromRepository( this.testRepositoryId, groupOrArtifactPath );
    }

    protected boolean deleteFromRepository( String repository, String groupOrArtifactPath )
        throws IOException
    {
        String serviceURI = "service/local/repositories/" + repository + "/content/" + groupOrArtifactPath;

        Response response = RequestFacade.doGetRequest( serviceURI );
        if ( response.getStatus().equals( Status.CLIENT_ERROR_NOT_FOUND ) )
        {
            log.debug( "It was not deleted because it didn't exist " + serviceURI );
            return true;
        }

        log.debug( "deleting: " + serviceURI );
        response = RequestFacade.sendMessage( serviceURI, Method.DELETE );

        boolean deleted = response.getStatus().isSuccess();

        if ( !deleted )
        {
            log.debug( "Failed to delete: " + serviceURI + "  - Status: " + response.getStatus() );
        }

        // fake it because the artifact doesn't exist
        // TODO: clean this up.
        if ( response.getStatus().getCode() == 404 )
        {
            deleted = true;
        }

        return deleted;
    }

    protected String getBaseNexusUrl()
    {
        return baseNexusUrl;
    }

    protected String getNexusTestRepoUrl( String repo )
    {
        return baseNexusUrl + REPOSITORY_RELATIVE_URL + repo + "/";
    }

    protected String getNexusTestRepoUrl()
    {
        return getNexusTestRepoUrl( testRepositoryId );
    }

    protected String getNexusTestRepoServiceUrl()
    {
        return baseNexusUrl + "service/local/repositories/" + testRepositoryId + "/content/";
    }

    protected PlexusContainer getContainer()
    {
        if ( this.container == null )
        {
            this.container = setupContainer( getClass() );
        }
        return this.container;
    }

    protected String getNexusBaseDir()
    {
        return nexusBaseDir;
    }

    protected String getTestRepositoryId()
    {
        return testRepositoryId;
    }

    protected void setTestRepositoryId( String repoId )
    {
        this.testRepositoryId = repoId;
    }

    protected String getRepositoryUrl( String repoId )
    {
        return baseNexusUrl + REPOSITORY_RELATIVE_URL + repoId + "/";
    }

    protected String getGroupUrl( String groupId )
    {
        return baseNexusUrl + GROUP_REPOSITORY_RELATIVE_URL + groupId + "/";
    }

    protected boolean isVerifyNexusConfigBeforeStart()
    {
        return verifyNexusConfigBeforeStart;
    }

    protected void setVerifyNexusConfigBeforeStart( boolean verifyNexusConfigBeforeStart )
    {
        this.verifyNexusConfigBeforeStart = verifyNexusConfigBeforeStart;
    }

    protected boolean printKnownErrorButDoNotFail( Class<? extends AbstractNexusIntegrationTest> clazz, String... tests )
    {
        StringBuffer error =
            new StringBuffer( "*********************************************************************************" );
        error.append( "\n* This test is being skipped because its known to fail," );
        error.append( "\n* It is a very minor error, and is only a problem if you start sending in " );
        error.append( "\n* raw REST request to Nexus. (it is not a security problem)" );
        error.append( "*\n*\n" );
        error.append( "*\n* TestClass: " + clazz );
        for ( String test : tests )
        {
            error.append( "*\n* Test: " + test );
        }
        error.append( "\n**********************************************************************************" );

        log.info( error.toString() );

        return true;
    }

    protected XStream getXMLXStream()
    {
        return XStreamFactory.getXmlXStream();
    }

    protected XStream getJsonXStream()
    {
        return XStreamFactory.getJsonXStream();
    }

    protected RepositoryTypeRegistry getRepositoryTypeRegistry()
        throws ComponentLookupException
    {
        return getContainer().lookup( RepositoryTypeRegistry.class );
    }

    @Test( enabled = false )
    public static final PlexusContainer getStaticContainer()
    {
        if ( staticContainer == null )
        {
            staticContainer = setupContainer( AbstractNexusIntegrationTest.class );
        }
        return staticContainer;
    }

}
