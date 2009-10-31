package org.sonatype.nexus.test.launcher;

import java.io.File;

import org.sonatype.appbooter.ForkedAppBooter;

public class NexusContext
{

    private ForkedAppBooter forkedAppBooter;

    private Integer port;

    private File workDir;

    public NexusContext( ForkedAppBooter forkedAppBooter, Integer port, File workDir )
    {
        super();
        this.forkedAppBooter = forkedAppBooter;
        this.port = port;
        this.workDir = workDir;
    }

    public ForkedAppBooter getForkedAppBooter()
    {
        return forkedAppBooter;
    }

    public Integer getPort()
    {
        return port;
    }

    public File getWorkDir()
    {
        return workDir;
    }

    public void kill()
        throws Exception
    {
        if ( forkedAppBooter != null )
        {
            if ( !forkedAppBooter.isStopped() )
            {
                forkedAppBooter.shutdown();
            }
        }
        forkedAppBooter = null;
    }

}
