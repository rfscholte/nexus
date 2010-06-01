package org.sonatype.nexus.index;

import org.apache.lucene.document.NumberTools;

public class LongFieldValue
    extends FieldValue<Long>
{
    public LongFieldValue( Field field, Long value )
    {
        super( field, value );
    }

    @Override
    public Long asValue( String value )
    {
        return NumberTools.stringToLong( value );
    }

    @Override
    protected String asString( Long rawValue )
    {
        return NumberTools.longToString( rawValue );
    }
}
