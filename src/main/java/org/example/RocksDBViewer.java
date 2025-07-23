import org.rocksdb.RocksDB;
import org.rocksdb.Options;

public class RocksDBViewer {
    public static void main(String[] args) throws Exception {
        RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(false);
        try (RocksDB db = RocksDB.open(options, "/path/to/rocksdb-directory")) {
            db.newIterator().forEachRemaining(entry -> {
                System.out.println("Key: " + new String(entry.getKey()) + ", Value: " + new String(entry.getValue()));
            });
        }
    }
}
