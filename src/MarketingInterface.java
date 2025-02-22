import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

//TODO: Create transactions with RDMBS
public interface MarketingInterface {

    List<Films> getAllFilms() throws SQLException;

    interface DatabaseConnection {
        Connection getConnection() throws SQLException;
    }

    interface TransactionQuery {
        void completeTransaction(TransactionOperation a) throws SQLException;
    }

    @FunctionalInterface
    interface TransactionOperation {
        void execute(Connection c) throws SQLException;
    }
}