package org.example;

import org.rocksdb.RocksDB;
import org.rocksdb.Options;
import org.rocksdb.RocksIterator;

public class RocksDBViewer {
    public static void main(String[] args) throws Exception {
        RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(false);
//        try (RocksDB db = RocksDB.open(options, "/path/to/rocksdb-directory")) {
//        try (RocksDB db = RocksDB.open(options, "/Users/kjalla/IdeaProjects/nexus/data/repository/local/rocks")) {
        try (RocksDB db = RocksDB.open(options, "../nexus/data/repository/local/rocks")) {
            try(RocksIterator iterator = db.newIterator()) {
                for(iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    System.out.println("Key: " + new String(iterator.key()) + ", Value: " + new String(iterator.value()));
                }
            }
//            db.newIterator().forEachRemaining(entry -> {
//                System.out.println("Key: " + new String(entry.getKey()) + ", Value: " + new String(entry.getValue()));
//            });
        }
    }
}
