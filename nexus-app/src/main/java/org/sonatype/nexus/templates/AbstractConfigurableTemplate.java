package org.sonatype.nexus.templates;

import org.sonatype.nexus.configuration.CoreConfiguration;

public abstract class AbstractConfigurableTemplate
    extends AbstractTemplate
    implements ConfigurableTemplate
{
    private CoreConfiguration coreConfiguration;

    public AbstractConfigurableTemplate( String id, String description )
    {
        super( id, description );
    }

    public CoreConfiguration getCoreConfiguration()
    {
        if ( coreConfiguration == null )
        {
            coreConfiguration = initCoreConfiguration();
        }

        return coreConfiguration;
    }

    public void setCoreConfiguration( CoreConfiguration coreConfiguration )
    {
        this.coreConfiguration = coreConfiguration;
    }

    // ==

    protected abstract CoreConfiguration initCoreConfiguration();
}