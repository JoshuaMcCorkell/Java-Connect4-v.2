## Java-Connect4 v.2
This Java Project is a connect 4 program with a javax.swing GUI and a minimax algorithm with alpha-beta pruning *and* a transposition table (HashMap).
This is the second version and has some large improvements over the first version. 
The Connect 4 zip file contains all the files to use the project yourself, including the .exe file, .jar file, custom JRE image (for Java SE 17) and resources (works only on windows). Just download, extract the files and run `Connect 4.exe`.

*NOTE:* This project uses threads in conjunction with Java Swing, and therefore may  have thread problems on some systems (although this is unlikely as I worked very hard to make it thread safe). 
Using a SwingWorker would have been a bit safer, but I believe it is still thread-safe in it's current form.

See https://github.com/JoshuaMcCorkell/Java-Connect4-v.1 for version 1 (or on second thoughts, don't!).
