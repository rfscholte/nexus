package org.sonatype.nexus.integrationtests.nexus1806;

import static org.sonatype.nexus.test.utils.EmailUtil.USER_EMAIL;
import static org.sonatype.nexus.test.utils.EmailUtil.USER_PASSWORD;
import static org.sonatype.nexus.test.utils.EmailUtil.USER_USERNAME;

import java.io.IOException;

import javax.mail.internet.MimeMessage;

import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.SmtpSettingsResource;
import org.sonatype.nexus.test.utils.EmailUtil;
import org.sonatype.nexus.test.utils.SettingsMessageUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

public class Nexus1806ValidateSmtpConfigurationIT
    extends AbstractNexusIntegrationTest
{

    private GreenMail changedServer;

    private GreenMail originalServer;

    @Override
    protected void startExtraServices()
        throws Exception
    {
        super.startExtraServices();

        // it is necessary to change port to make sure it worked
        ServerSetup smtp = new ServerSetup( webProxyPort, null, ServerSetup.PROTOCOL_SMTP );

        changedServer = new GreenMail( smtp );
        changedServer.setUser( USER_EMAIL, USER_USERNAME, USER_PASSWORD );
        log.debug( "Starting e-mail server" );
        changedServer.start();

        originalServer = EmailUtil.startEmailServer( emailServerPort );
    }

    @Test
    public void validateChangedSmtp()
        throws Exception
    {
        run( webProxyPort, changedServer );
    }

    @Test
    public void validateOriginalSmtp()
        throws Exception
    {
        run( emailServerPort, originalServer );
    }

    @Test
    public void invalidServer()
        throws Exception
    {
        SmtpSettingsResource smtpSettings = new SmtpSettingsResource();
        smtpSettings.setHost( "someremote.localhost.com.zh" );
        smtpSettings.setPort( 1234 );
        smtpSettings.setUsername( EmailUtil.USER_USERNAME );
        smtpSettings.setPassword( EmailUtil.USER_PASSWORD );
        smtpSettings.setSystemEmailAddress( EmailUtil.USER_EMAIL );
        smtpSettings.setTestEmail( "test_user@sonatype.org" );
        Status status = SettingsMessageUtil.validateSmtp( smtpSettings );
        AssertJUnit.assertEquals( "Unable to validate e-mail " + status, 400, status.getCode() );
    }

    @Test
    public void invalidUsername()
        throws Exception
    {
        if ( true )
        {
            // greenmail doesn't allow authentication
            printKnownErrorButDoNotFail( getClass(), "invalidUsername()" );
            return;
        }

        String login = "invaliduser_test";
        String email = "invaliduser_test@sonatype.org";
        changedServer.setUser( email, login, "%^$@invalidUserPW**" );

        SmtpSettingsResource smtpSettings = new SmtpSettingsResource();
        smtpSettings.setHost( "localhost" );
        smtpSettings.setPort( webProxyPort );
        smtpSettings.setUsername( login );
        smtpSettings.setPassword( USER_PASSWORD );
        smtpSettings.setSystemEmailAddress( email );
        smtpSettings.setTestEmail( "test_user@sonatype.org" );
        Status status = SettingsMessageUtil.validateSmtp( smtpSettings );
        AssertJUnit.assertEquals( "Unable to validate e-mail " + status, 400, status.getCode() );
    }

    private void run( int port, GreenMail server )
        throws IOException, InterruptedException
    {
        SmtpSettingsResource smtpSettings = new SmtpSettingsResource();
        smtpSettings.setHost( "localhost" );
        smtpSettings.setPort( port );
        smtpSettings.setUsername( EmailUtil.USER_USERNAME );
        smtpSettings.setPassword( EmailUtil.USER_PASSWORD );
        smtpSettings.setSystemEmailAddress( EmailUtil.USER_EMAIL );
        smtpSettings.setTestEmail( "test_user@sonatype.org" );
        Status status = SettingsMessageUtil.validateSmtp( smtpSettings );
        AssertJUnit.assertTrue( "Unable to validate e-mail " + status, status.isSuccess() );

        server.waitForIncomingEmail( 2000, 1 );

        MimeMessage[] msgs = server.getReceivedMessages();
        AssertJUnit.assertEquals( 1, msgs.length );

        MimeMessage msg = msgs[0];
        String body = GreenMailUtil.getBody( msg );

        AssertJUnit.assertNotNull( "Missing message", body );
        AssertJUnit.assertFalse( "Got empty message", body.trim().length() == 0 );
    }

    @AfterClass
    public void stop()
    {
        originalServer.stop();
        changedServer.stop();
    }
}
