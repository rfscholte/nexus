package org.sonatype.nexus.test.launcher;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.sonatype.nexus.test.launcher.NexusContext;
import org.sonatype.nexus.test.launcher.NexusInstancesFactory;
import org.sonatype.nexus.test.utils.NexusStatusUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NexusInstancesFactoryTest
{

    private static NexusInstancesFactory factory;

    private static DefaultPlexusContainer container;

    @BeforeClass
    public static void createContainer()
        throws PlexusContainerException
    {
        container = new DefaultPlexusContainer();

        factory = new NexusInstancesFactory( container );
    }

    @Test
    public void singleInstance()
        throws Exception
    {
        NexusContext c1 = factory.createInstance();
        try
        {
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().isOpen() );
            Integer port = c1.getPort();
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( port ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );
        }
        finally
        {
            factory.destroyInstance( c1 );
        }
    }

    @Test( dependsOnMethods = { "singleInstance" } )
    public void testFactory()
        throws Exception
    {
        NexusContext c1 = factory.createInstance();
        NexusContext c2 = factory.createInstance();
        try
        {
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().isOpen() );
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( c1.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );

            Assert.assertTrue( c2.getForkedAppBooter().getControllerClient().isOpen() );
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( c2.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );

            MatcherAssert.assertThat( c1, CoreMatchers.not( CoreMatchers.equalTo( c2 ) ) );
            MatcherAssert.assertThat( c1.getPort(), CoreMatchers.not( CoreMatchers.equalTo( c2.getPort() ) ) );
            MatcherAssert.assertThat( c1.getWorkDir(), CoreMatchers.not( CoreMatchers.equalTo( c2.getWorkDir() ) ) );

        }
        finally
        {
            factory.destroyInstance( c1 );
            factory.destroyInstance( c2 );
        }
    }

    @Test( threadPoolSize = 8, invocationCount = 200, dependsOnMethods = { "testFactory" } )
    public void severalRequests()
        throws Exception
    {
        NexusContext c1 = factory.createInstance();
        try
        {
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().isOpen() );
            Assert.assertTrue( c1.getForkedAppBooter().getControllerClient().ping() );
            MatcherAssert.assertThat( NexusStatusUtil.getNexusStatus( c1.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );
        }
        finally
        {
            factory.destroyInstance( c1 );
        }
    }

    @AfterClass
    public static void kill()
        throws Exception
    {
        factory.shutdown();
        factory = null;

        container.dispose();
        container = null;
    }

}
