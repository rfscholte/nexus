package nexuspool;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.rest.model.StatusResourceResponse;
import org.sonatype.nexus.test.launcher.NexusContext;
import org.sonatype.nexus.test.launcher.NexusInstancesPool;
import org.sonatype.nexus.test.utils.NexusIllegalStateException;
import org.sonatype.nexus.test.utils.XStreamFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.xstream.XStream;

public class NexusPoolTest
{

    private static ObjectPool pool;

    private static int validateCount;

    private static int passivateCount;

    private static int makeCount;

    private static int destroyCount;

    private static int activateCount;

    private static DefaultPlexusContainer container;

    @BeforeClass
    public static void createContainer()
        throws PlexusContainerException
    {
        container = new DefaultPlexusContainer();

        validateCount = 0;
        passivateCount = 0;
        makeCount = 0;
        destroyCount = 0;
        activateCount = 0;
        PoolableObjectFactory factory = new PoolableObjectFactory()
        {
            NexusInstancesPool factory = new NexusInstancesPool( container );

            public boolean validateObject( Object obj )
            {
                validateCount++;
                return factory.validateObject( obj );
            }

            public void passivateObject( Object obj )
                throws Exception
            {
                passivateCount++;
                factory.passivateObject( obj );
            }

            public Object makeObject()
                throws Exception
            {
                makeCount++;
                return factory.makeObject();
            }

            public void destroyObject( Object obj )
                throws Exception
            {
                destroyCount++;
                factory.destroyObject( obj );
            }

            public void activateObject( Object obj )
                throws Exception
            {
                activateCount++;
                factory.activateObject( obj );
            }
        };

        pool = new GenericObjectPool( factory );
    }

    @Test
    public void singleInstance()
        throws Exception
    {
        NexusContext c1 = (NexusContext) pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getClient().isOpen() );
            Integer port = c1.getPort();
            MatcherAssert.assertThat( getNexusStatus( port ).getData().getState(),
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
        NexusContext c1 = (NexusContext) pool.borrowObject();
        NexusContext c2 = (NexusContext) pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getClient().isOpen() );
            MatcherAssert.assertThat( getNexusStatus( c1.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );

            MatcherAssert.assertThat( c1, CoreMatchers.not( CoreMatchers.equalTo( c2 ) ) );

            Assert.assertTrue( c2.getClient().isOpen() );
            MatcherAssert.assertThat( getNexusStatus( c2.getPort() ).getData().getState(),
                                      CoreMatchers.equalTo( "STARTED" ) );

        }
        finally
        {
            pool.returnObject( c1 );
            pool.returnObject( c2 );
        }
    }

    @Test( threadPoolSize = 4 /* pool has 8 elements we use 2 per test */, invocationCount = 30, dependsOnMethods = { "testPool" } )
    public void severalObjectComparation()
        throws Exception
    {
        NexusContext c1 = (NexusContext) pool.borrowObject();
        NexusContext c2 = (NexusContext) pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getClient().isOpen() );
            MatcherAssert.assertThat( c1, CoreMatchers.not( CoreMatchers.equalTo( c2 ) ) );
            Assert.assertTrue( c2.getClient().isOpen() );
        }
        finally
        {
            pool.returnObject( c1 );
            pool.returnObject( c2 );
        }
    }

    @Test( threadPoolSize = 8, invocationCount = 20, dependsOnMethods = { "testPool" } )
    public void severalRequests()
        throws Exception
    {
        NexusContext c1 = (NexusContext) pool.borrowObject();
        try
        {
            Assert.assertTrue( c1.getClient().isOpen() );
            MatcherAssert.assertThat( getNexusStatus( c1.getPort() ).getData().getState(),
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
        System.out.println( "=========================================================================" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "makeCount " + makeCount );
        System.out.println( "activateCount " + activateCount );
        System.out.println( "passivateCount " + passivateCount );
        System.out.println( "destroyCount " + destroyCount );
        System.out.println( "=                                                                       =" );
        System.out.println( "=                                                                       =" );
        System.out.println( "=========================================================================" );

        pool.close();
        pool = null;

        container.dispose();
        container = null;
    }

    public static StatusResourceResponse getNexusStatus( int port )
        throws NexusIllegalStateException
    {

        Response response;
        Status status;
        int i = 0;
        do
        {
            try
            {
                response =
                    RequestFacade.sendMessage( new URL( "http://localhost:" + port + "/nexus/service/local/status" ),
                                               Method.GET, null );
            }
            catch ( IOException e )
            {
                throw new NexusIllegalStateException( "Unable to retrieve nexus status", e );
            }

            status = response.getStatus();

            if ( status.isSuccess() )
            {
                break;
            }

            if ( status.isConnectorError() && ++i < 100 )
            {
                try
                {
                    Thread.sleep( 200 );
                }
                catch ( InterruptedException e )
                {
                    // ignore
                }
                continue;
            }
            throw new NexusIllegalStateException( "Error retrieving current status " + response.getStatus().toString() );
        }
        while ( true );

        XStream xstream = XStreamFactory.getXmlXStream();

        String entityText;
        try
        {
            entityText = response.getEntity().getText();
        }
        catch ( IOException e )
        {
            throw new NexusIllegalStateException( "Unable to retrieve nexus status " + new XStream().toXML( response ),
                                                  e );
        }

        return (StatusResourceResponse) xstream.fromXML( entityText );
    }

}
