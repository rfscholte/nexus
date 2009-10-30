package org.sonatype.nexus.test.launcher;

import java.io.File;

import org.sonatype.appbooter.ForkedAppBooter;
import org.sonatype.appbooter.ctl.ControllerClient;

public class NexusContext
{

    private ControllerClient client;

    private ForkedAppBooter forkedAppBooter;

    private Integer port;

    private File workDir;

    public NexusContext( ControllerClient client, ForkedAppBooter forkedAppBooter, Integer port, File workDir )
    {
        super();
        this.client = client;
        this.forkedAppBooter = forkedAppBooter;
        this.port = port;
        this.workDir = workDir;
    }

    public ControllerClient getClient()
    {
        return client;
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
    {
        client = null;
        forkedAppBooter = null;
    }

}
