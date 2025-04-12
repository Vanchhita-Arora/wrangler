package io.cdap.wrangler.parser;
import io.cdap.wrangler.api.parser.*;

public class DirectivesVisitorImpl extends DirectivesBaseVisitor<Token> {
    @Override
    public Token visitByteSizeValue(DirectivesParser.ByteSizeValueContext ctx){
        return new ByteSize(ctx.getText());
    }

    @Override
    public Token visitTimeDurationValue(DirectivesParser.TimeDurationValueContext ctx){
        return new TimeDuration(ctx.getText());
    }
}
