package org.sonatype.nexus.test.launcher;

import java.io.File;

import org.sonatype.appbooter.ForkedAppBooter;

public class NexusContext
{

    private ForkedAppBooter forkedAppBooter;

    private String forkedAppBooterHint;

    private Integer port;

    private File workDir;

    public NexusContext( String forkedAppBooterHint, Integer port, File workDir )
    {
        super();
        this.forkedAppBooterHint = forkedAppBooterHint;
        this.port = port;
        this.workDir = workDir;
    }

    public ForkedAppBooter getForkedAppBooter()
    {
        return forkedAppBooter;
    }

    String getForkedAppBooterHint()
    {
        return forkedAppBooterHint;
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
        forkedAppBooterHint = null;
        if ( forkedAppBooter != null )
        {
            forkedAppBooter.shutdown();
        }
        forkedAppBooter = null;
    }

    void setForkedAppBooter( ForkedAppBooter forkedAppBooter )
    {
        this.forkedAppBooter = forkedAppBooter;
    }

}
