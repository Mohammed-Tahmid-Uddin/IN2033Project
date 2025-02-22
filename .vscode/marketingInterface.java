import java.sql.Connection;
import java.sql.SQLException;


public interface MarketingInterface {

    List<Films> getAllFilms() throws SQLException;

    
}

public interface DatabaseConnection {

    Connection getConnection() throws SQLException;
}

public interface TransactionQuery {

    void completeTransaction(TransactionOperation a) throws SQLException;
}

@FunctionalInterface
public interface TransactionOperation {
    void execute(Connection c) throws SQLException;   
}
