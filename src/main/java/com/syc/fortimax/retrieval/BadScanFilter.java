package com.syc.fortimax.retrieval;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.util.Base64;

public class BadScanFilter extends TokenFilter {

    //private final boolean ignoreCase;
    public static String inputFileName;

    /**
     * Construct a token stream filtering the given input.
     */
    public BadScanFilter(TokenStream input) {
        //this(input, false);
        super(input);
    }

    /**
     * Returns the next input Token whose termText() is not a stop word.
     */
    public final Token next() throws IOException {
        // return the first non-stop word found
        for (Token token = input.next(); token != null; token = input.next()) {
            String termText = token.termText();
            if (!isSameCharArray(termText))
                return token;
        }
        // reached EOS -- return null
        return null;
    }

    private boolean isSameCharArray(String tokenValue) {
        char[] c = new char[tokenValue.length()];
        Arrays.fill(c, 0, c.length, tokenValue.charAt(0));
        String strCompare = new String(c);
        return strCompare.equals(tokenValue);
    }
}
