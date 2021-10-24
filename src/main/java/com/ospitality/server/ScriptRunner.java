package com.ospitality.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

class RuntimeSqlException extends RuntimeException {
    public RuntimeSqlException() {
    }

    public RuntimeSqlException(String message) {
        super(message);
    }

    public RuntimeSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeSqlException(Throwable cause) {
        super(cause);
    }
}

public class ScriptRunner {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private static final String DEFAULT_DELIMITER = ";";
    private Connection connection;
    private boolean stopOnError;
    private boolean autoCommit;
    private boolean sendFullScript;
    private PrintWriter logWriter;
    private PrintWriter errorLogWriter;
    private String delimiter;
    private boolean fullLineDelimiter;
    private String characterSetName;

    public ScriptRunner(Connection connection) {
        this.logWriter = new PrintWriter(System.out);
        this.errorLogWriter = new PrintWriter(System.err);
        this.delimiter = ";";
        this.fullLineDelimiter = false;
        this.connection = connection;
    }

    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setSendFullScript(boolean sendFullScript) {
        this.sendFullScript = sendFullScript;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setFullLineDelimiter(boolean fullLineDelimiter) {
        this.fullLineDelimiter = fullLineDelimiter;
    }

    public void runScript(Reader reader) {
        this.setAutoCommit();

        try {
            if (this.sendFullScript) {
                this.executeFullScript(reader);
            } else {
                this.executeLineByLine(reader);
            }
        } finally {
            this.rollbackConnection();
        }

    }

    private void executeFullScript(Reader reader) {
        StringBuffer script = new StringBuffer();

        String line;
        try {
            BufferedReader lineReader = new BufferedReader(reader);

            while((line = lineReader.readLine()) != null) {
                script.append(line);
                script.append(LINE_SEPARATOR);
            }

            this.executeStatement(script.toString());
            this.commitConnection();
        } catch (Exception var5) {
            line = "Error executing: " + script + ".  Cause: " + var5;
            this.printlnError(line);
            throw new RuntimeSqlException(line, var5);
        }
    }

    private void executeLineByLine(Reader reader) {
        StringBuffer command = new StringBuffer();

        String line;
        try {
            for(BufferedReader lineReader = new BufferedReader(reader); (line = lineReader.readLine()) != null; command = this.handleLine(command, line)) {
            }

            this.commitConnection();
            this.checkForMissingLineTerminator(command);
        } catch (Exception var5) {
            line = "Error executing: " + command + ".  Cause: " + var5;
            this.printlnError(line);
            throw new RuntimeSqlException(line, var5);
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (Exception var2) {
        }

    }

    private void setAutoCommit() {
        try {
            if (this.autoCommit != this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(this.autoCommit);
            }

        } catch (Throwable var2) {
            throw new RuntimeSqlException("Could not set AutoCommit to " + this.autoCommit + ". Cause: " + var2, var2);
        }
    }

    private void commitConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.commit();
            }

        } catch (Throwable var2) {
            throw new RuntimeSqlException("Could not commit transaction. Cause: " + var2, var2);
        }
    }

    private void rollbackConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.rollback();
            }
        } catch (Throwable var2) {
        }

    }

    private void checkForMissingLineTerminator(StringBuffer command) {
        if (command != null && command.toString().trim().length() > 0) {
            throw new RuntimeSqlException("Line missing end-of-line terminator (" + this.delimiter + ") => " + command);
        }
    }

    private StringBuffer handleLine(StringBuffer command, String line) throws SQLException, UnsupportedEncodingException {
        String trimmedLine = line.trim();
        if (this.lineIsComment(trimmedLine)) {
            this.println(trimmedLine);
        } else if (this.commandReadyToExecute(trimmedLine)) {
            command.append(line.substring(0, line.lastIndexOf(this.delimiter)));
            command.append(LINE_SEPARATOR);
            this.println(command);
            this.executeStatement(command.toString());
            command.setLength(0);
        } else if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }

        return command;
    }

    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        return !this.fullLineDelimiter && trimmedLine.endsWith(this.delimiter) || this.fullLineDelimiter && trimmedLine.equals(this.delimiter);
    }

    private void executeStatement(String command) throws SQLException, UnsupportedEncodingException {
        if (this.characterSetName != null) {
            command = new String(command.getBytes(), this.characterSetName);
        }

        boolean hasResults = false;
        Statement statement = this.connection.createStatement();
        if (this.stopOnError) {
            hasResults = statement.execute(command);
        } else {
            try {
                hasResults = statement.execute(command);
            } catch (SQLException var7) {
                String message = "Error executing: " + command + ".  Cause: " + var7;
                this.printlnError(message);
            }
        }

        this.printResults(statement, hasResults);

        try {
            statement.close();
        } catch (Exception var6) {
        }

        this.commitConnection();
    }

    private void printResults(Statement statement, boolean hasResults) {
        try {
            if (hasResults) {
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();

                    int i;
                    String value;
                    for(i = 0; i < cols; ++i) {
                        value = md.getColumnLabel(i + 1);
                        this.print(value + "\t");
                    }

                    this.println("");

                    while(rs.next()) {
                        for(i = 0; i < cols; ++i) {
                            value = rs.getString(i + 1);
                            this.print(value + "\t");
                        }

                        this.println("");
                    }
                }
            }
        } catch (SQLException var8) {
            this.printlnError("Error printing results: " + var8.getMessage());
        }

    }

    private void print(Object o) {
        if (this.logWriter != null) {
            this.logWriter.print(o);
            this.logWriter.flush();
        }

    }

    private void println(Object o) {
        if (this.logWriter != null) {
            this.logWriter.println(o);
            this.logWriter.flush();
        }

    }

    private void printlnError(Object o) {
        if (this.errorLogWriter != null) {
            this.errorLogWriter.println(o);
            this.errorLogWriter.flush();
        }

    }
}
