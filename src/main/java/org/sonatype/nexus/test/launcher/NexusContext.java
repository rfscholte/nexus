package org.sonatype.nexus.test.launcher;

import java.io.File;

public class NexusContext
{

    private Object appBooter;

    private Integer port;

    private File workDir;

    public NexusContext( Object appBooter, Integer port, File workDir )
    {
        super();
        this.appBooter = appBooter;
        this.port = port;
        this.workDir = workDir;
    }

    public Object getAppBooter()
    {
        return appBooter;
    }

    public Integer getPort()
    {
        return port;
    }

    public File getWorkDir()
    {
        return workDir;
    }

    public void release()
    {
        appBooter = null;
    }

}
