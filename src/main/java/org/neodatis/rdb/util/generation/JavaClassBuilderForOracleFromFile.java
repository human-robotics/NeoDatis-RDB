package org.neodatis.rdb.util.generation;

/**
 * Description : Generates java file matching database tables all description
 * must be available in classes.properties file
 * 
 * @author : Olivier Smadja - osmadja@gmail.com
 * @date : date - 17/08.2000
 */

public class JavaClassBuilderForOracleFromFile extends Main {

	public JavaClassBuilderForOracleFromFile(String directory, String in_sFileName) {
		super(directory, in_sFileName, "oracle");
	}

	// Manages this table
	public static void main(String[] args) throws Exception {
		System.out.println("Nb args =" + args.length);
		if (args.length != 2) {
			displayHelpMessage();
		} else {
			JavaClassBuilderForOracleFromFile builder = new JavaClassBuilderForOracleFromFile(args[0], args[1]);
			if (builder.isOk()) {
				builder.manageTables();
			} else {
				System.out.println("Problem with builder");
			}

		}

	}
}
