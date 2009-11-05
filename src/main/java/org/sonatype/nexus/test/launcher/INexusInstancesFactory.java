package org.sonatype.nexus.test.launcher;

import java.io.File;

public interface INexusInstancesFactory
{

    public abstract NexusContext createInstance()
        throws Exception;

    public abstract NexusContext createInstance( Integer nexusPort, File nexusWorkDir )
        throws Exception;

    public abstract void shutdown()
        throws Exception;

    public abstract void destroyInstance( NexusContext context )
        throws Exception;

}