# Part of Willow by Tiger Sachse.

# Build the contents of Source and run, then remove class files.
cd Source
javac Willow/*.java &&
java Willow.GraphicalInterface &&
rm Willow/*.class
cd ..
