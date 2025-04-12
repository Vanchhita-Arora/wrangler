package io.cdap.wrangler.executor;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.expression.EL;
import io.cdap.wrangler.expression.ELContext;
import io.cdap.wrangler.expression.ELException;
import io.cdap.wrangler.expression.ELResult;
import io.cdap.wrangler.parser.GrammarBasedParser;
// import io.cdap.wrangler.parser.TokenizedLine;
// import io.cdap.wrangler.parser.UsageDefinition;
import io.cdap.wrangler.utils.RowHelper;

import java.util.ArrayList;
import java.util.List;

public class AggregateStats implements Directive {
    public static final String NAME = "aggregate-stats";
    private String sizeColumn;
    private String timeColumn;
    private String outputSizeColumn;
    private String outputTimeColumn;
    private String sizeOutputUnit = "MB";
    private String timeOutputUnit = "s";
    private String aggregationType = "total";

    private long totalBytes = 0;
    private long totalNanos = 0;
    private int rowCount = 0;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
        builder.define("sizeColumn", TokenType.COLUMN_NAME);
        builder.define("timeColumn", TokenType.COLUMN_NAME);
        builder.define("outputSizeColumn", TokenType.COLUMN_NAME);
        builder.define("outputTimeColumn", TokenType.COLUMN_NAME);
        builder.define("sizeOutputUnit", TokenType.IDENTIFIER, true);
        builder.define("timeOutputUnit", TokenType.IDENTIFIER, true);
        builder.define("aggregationType", TokenType.IDENTIFIER, true);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.sizeColumn = ((ColumnName) args.value("sizeColumn")).value();
        this.timeColumn = ((ColumnName) args.value("timeColumn")).value();
        this.outputSizeColumn = ((ColumnName) args.value("outputSizeColumn")).value();
        this.outputTimeColumn = ((ColumnName) args.value("outputTimeColumn")).value();

        if (args.contains("sizeOutputUnit")) {
            this.sizeOutputUnit = ((Identifier) args.value("sizeOutputUnit")).value();
        }
        if (args.contains("timeOutputUnit")) {
            this.timeOutputUnit = ((Identifier) args.value("timeOutputUnit")).value();
        }
        if (args.contains("aggregationType")) {
            this.aggregationType = ((Identifier) args.value("aggregationType")).value();
        }
    }

    @Override
    public void destroy() {
        // no-op
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        for (Row row : rows) {
            Object sizeObj = row.getValue(sizeColumn);
            Object timeObj = row.getValue(timeColumn);

            if (sizeObj != null) {
                try {
                    ByteSize byteSize = new ByteSize(sizeObj.toString());
                    totalBytes += byteSize.getBytes();
                } catch (Exception e) {
                    throw new DirectiveExecutionException(
                        NAME, String.format("Invalid byte size value '%s' in column '%s'", sizeObj, sizeColumn), e);
                }
            }

            if (timeObj != null) {
                try {
                    TimeDuration timeDuration = new TimeDuration(timeObj.toString());
                    totalNanos += timeDuration.getNanoseconds();
                } catch (Exception e) {
                    throw new DirectiveExecutionException(
                        NAME, String.format("Invalid time duration value '%s' in column '%s'", timeObj, timeColumn), e);
                }
            }

            rowCount++;
        }

        // Return empty list during execution phase since we're aggregating
        return new ArrayList<>();
    }

    // @Override
    public List<Row> terminate() throws DirectiveExecutionException {
        Row row = new Row();
        
        // Calculate and convert size
        double outputSize;
        switch (sizeOutputUnit.toUpperCase()) {
            case "KB":
                outputSize = totalBytes / 1024.0;
                break;
            case "MB":
                outputSize = totalBytes / (1024.0 * 1024);
                break;
            case "GB":
                outputSize = totalBytes / (1024.0 * 1024 * 1024);
                break;
            default:
                outputSize = totalBytes;
                break;
        }

        // Calculate and convert time
        double outputTime;
        switch (timeOutputUnit.toLowerCase()) {
            case "ms":
                outputTime = totalNanos / 1_000_000.0;
                break;
            case "s":
                outputTime = totalNanos / 1_000_000_000.0;
                break;
            case "m":
                outputTime = totalNanos / (60 * 1_000_000_000.0);
                break;
            case "h":
                outputTime = totalNanos / (60 * 60 * 1_000_000_000.0);
                break;
            default:
                outputTime = totalNanos;
                break;
        }

        // Apply aggregation type
        if ("average".equalsIgnoreCase(aggregationType)) {
            outputSize = rowCount > 0 ? outputSize / rowCount : 0;
            outputTime = rowCount > 0 ? outputTime / rowCount : 0;
        }

        row.add(outputSizeColumn, outputSize);
        row.add(outputTimeColumn, outputTime);

        List<Row> result = new ArrayList<>();
        result.add(row);
        return result;
    }
}