package org.sonatype.nexus.integrationtests.nexus2178;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;
import org.sonatype.nexus.rest.model.ErrorReportingSettings;
import org.sonatype.nexus.rest.model.GlobalConfigurationResource;
import org.sonatype.nexus.test.utils.SettingsMessageUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class Nexus2178ErrorReportingConfigRestIT
    extends AbstractNexusIntegrationTest
{
    @Test
    public void validationConfiguration()
        throws Exception
    {
        // Default config
        GlobalConfigurationResource resource = SettingsMessageUtil.getCurrentSettings();
        
        AssertJUnit.assertNull( "Error reporting should be null by default", resource.getErrorReportingSettings() );
        
        // Set some values
        ErrorReportingSettings settings = new ErrorReportingSettings();
        settings.setJiraUsername( "someusername" );
        settings.setJiraPassword( "somepassword" );
        
        resource.setErrorReportingSettings( settings );
        
        SettingsMessageUtil.save( resource );
        
        resource = SettingsMessageUtil.getCurrentSettings();
        
        AssertJUnit.assertNotNull( "Error reporting should not be null", resource.getErrorReportingSettings() );
        AssertJUnit.assertEquals( "someusername", resource.getErrorReportingSettings().getJiraUsername() );
        AssertJUnit.assertEquals( AbstractNexusPlexusResource.PASSWORD_PLACE_HOLDER, resource.getErrorReportingSettings().getJiraPassword() );
        
        // Clear them again
        resource.setErrorReportingSettings( null );
        
        SettingsMessageUtil.save( resource );
        
        resource = SettingsMessageUtil.getCurrentSettings();
        
        AssertJUnit.assertNull( "Error reporting should be null", resource.getErrorReportingSettings() );
    }
}
