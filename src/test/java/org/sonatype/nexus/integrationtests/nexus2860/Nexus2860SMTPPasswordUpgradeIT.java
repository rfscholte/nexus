package org.sonatype.nexus.integrationtests.nexus2860;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.NexusConfigUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class Nexus2860SMTPPasswordUpgradeIT
    extends AbstractNexusIntegrationTest
{

    @Test
    public void upgradeSmtp()
        throws Exception
    {
        String pw = NexusConfigUtil.getNexusConfig().getSmtpConfiguration().getPassword();
        // ensuring it wasn't encrypted twice
        AssertJUnit.assertEquals( "IT-password", pw );
    }
}
