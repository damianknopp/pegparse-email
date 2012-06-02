package dmk.pegparser;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.parboiled.Parboiled;
import org.parboiled.common.StringUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

public class EmailRunner {

	public static void main(String[] args) {
		EmailParser parser = Parboiled.createParser(EmailParser.class);
		final String contents = fastStreamCopy("email1.txt");
		System.out.println("contents=" + contents);
		System.out.println("starting parse...");
		ParsingResult<?> result = new RecoveringParseRunner(parser
				.MessageChain()).run(contents);
		System.out.println("parse tree= " + result.parseTreeRoot.getValue()
				+ '\n');
		System.out.println(printNodeTree(result) + '\n');

		if (!result.matched) {
			System.out.println(StringUtils.join(result.parseErrors, "---\n"));
		}
	}

	/**
	 * Read the contents of a text file using a memory-mapped byte buffer.
	 * 
	 * A MappedByteBuffer, is simply a special ByteBuffer. MappedByteBuffer maps
	 * a region of a file directly in memory. Typically, that region comprises
	 * the entire file, although it could map a portion. You must, therefore,
	 * specify what part of the file to map. Moreover, as with the other Buffer
	 * objects, no constructor exists; you must ask the
	 * java.nio.channels.FileChannel for its map() method to get a
	 * MappedByteBuffer.
	 * 
	 * Direct buffers allocate their data directly in the runtime environment
	 * memory, bypassing the JVM|OS boundary, usually doubling file copy speed.
	 * However, they generally cost more to allocate.
	 */
	private static String fastStreamCopy(String filename) {
		String s = "";
		FileChannel fc = null;
		try {
			fc = new FileInputStream(filename).getChannel();

			// int length = (int)fc.size();

			MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY,
					0, fc.size());
			// CharBuffer charBuffer =
			// Charset.forName("ISO-8859-1").newDecoder().decode(byteBuffer);

			// ByteBuffer byteBuffer = ByteBuffer.allocate(length);
			// ByteBuffer byteBuffer = ByteBuffer.allocateDirect(length);
			// CharBuffer charBuffer = byteBuffer.asCharBuffer();

			// CharBuffer charBuffer =
			// ByteBuffer.allocateDirect(length).asCharBuffer();
			/*
			 * int size = charBuffer.length(); if (size > 0) { StringBuffer sb =
			 * new StringBuffer(size); for (int count=0; count<size; count++)
			 * sb.append(charBuffer.get()); s = sb.toString(); }
			 * 
			 * if (length > 0) { StringBuffer sb = new StringBuffer(length); for
			 * (int count=0; count<length; count++) {
			 * sb.append(byteBuffer.get()); } s = sb.toString(); }
			 */
			int size = byteBuffer.capacity();
			if (size > 0) {
				// Retrieve all bytes in the buffer
				byteBuffer.clear();
				byte[] bytes = new byte[size];
				byteBuffer.get(bytes, 0, bytes.length);
				s = new String(bytes);
			}

			fc.close();
		} catch (FileNotFoundException fnfx) {
			System.err.println("File not found: " + fnfx);
		} catch (IOException iox) {
			System.err.println("I/O problems: " + iox);
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (IOException ignore) {
					// ignore
				}
			}
		}
		return s;
	}

}