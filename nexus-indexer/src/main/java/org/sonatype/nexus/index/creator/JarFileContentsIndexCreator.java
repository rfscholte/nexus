/**
 * Copyright (c) 2007-2008 Sonatype, Inc. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License Version 1.0, which accompanies this distribution and is
 * available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.sonatype.nexus.index.creator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.index.ArtifactContext;
import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.ArtifactInfoRecord;
import org.sonatype.nexus.index.IndexerField;
import org.sonatype.nexus.index.IndexerFieldVersion;
import org.sonatype.nexus.index.MAVEN;
import org.sonatype.nexus.index.MavenArtifactInfoRecord;
import org.sonatype.nexus.index.StringFieldValue;
import org.sonatype.nexus.index.context.IndexCreator;

/**
 * An index creator used to index Java class names from a Maven artifact. Will open up the JAR and collect all the class
 * names from it.
 */
@Component( role = IndexCreator.class, hint = JarFileContentsIndexCreator.ID )
public class JarFileContentsIndexCreator
    extends AbstractIndexCreator
    implements LegacyDocumentUpdater
{
    public static final String ID = "jarContent";

    public static final IndexerField FLD_CLASSNAMES =
        new IndexerField( MAVEN.CLASSNAMES, IndexerFieldVersion.V3, "classnames", "Artifact Classes (tokenized)",
            Store.NO, Index.TOKENIZED );

    /**
     * NexusAnalyzer makes exception with this field only, to keep backward compatibility with old consumers of
     * nexus-indexer. This field is here for "backward" compat only! The order is important too! FLD_CLASSNAMES must be
     * registered BEFORE FLD_CLASSNAMES_KW!
     */
    public static final IndexerField FLD_CLASSNAMES_KW =
        new IndexerField( MAVEN.CLASSNAMES, IndexerFieldVersion.V1, "c",
            "Artifact Classes (tokenized on newlines only)", Store.COMPRESS, Index.TOKENIZED );

    public void populateArtifactInfo( ArtifactContext artifactContext )
        throws IOException
    {
        ArtifactInfoRecord ai = artifactContext.getArtifactInfo();

        File artifactFile = artifactContext.getArtifact();

        if ( artifactFile != null && artifactFile.exists() && artifactFile.getName().endsWith( ".jar" ) )
        {
            updateArtifactInfo( ai.adapt( MavenArtifactInfoRecord.class ), artifactFile );
        }
    }

    public void updateDocument( ArtifactInfoRecord ai, Document doc )
    {
        MavenArtifactInfoRecord mai = ai.adapt( MavenArtifactInfoRecord.class );

        if ( mai.getClassNames() != null )
        {
            FLD_CLASSNAMES_KW.toLuceneField( mai, doc );
            FLD_CLASSNAMES.toLuceneField( mai, doc );
        }
    }

    public void updateLegacyDocument( ArtifactInfo ai, Document doc )
    {
        if ( ai.classNames != null )
        {
            String classNames = ai.classNames;

            // downgrade the classNames if needed
            if ( classNames.length() > 0 && classNames.charAt( 0 ) == '/' )
            {
                // conversion from the new format
                String[] lines = classNames.split( "\\n" );
                StringBuilder sb = new StringBuilder();
                for ( String line : lines )
                {
                    sb.append( line.substring( 1 ) ).append( '\n' );
                }

                classNames = sb.toString();
            }

            FLD_CLASSNAMES_KW.toLuceneField( ai, doc );
        }
    }

    public boolean updateArtifactInfo( Document doc, ArtifactInfoRecord artifactInfo )
    {
        String names = doc.get( FLD_CLASSNAMES_KW.getKey() );

        if ( names != null )
        {
            if ( names.length() == 0 || names.charAt( 0 ) == '/' )
            {
                FLD_CLASSNAMES_KW.toFieldValue( doc, artifactInfo, new StringFieldValue(
                    FLD_CLASSNAMES_KW.getOntology(), null ) );
            }
            else
            {
                // conversion from the old format
                String[] lines = names.split( "\\n" );
                StringBuilder sb = new StringBuilder();
                for ( String line : lines )
                {
                    sb.append( '/' ).append( line ).append( '\n' );
                }
                artifactInfo.addFieldValue( new StringFieldValue( FLD_CLASSNAMES_KW.getOntology(), sb.toString() ) );
            }

            return true;
        }

        return false;
    }

    private void updateArtifactInfo( MavenArtifactInfoRecord ai, File f )
        throws IOException
    {
        if ( ai == null )
        {
            return;
        }

        ZipFile jar = null;

        try
        {
            jar = new ZipFile( f );

            StringBuilder sb = new StringBuilder();

            @SuppressWarnings( "unchecked" )
            Enumeration en = jar.entries();
            while ( en.hasMoreElements() )
            {
                ZipEntry e = (ZipEntry) en.nextElement();

                String name = e.getName();

                if ( name.endsWith( ".class" ) )
                {
                    // TODO verify if class is public or protected
                    // TODO skip all inner classes for now

                    int i = name.indexOf( "$" );

                    if ( i == -1 )
                    {
                        if ( name.charAt( 0 ) != '/' )
                        {
                            sb.append( '/' );
                        }

                        // class name without ".class"
                        sb.append( name.substring( 0, name.length() - 6 ) ).append( '\n' );
                    }
                }
            }

            if ( sb.toString().trim().length() != 0 )
            {
                ai.setClassNames( sb.toString() );
            }
            else
            {
                ai.setClassNames( null );
            }
        }
        finally
        {
            if ( jar != null )
            {
                try
                {
                    jar.close();
                }
                catch ( Exception e )
                {
                    getLogger().error( "Could not close jar file properly.", e );
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return ID;
    }

    public Collection<IndexerField> getIndexerFields()
    {
        return Arrays.asList( FLD_CLASSNAMES, FLD_CLASSNAMES_KW );
    }
}
