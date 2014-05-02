package edu.buffalo.cse562.indexer.modifier;

import edu.buffalo.cse562.data.Datum;
import edu.buffalo.cse562.indexer.serializer.DatumSerializer;
import edu.buffalo.cse562.schema.ColumnSchema;
import jdbm.*;

import java.io.File;
import java.io.IOException;

import static edu.buffalo.cse562.data.DatumUtils.getDatumOfTypeFromValue;
import static edu.buffalo.cse562.indexer.constants.IndexingConstants.RECORD_MANAGER_NAME;

public class Indexer {

    protected RecordManager getRecordManager(File indexDir) {
        try {
            return RecordManagerFactory.createRecordManager(indexDir.getAbsolutePath() + "//" + RECORD_MANAGER_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating record manager!");
        }
    }

    protected PrimaryStoreMap<Long, String> getPrimaryStoreMap(RecordManager recordManager, String tableName) {
        return recordManager.storeMap(tableName);
    }

    protected SecondaryTreeMap<Datum, Long, String> getSecondaryMap(PrimaryStoreMap<Long, String> storeMap,
                                                                    ColumnSchema[] schema,
                                                                    Integer position) {
        ColumnSchema columnSchema = schema[position];
        return storeMap.secondaryTreeMap(
                columnSchema.getColName(),
                getSecondaryKeyExtractor(position, columnSchema),
                new DatumSerializer(columnSchema)
        );
    }

    private SecondaryKeyExtractor<Datum, Long, String> getSecondaryKeyExtractor(final Integer position, final ColumnSchema columnSchema) {
        return new SecondaryKeyExtractor<Datum, Long, String>() {
            @Override
            public Datum extractSecondaryKey(Long primaryKey, String tuple) {
                final String cellValue = (tuple.split("\\|"))[position];
                return getDatumOfTypeFromValue(columnSchema.getType(), cellValue);
            }
        };
    }

    protected void commit(RecordManager recordManager) {
        try {
            recordManager.commit();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error closing record manager!");
        }
    }

    protected void close(RecordManager recordManager) {
        try {
            recordManager.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error closing record manager!");
        }
    }
}
