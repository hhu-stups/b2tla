package de.tlc4b;

public class TLC4BGlobals {
	private static int DEFERRED_SET_SIZE;
	private static int MAX_INT;
	private static int MIN_INT;

	private static boolean checkGOAL;
	private static boolean checkDeadlock;
	private static boolean checkInvariant;
	private static boolean checkLTL;
	private static boolean checkWD;
	private static boolean proBconstantsSetup;

	private static boolean deleteFilesOnExit;

	private static boolean runTLC;
	private static boolean translate;
	private static boolean hideTLCConsoleOutput;
	private static boolean createTraceFile;
	
	private static boolean testingMode;
	
	private static boolean cleanup;

	private static boolean forceTLCToEvalConstants;
	
	private static int workers;
	
	
	private static boolean runTestscript;

	static {
		resetGlobals();
	}

	public static void resetGlobals() {
		DEFERRED_SET_SIZE = 3;
		MAX_INT = 4;
		MIN_INT = -1;

		checkGOAL = true;
		checkDeadlock = true;
		checkInvariant = true;
		checkLTL = true;
		checkWD= false;
		
		setForceTLCToEvalConstants(true);
		
		setProBconstantsSetup(false);

		setCleanup(true);
		
		workers = 1;
		
		// for debugging purposes
		runTLC = true;
		translate = true;
		hideTLCConsoleOutput = false; // is mapped to TOOLIO.tool
		deleteFilesOnExit = false; // if enabled: deletes all created '.tla',
									// '.cfg' files on exit of the JVM. This
									// includes
									// the created B2TLA standard modules (e.g.
									// Relation, but not Naturals etc.).
		runTestscript = false;
		testingMode = false;
		createTraceFile = true;
	}

	public static boolean isCreateTraceFile() {
		return createTraceFile;
	}

	public static void setCreateTraceFile(boolean createTraceFile) {
		TLC4BGlobals.createTraceFile = createTraceFile;
	}

	public static boolean isRunTestscript() {
		return runTestscript;
	}

	public static void setRunTestscript(boolean runTestscript) {
		TLC4BGlobals.runTestscript = runTestscript;
	}

	public static int getDEFERRED_SET_SIZE() {
		return DEFERRED_SET_SIZE;
	}

	public static int getMAX_INT() {
		return MAX_INT;
	}

	public static int getMIN_INT() {
		return MIN_INT;
	}

	public static boolean isGOAL() {
		return checkGOAL;
	}

	public static boolean isDeadlockCheck() {
		return checkDeadlock;
	}

	public static boolean isRunTLC() {
		return runTLC;
	}

	public static boolean isTranslate() {
		return translate;
	}

	public static boolean isInvariant() {
		return checkInvariant;
	}

	public static boolean isCheckltl() {
		return checkLTL;
	}

	public static boolean isTool() {
		return hideTLCConsoleOutput;
	}

	public static boolean isDeleteOnExit() {
		return deleteFilesOnExit;
	}

	public static void setDEFERRED_SET_SIZE(int dEFERRED_SET_SIZE) {
		DEFERRED_SET_SIZE = dEFERRED_SET_SIZE;
	}

	public static void setMAX_INT(int mAX_INT) {
		MAX_INT = mAX_INT;
	}

	public static void setMIN_INT(int mIN_INT) {
		MIN_INT = mIN_INT;
	}

	public static void setGOAL(boolean gOAL) {
		checkGOAL = gOAL;
	}

	public static void setDeadlockCheck(boolean deadlockCheck) {
		TLC4BGlobals.checkDeadlock = deadlockCheck;
	}

	public static void setRunTLC(boolean runTLC) {
		TLC4BGlobals.runTLC = runTLC;
	}

	public static void setTranslate(boolean translate) {
		TLC4BGlobals.translate = translate;
	}

	public static void setInvariant(boolean invariant) {
		TLC4BGlobals.checkInvariant = invariant;
	}

	public static void setCheckltl(boolean checkltl) {
		TLC4BGlobals.checkLTL = checkltl;
	}

	public static void setTool(boolean tool) {
		TLC4BGlobals.hideTLCConsoleOutput = tool;
	}

	public static void setDeleteOnExit(boolean deleteOnExit) {
		TLC4BGlobals.deleteFilesOnExit = deleteOnExit;
	}

	public static void setWorkers(int w){
		TLC4BGlobals.workers = w;
	}
	
	public static int getWorkers(){
		return TLC4BGlobals.workers;
	}

	public static boolean isCleanup() {
		return cleanup;
	}

	public static void setCleanup(boolean cleanup) {
		TLC4BGlobals.cleanup = cleanup;
	}

	public static boolean isProBconstantsSetup() {
		return proBconstantsSetup;
	}

	public static void setProBconstantsSetup(boolean proBconstantsSetup) {
		TLC4BGlobals.proBconstantsSetup = proBconstantsSetup;
	}
	
	public static void setTestingMode(boolean b){
		TLC4BGlobals.testingMode = b;
	}
	
	public static boolean getTestingMode(){
		return TLC4BGlobals.testingMode;
	}
	
	public static void setWelldefinednessCheck(boolean b){
		TLC4BGlobals.checkWD = b;
	}
	
	public static boolean checkWelldefinedness(){
		return TLC4BGlobals.checkWD;
	}

	public static boolean isForceTLCToEvalConstants() {
		return forceTLCToEvalConstants;
	}

	public static void setForceTLCToEvalConstants(boolean forceTLCToEvalConstants) {
		TLC4BGlobals.forceTLCToEvalConstants = forceTLCToEvalConstants;
	}
	
}