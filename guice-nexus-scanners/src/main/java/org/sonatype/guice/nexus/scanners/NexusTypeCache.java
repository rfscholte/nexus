/**
 * Copyright (c) 2010 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.guice.nexus.scanners;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceScanner;
import org.sonatype.guice.bean.scanners.EmptyClassVisitor;
import org.sonatype.plugin.ExtensionPoint;
import org.sonatype.plugin.Managed;

/**
 * Caching {@link ClassVisitor} that maintains a map of interface names to {@link NexusType}s.
 */
final class NexusTypeCache
    extends EmptyClassVisitor
{
    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    private static final String EXTENSION_POINT_DESC = Type.getDescriptor( ExtensionPoint.class );

    private static final String MANAGED_DESC = Type.getDescriptor( Managed.class );

    private static final String SINGLETON_DESC = Type.getDescriptor( Singleton.class );

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final Map<String, NexusType> cachedResults = new HashMap<String, NexusType>();

    private NexusType nexusType;

    private boolean isSingleton;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * Attempts to find the {@link NexusType} of a given interface.
     * 
     * @param space The class space
     * @param desc The interface name
     * @return Nexus component type
     */
    public NexusType nexusType( final ClassSpace space, final String name )
    {
        if ( !cachedResults.containsKey( name ) )
        {
            ClassSpaceScanner.accept( this, space.getResource( name + ".class" ) );
            cachedResults.put( name, nexusType );
        }
        return cachedResults.get( name );
    }

    @Override
    public void visit( final int version, final int access, final String name, final String signature,
                       final String superName, final String[] interfaces )
    {
        nexusType = NexusType.UNKNOWN;
    }

    @Override
    public AnnotationVisitor visitAnnotation( final String desc, final boolean visible )
    {
        if ( EXTENSION_POINT_DESC.equals( desc ) )
        {
            nexusType = NexusType.EXTENSION_POINT;
        }
        else if ( MANAGED_DESC.equals( desc ) )
        {
            nexusType = NexusType.MANAGED;
        }
        else if ( SINGLETON_DESC.equals( desc ) )
        {
            isSingleton = true;
        }
        return null;
    }

    @Override
    public void visitEnd()
    {
        if ( isSingleton )
        {
            nexusType = nexusType.asSingleton();
        }
    }
}
