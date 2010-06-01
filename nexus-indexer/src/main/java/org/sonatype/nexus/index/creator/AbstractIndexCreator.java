/**
 * Copyright (c) 2007-2008 Sonatype, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.sonatype.nexus.index.creator;

import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.nexus.index.context.IndexCreator;

/**
 * An abstract base class for {@link IndexCreator} implementations.
 * 
 * @author Jason van Zyl
 */
public abstract class AbstractIndexCreator
    implements IndexCreator
{
    @Requirement
    private Logger logger;

    protected Logger getLogger()
    {
        return logger;
    }

    public static String bos( boolean b )
    {
        return b ? "1" : "0";
    }

    public static boolean sob( String b )
    {
        return b.equals( "1" );
    }

}
