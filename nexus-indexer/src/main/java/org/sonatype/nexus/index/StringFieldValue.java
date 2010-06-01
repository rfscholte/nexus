package org.sonatype.nexus.index;

public class StringFieldValue
    extends FieldValue<String>
{
    public StringFieldValue( Field field, String value )
    {
        super( field, value );
    }

    @Override
    public String asValue( String value )
    {
        return value;
    }

    @Override
    protected String asString( String rawValue )
    {
        return rawValue;
    }
}
