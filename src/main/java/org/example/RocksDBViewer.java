package org.example;

import org.rocksdb.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RocksDBViewer {
    public static void main(String[] args) throws Exception {
        RocksDB.loadLibrary();

        final String dbPath = "/Users/kjalla/IdeaProjects/nexus/data/repository/local/rocks/4E85DCEB-5BBF-5BF6-9B92-54EFEBF98724";

        List<byte[]> cfNames = RocksDB.listColumnFamilies(new Options(), dbPath);
        List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();

        for (byte[] name : cfNames) {
            cfDescriptors.add(new ColumnFamilyDescriptor(name, new ColumnFamilyOptions()));
        }

        List<ColumnFamilyHandle> cfHandles = new ArrayList<>();
        DBOptions dbOptions = new DBOptions().setCreateIfMissing(false).setCreateMissingColumnFamilies(false);

        try (RocksDB db = RocksDB.open(dbOptions, dbPath, cfDescriptors, cfHandles)) {
            for (int i = 0; i < cfHandles.size(); i++) {
                ColumnFamilyHandle handle = cfHandles.get(i);
                String cfName = new String(cfDescriptors.get(i).columnFamilyName(), StandardCharsets.UTF_8);
                System.out.println("Reading from column family: " + cfName);

                try (RocksIterator iterator = db.newIterator(handle)) {
                    for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                        String key = new String(iterator.key(), StandardCharsets.UTF_8);
                        String value = new String(iterator.value(), StandardCharsets.UTF_8);
                        System.out.printf("  Key: %s, Value: %s%n", key, value);
                    }
                }
            }
        } finally {
            // Clean up all CF handles
            for (ColumnFamilyHandle handle : cfHandles) {
                handle.close();
            }
            dbOptions.close();
        }


//        RocksDB.loadLibrary();
//        Options options = new Options().setCreateIfMissing(false);
//        try (RocksDB db = RocksDB.open(options, "/Users/kjalla/IdeaProjects/nexus/data/repository/local/rocks/4E85DCEB-5BBF-5BF6-9B92-54EFEBF98724")) {
//            try(RocksIterator iterator = db.newIterator()) {
//                for(iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
//                    System.out.println("Key: " + new String(iterator.key()) + ", Value: " + new String(iterator.value()));
//                }
//            }
//        }
    }
}
