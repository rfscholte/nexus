package org.sonatype.nexus.index;

import java.util.HashSet;
import java.util.Set;

import org.sonatype.nexus.index.context.IndexingContext;

/**
 * A special reusable filter, that filters the result set to unique Repository-GroupId-ArtifactId combination, leaving
 * out Version. There is a switch to make the Indexer-wide unique by ignoring repositories too.
 * 
 * @author cstamas
 */
public class UniqueArtifactFilterPostprocessor
    implements ArtifactInfoFilter
{
    private final Set<Field> uniqueFields = new HashSet<Field>();

    private final Set<String> gas = new HashSet<String>();

    public UniqueArtifactFilterPostprocessor()
    {
    }

    public UniqueArtifactFilterPostprocessor( Set<Field> uniqueFields )
    {
        this.uniqueFields.addAll( uniqueFields );
    }

    public boolean accepts( IndexingContext ctx, ArtifactInfoRecord ai )
    {
        StringBuilder sb = new StringBuilder();

        for ( Field field : uniqueFields )
        {
            sb.append( ai.get( field ).getRawValue() ).append( ":" );
        }

        String key = sb.toString().substring( 0, sb.length() - 1 );

        if ( gas.contains( key ) )
        {
            return false;
        }
        else
        {
            gas.add( key );

            postprocess( ctx, ai );

            return true;
        }
    }

    public void postprocess( IndexingContext ctx, ArtifactInfoRecord ai )
    {
        for ( Field field : ai.getFields() )
        {
            if ( !uniqueFields.contains( field ) )
            {
                ai.get( field ).setRawValue( null );
            }
        }
    }

    public void addField( Field field )
    {
        uniqueFields.add( field );
    }
}
