package nexuspool;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.sonatype.nexus.test.launcher.NexusContext;
import org.sonatype.nexus.test.launcher.NexusInstancesPool;
import org.sonatype.nexus.test.utils.NexusStatusUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NexusPoolTest
{

    private static NexusInstancesPool pool;

    private static DefaultPlexusContainer container;

    @BeforeClass
    public static void createContainer()
        throws PlexusContainerException
    {
        container = new DefaultPlexusContainer();

        pool = new NexusInstancesPool( container );
    }

    @Test
    public void singleInstance()
        throws Exception
    {
        NexusContext c1 = pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().isOpen() );
            Integer port = c1.getPort();
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( port ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );
        }
        finally
        {
            pool.returnObject( c1 );
        }
    }

    @Test( dependsOnMethods = { "singleInstance" } )
    public void testPool()
        throws Exception
    {
        NexusContext c1 = pool.borrowObject();
        NexusContext c2 = pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().isOpen() );
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( c1.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );

            MatcherAssert.assertThat( c1, CoreMatchers.not( CoreMatchers.equalTo( c2 ) ) );

            Assert.assertTrue( c2.getForkedAppBooter().getControllerClient().isOpen() );
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( c2.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );

        }
        finally
        {
            pool.returnObject( c1 );
            pool.returnObject( c2 );
        }
    }

    @Test( threadPoolSize = 8, invocationCount = 50, dependsOnMethods = { "testPool" } )
    public void severalRequests()
        throws Exception
    {
        NexusContext c1 = pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().isOpen() );
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().ping() );
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( c1.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );
        }
        finally
        {
            pool.returnObject( c1 );
        }
    }

    @AfterClass
    public static void kill()
        throws Exception
    {
        pool.close();
        pool = null;

        container.dispose();
        container = null;
    }

}
