# Build script for Willow by Tiger Sachse.

# If no name is provided, resort to a default.
if [ -z "$1" ]
then
    NAME="Willow"
else
    NAME=$1
fi

# Make the Build directory and copy everything we need.
2> /dev/null 1>&2 rm -r build/
mkdir build/
cp -r source/* build/
cp -r docs/ build/Documentation/

# Compile the source code and supporting material into a jar in the distribution folder.
cd build/
javac Willow/*.java &&
jar cvfm ../dist/$NAME.jar Willow/Manifest.txt Willow/* Styles/* Documentation/*
cd ..

# Clean up after yourself.
rm -r build/

# All done!
echo ""
echo "Willow packaged into jar located at dist/$NAME.jar"
