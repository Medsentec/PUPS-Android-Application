package com.medsentec.data;

import java.text.ParseException;

/**
 * Created by Justin Ho on 3/13/17.
 */

public interface PartialDataParser<T> {

    public T parseStringForData(String rawData) throws ParseException;

    public void parsePartialString(String partialDataString);

}
