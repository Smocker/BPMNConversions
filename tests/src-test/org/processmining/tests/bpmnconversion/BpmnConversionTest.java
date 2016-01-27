package org.processmining.tests.bpmnconversion;
import org.junit.Test;
import org.processmining.contexts.cli.CLI;
import junit.framework.TestCase;

public class BpmnConversionTest extends TestCase {

//  @Test
//  public void testBPMNConversion_listPlugins() throws Throwable {
//    String args[] = new String[] {"-l"};
//    CLI.main(args);
//  }

  @Test
  public void testNewPackage2() throws Throwable {
    String testFileRoot = System.getProperty("test.testFileRoot", "./tests/testfiles");
    String args[] = new String[] {"-f", testFileRoot+"/NewPackage_Example.txt"};
    
    CLI.main(args);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(BpmnConversionTest.class);
  }
  
}
