package org.sonatype.nexus.index;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class StringListFieldValue
    extends FieldValue<List<String>>
{
    public static final String FS = "|";

    public static final Pattern FS_PATTERN = Pattern.compile( Pattern.quote( FS ) );

    public StringListFieldValue( Field field, List<String> value )
    {
        super( field, value );
    }

    @Override
    protected String asString( List<String> value )
    {
        return lst2str( value );
    }

    @Override
    protected List<String> asValue( String rawValue )
    {
        return str2lst( rawValue );
    }

    // ==

    public static String lst2str( Collection<String> list )
    {
        StringBuilder sb = new StringBuilder();

        for ( String s : list )
        {
            sb.append( s ).append( FS );
        }

        return sb.length() == 0 ? sb.toString() : sb.substring( 0, sb.length() - 1 );
    }

    public static List<String> str2lst( String str )
    {
        return Arrays.asList( FS_PATTERN.split( str ) );
    }
}
