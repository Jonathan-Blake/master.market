package com.stocktrader.market.service.report.convertor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stocktrader.market.util.AutoDeletingFile;
import org.springframework.data.util.Streamable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class CSVConvertor implements IReportConvertor {

    private static final CsvMapper mapper = new CsvMapper();

    static {
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @Override
    public AutoDeletingFile convertJSON(Object json, Class schema) throws IOException {
        AutoDeletingFile ret = null;
        if (json instanceof Collection) {
            final Object[] collection = ((Collection) json).toArray();
            if (collection.length > 0)
                ret = convertJSON(collection, collection[0].getClass());
        } else if (json instanceof Streamable) {
            final Object[] stream = ((Streamable) json).stream().toArray();
            if (stream.length > 0)
                ret = convertJSON(stream, stream[0].getClass());
        } else {
            CsvSchema csvSchema = mapper.schemaFor(schema)
                    .withHeader();
            File tempFile = File.createTempFile("CSV-Report-", ".csv");
            mapper.writerFor(json.getClass()).with(csvSchema).writeValue(tempFile, json);
            ret = new AutoDeletingFile(tempFile, ".csv");
        }
        return ret;
    }
}
