package org.unhcr.esafe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 *          Created 22 Jun 2018:00:27:22
 */

public final class XmlOutputParser {

	/**
	 * 
	 */
	public XmlOutputParser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, IOException {
		List<File> toProcess = new ArrayList<>();
		boolean toFile = false;
		for (String arg : args) {
			if (arg.equals("-h")) {
				usage();
				return;
			}
			if (arg.equals("-f")) {
				toFile = true;
			}
			File toTest = new File(arg);
			if (toTest.exists() && toTest.isFile()) {
				toProcess.add(toTest);
			}
		}

		if (toProcess.isEmpty()) {
			usage();
		}
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		EsafeXmlHandler handler = new EsafeXmlHandler(toFile);
		
		for (File toParse : toProcess) {
			handler.setCurrentFile(toParse);
			saxParser.parse(toParse, handler);
		}
	}

	private static void usage() {
		System.out.println("Please add the path of the file you'd like to transform.");
	}
}
