package org.sonatype.nexus.index;

import java.text.ParseException;
import java.util.Date;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;

public class DateFieldValue
    extends FieldValue<Date>
{
    private final Resolution resolution;

    public DateFieldValue( Field field, Date value, final Resolution resolution )
    {
        super( field, value );

        this.resolution = resolution;
    }

    @Override
    protected String asString( Date value )
    {
        return DateTools.dateToString( value, resolution );
    }

    @Override
    protected Date asValue( String rawValue )
    {
        try
        {
            return DateTools.stringToDate( rawValue );
        }
        catch ( ParseException e )
        {
            // eh?
            e.printStackTrace();

            return new Date();
        }
    }

}
