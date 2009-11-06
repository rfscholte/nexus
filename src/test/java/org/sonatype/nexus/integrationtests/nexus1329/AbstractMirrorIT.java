package org.sonatype.nexus.integrationtests.nexus1329;

import org.restlet.data.MediaType;
import org.sonatype.jettytestsuite.ControlledServer;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.JettyInstaceFactory;
import org.sonatype.nexus.test.utils.MirrorMessageUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class AbstractMirrorIT
    extends AbstractNexusIntegrationTest
{

    public static final String REPO = "nexus1329-repo";

    @BeforeClass
    public static void init()
        throws Exception
    {
        cleanWorkDir();
    }

    protected ControlledServer server;

    protected MirrorMessageUtils messageUtil;

    public AbstractMirrorIT()
    {
        super();
        this.messageUtil = new MirrorMessageUtils( this.getJsonXStream(), MediaType.APPLICATION_JSON );
    }

    @Override
    protected void startExtraServices()
        throws Exception
    {
        server = JettyInstaceFactory.getHttpProxyWtihReturnCodeControlServer( webProxyPort );
        server.start();
    }

    @AfterClass
    public void stop()
        throws Exception
    {
        server.stop();
    }

}