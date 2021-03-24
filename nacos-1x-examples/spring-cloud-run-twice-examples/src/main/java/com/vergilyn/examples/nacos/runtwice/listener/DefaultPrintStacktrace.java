package com.vergilyn.examples.nacos.runtwice.listener;

public interface DefaultPrintStacktrace {

	default void printStacktrace(){

		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		for (int i = 1; i < stackTrace.length; i++) {
			System.out.printf("\t%d, %s\r\n", i - 1, stackTrace[i].toString());
		}

		System.out.println();
	}
}
