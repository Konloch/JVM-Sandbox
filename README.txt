JVM Sandbox - Utilize Java Agents and ASM to manipulate then override Java SE classes.

Just a simple POC to block Runtime.exec for any Java jar, the final version is in https://github.com/Konloch/bytecode-viewer

This should work on all VMs on all OSs, if it doesn't please send an email to konloch@gmail.com with your OS and JVM information, thank you.

How to use this:
   0) Download the zip file containing the jar and .bat/.sh files.
   1) Click the .bat or .sh depending on your OS
   2) Import the jar you wish to sandbox/debug
   3) Enter the main and method class it'll invoke
   4) If you want to just test to see if it works, run the test.jar class.

Features:
    Blocks all Runtime.exec calls