package ar.edu.itba.g5.client;

import com.opencsv.*;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AdminService;
import utils.CommandUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

public abstract class CSVUtils {
    public static CSVReader getReader(Reader reader) {
        return new CSVReaderBuilder(reader)
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build()
                )
                .build();
    }

    public static ICSVWriter getWriter(Writer writer) {
        return new CSVWriterBuilder(writer)
                .withSeparator(';')
                .build();
    }

    public static Reader getFileReader(String filepath) {
        return new InputStreamReader(ClassLoader.getSystemResourceAsStream(filepath));
    }

    public static Writer getFileWriter(String filepath) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(filepath));
    }
}
