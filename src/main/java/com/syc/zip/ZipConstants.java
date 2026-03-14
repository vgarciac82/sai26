/*
 * @(#)ZipConstants.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.syc.zip;

import java.util.Base64;

/*
 * This interface defines the constants that are used by the classes
 * which manipulate ZIP files.
 *
 * @version	1.17, 01/23/03
 * @author	David Connelly
 */
interface ZipConstants {

    /*
	 * Header signatures
	 */
    // "PK\003\004"
    static long LOCSIG = 0x04034b50L;

    // "PK\007\008"
    static long EXTSIG = 0x08074b50L;

    // "PK\001\002"
    static long CENSIG = 0x02014b50L;

    // "PK\005\006"
    static long ENDSIG = 0x06054b50L;

    /*
	 * Header sizes in bytes (including signatures)
	 */
    // LOC header size
    static final int LOCHDR = 30;

    // EXT header size
    static final int EXTHDR = 16;

    // CEN header size
    static final int CENHDR = 46;

    // END header size
    static final int ENDHDR = 22;

    /*
	 * Local file (LOC) header field offsets
	 */
    // version needed to extract
    static final int LOCVER = 4;

    // general purpose bit flag
    static final int LOCFLG = 6;

    // compression method
    static final int LOCHOW = 8;

    // modification time
    static final int LOCTIM = 10;

    // uncompressed file crc-32 value
    static final int LOCCRC = 14;

    // compressed size
    static final int LOCSIZ = 18;

    // uncompressed size
    static final int LOCLEN = 22;

    // filename length
    static final int LOCNAM = 26;

    // extra field length
    static final int LOCEXT = 28;

    /*
	 * Extra local (EXT) header field offsets
	 */
    // uncompressed file crc-32 value
    static final int EXTCRC = 4;

    // compressed size
    static final int EXTSIZ = 8;

    // uncompressed size
    static final int EXTLEN = 12;

    /*
	 * Central directory (CEN) header field offsets
	 */
    // version made by
    static final int CENVEM = 4;

    // version needed to extract
    static final int CENVER = 6;

    // encrypt, decrypt flags
    static final int CENFLG = 8;

    // compression method
    static final int CENHOW = 10;

    // modification time
    static final int CENTIM = 12;

    // uncompressed file crc-32 value
    static final int CENCRC = 16;

    // compressed size
    static final int CENSIZ = 20;

    // uncompressed size
    static final int CENLEN = 24;

    // filename length
    static final int CENNAM = 28;

    // extra field length
    static final int CENEXT = 30;

    // comment length
    static final int CENCOM = 32;

    // disk number start
    static final int CENDSK = 34;

    // internal file attributes
    static final int CENATT = 36;

    // external file attributes
    static final int CENATX = 38;

    // LOC header offset
    static final int CENOFF = 42;

    /*
	 * End of central directory (END) header field offsets
	 */
    // number of entries on this disk
    static final int ENDSUB = 8;

    // total number of entries
    static final int ENDTOT = 10;

    // central directory size in bytes
    static final int ENDSIZ = 12;

    // offset of first CEN header
    static final int ENDOFF = 16;

    // zip file comment length
    static final int ENDCOM = 20;
}
